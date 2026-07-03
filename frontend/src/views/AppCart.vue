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
          :key="item.id"
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
            :loading="processing"
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
      @action="$router.push('/restaurants')"
    />

    <!-- Checkout Modal -->
    <BaseModal v-model="isCheckoutModalOpen" title="Finalizar Pedido">
        <form @submit.prevent="handlePlaceOrder" class="checkout-form">
          <div>
            <BaseInput id="enderecoPedido" v-model="checkoutForm.deliveryAddress" label="Endereço de Entrega" placeholder="Digite seu endereço completo" />
          </div>
        </form>
        <template #footer>
          <div>
            <BaseButton label="Cancelar" variant="secondary" @click="isCheckoutModalOpen = false" />
            <BaseButton label="Pagar com PIX" :loading="processing" @click="handlePlaceOrder" />
          </div>
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
import { orderService } from '@/services/order.service';

import BaseButton from '@/components/base/BaseButton.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import CartItemCard from '@/components/features/cart/CartItemCard.vue';
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
  deliveryAddress: '',
});

onMounted(() => {
  if (authStore.user?.address) {
    checkoutForm.deliveryAddress = authStore.user.address;
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
  if (!checkoutForm.deliveryAddress) {
    addNotification({ type: 'warning', message: 'Por favor, preencha o endereço de entrega.' });
    return;
  }

  try {
    await execute(async () => {
      const pedidoPayload = {
        deliveryAddress: checkoutForm.deliveryAddress,
        items: cartStore.items.map(item => ({
          productId: item.id,
          quantity: item.quantity || 1
        })),
      };
      
      const novoPedido = await orderService.create(pedidoPayload);

      addNotification({ type: 'success', message: 'Pedido criado com sucesso! Redirecionando para o pagamento...' });
      cartStore.clearCart();
      isCheckoutModalOpen.value = false;
      router.push({ name: 'CheckoutPix', query: { pedidoId: novoPedido.id, valor: novoPedido.total } });
    });
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao finalizar o pedido. Por favor, tente novamente.' });
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);

  h1 {
    font-size: var(--font-size-h2);
    margin: 0;
    color: var(--color-text-dark);
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-text-muted);
  }
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--spacing-xl);

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
  .summary-card {
    background-color: var(--color-surface);
    border-radius: var(--radius-lg);
    padding: var(--spacing-lg);
    box-shadow: var(--shadow-md);
    display: flex;
    flex-direction: column;
    gap: var(--spacing-md);

    .summary-row {
      display: flex;
      justify-content: space-between;
      font-size: var(--font-size-md);
      color: var(--color-text-dark);

      &.total {
        font-size: var(--font-size-lg);
        font-weight: bold;
        border-top: 1px solid var(--color-border);
        padding-top: var(--spacing-md);
        margin-top: var(--spacing-sm);
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