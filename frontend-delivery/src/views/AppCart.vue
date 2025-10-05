<template>
  <div class="cart-container">
    <div class="cart-header">
      <h1>Seu Carrinho</h1>
      <p v-if="cartStore.itemCount > 0">{{ cartStore.itemCount }} {{ cartStore.itemCount === 1 ? 'item' : 'itens' }}</p>
    </div>

    <div v-if="cartStore.itemCount > 0" class="cart-content">
      <div class="cart-items">
        <CartItemCard
          v-for="item in cartStore.items"
          :key="item.idProduto"
          :item="item"
          @update-quantity="updateQuantity"
          @remove-item="removeItem"
        />
      </div>

      <div class="cart-summary">
        <div class="summary-card">
          <div class="summary-row">
            <span>Subtotal</span>
            <span>{{ formatCurrency(cartStore.total) }}</span>
          </div>
          <div class="summary-row">
            <span>Taxa de entrega</span>
            <span>{{ formatCurrency(deliveryFee) }}</span>
          </div>
          <div class="summary-row total">
            <span>Total</span>
            <span>{{ formatCurrency(cartStore.total + deliveryFee) }}</span>
          </div>

          <BaseButton
            label="Finalizar Pedido"
            variant="primary"
            size="lg"
            block
            @click="startCheckout"
          />
        </div>
      </div>
    </div>

    <EmptyState
      v-else
      title="Carrinho vazio"
      description="Adicione produtos para continuar"
      icon="shopping-cart"
      action-label="Ver Produtos"
      @action="$router.push('/')"
    />

    <!-- Checkout Modal -->
    <BaseModal v-model="isCheckoutModalOpen" title="Finalizar Pedido">
      <form @submit.prevent="handlePlaceOrder" class="checkout-form">
        <BaseInput v-model="checkoutForm.enderecoPedido" label="Endereço de Entrega" placeholder="Digite seu endereço completo" />
        <hr />
        <h4>Pagamento</h4>
        <BaseInput v-model="checkoutForm.numeroCartao" label="Número do Cartão" placeholder="0000 0000 0000 0000" />
      </form>
      <template #footer>
        <BaseButton label="Cancelar" variant="secondary" @click="isCheckoutModalOpen = false" />
        <BaseButton label="Pagar" :loading="processing" @click="handlePlaceOrder" />
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '@/stores/cart';
import { useAuthStore } from '@/stores/auth';
import { useNotifications } from '@/composables/useNotifications';
import { useApi } from '@/composables/useApi';
import api from '@/api';

import BaseButton from '@/components/base/BaseButton.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import CartItemCard from '@/components/cart/CartItemCard.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';

const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();
const { addNotification } = useNotifications();
const { loading: processing, execute } = useApi();

const deliveryFee = ref(5.00); // Placeholder
const isCheckoutModalOpen = ref(false);

const checkoutForm = reactive({
  enderecoPedido: '',
  numeroCartao: '',
});

onMounted(() => {
  if (authStore.user?.endereco) {
    checkoutForm.enderecoPedido = authStore.user.endereco;
  }
});

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};

const updateQuantity = (productId, quantity) => {
  cartStore.updateQuantity(productId, quantity);
};

const removeItem = (productId) => {
  cartStore.removeItem(productId);
  addNotification({ type: 'info', message: 'Item removido do carrinho.' });
};

const startCheckout = () => {
  isCheckoutModalOpen.value = true;
};

const handlePlaceOrder = async () => {
  if (!checkoutForm.enderecoPedido || !checkoutForm.numeroCartao) {
    addNotification({ type: 'warning', message: 'Por favor, preencha o endereço e os dados de pagamento.' });
    return;
  }

  try {
    await execute(async () => {
      // 1. Create Order
      const pedidoPayload = {
        enderecoPedido: checkoutForm.enderecoPedido,
        produtoIds: cartStore.items.map(item => item.idProduto),
      };
      const pedidoResponse = await api.post('/pedidos', pedidoPayload);
      const novoPedido = pedidoResponse.data;

      // 2. Process Payment
      const pagamentoPayload = {
        codigoPedido: novoPedido.codigoPedido,
        numeroCartao: checkoutForm.numeroCartao,
      };
      await api.post('/pagamentos/processar', pagamentoPayload);

      // 3. On Success
      addNotification({ type: 'success', message: 'Pedido realizado com sucesso!' });
      cartStore.clearCart();
      isCheckoutModalOpen.value = false;
      router.push('/orders');
    });
  } catch (error) {
    // Global interceptor in api.js will show the error notification
    console.error('Failed to place order:', error);
  }
};
</script>

<style lang="scss" scoped>
.cart-container {
  padding: var(--spacing-lg) var(--spacing-md);
  max-width: 1000px;
  margin: 0 auto;
}

.cart-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);

  h1 {
    font-size: var(--font-size-h2);
    margin-bottom: var(--spacing-xs);
    color: var(--color-text-dark);
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-dark);
  }
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-lg);

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.cart-summary {
  position: sticky;
  top: var(--spacing-lg);

  .summary-card {
    background-color: var(--color-background);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-md);
    padding: var(--spacing-lg);

    .summary-row {
      display: flex;
      justify-content: space-between;
      margin-bottom: var(--spacing-sm);
      font-size: var(--font-size-md);
      color: var(--color-text-dark);

      &.total {
        font-size: var(--font-size-lg);
        font-weight: var(--font-weight-bold);
        color: var(--color-primary);
        border-top: 1px solid var(--color-border);
        padding-top: var(--spacing-sm);
        margin-top: var(--spacing-md);
      }
    }
  }
}

.checkout-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
</style>