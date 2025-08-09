<template>
  <div class="container mt-5">
    <h1 class="mb-4">Seu Carrinho</h1>
    <div v-if="cartItems.length === 0" class="alert alert-info">Seu carrinho está vazio.</div>
    <div v-else>
      <ul class="list-group mb-4">
        <li class="list-group-item d-flex justify-content-between align-items-center" v-for="item in cartItems" :key="item.idProduto">
          {{ item.nomeProduto }} - R$ {{ item.preco.toFixed(2) }}
          <button @click="removeFromCart(item.idProduto)" class="btn btn-danger btn-sm">Remover</button>
        </li>
      </ul>
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h4>Total: R$ {{ cartTotal.toFixed(2) }}</h4>
        <button @click="checkout" class="btn btn-success" :disabled="!isLoggedIn">Finalizar Pedido</button>
      </div>
      <div v-if="!isLoggedIn" class="alert alert-warning">Faça login para finalizar seu pedido.</div>
      <div v-if="orderError" class="alert alert-danger">{{ orderError }}</div>
      <div v-if="orderSuccess" class="alert alert-success">{{ orderSuccess }}</div>
    </div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  name: 'AppCart',
  data() {
    return {
      cartItems: [],
      orderError: null,
      orderSuccess: null,
      isLoggedIn: false,
    };
  },
  computed: {
    cartTotal() {
      return this.cartItems.reduce((sum, item) => sum + item.preco, 0);
    },
  },
  created() {
    this.loadCart();
    this.checkLoginStatus();
    window.addEventListener('storage', this.loadCart); // Reage a mudanças no localStorage
    window.addEventListener('storage', this.checkLoginStatus);
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.loadCart);
    window.removeEventListener('storage', this.checkLoginStatus);
  },
  methods: {
    loadCart() {
      this.cartItems = JSON.parse(localStorage.getItem('cart') || '[]');
    },
    removeFromCart(productId) {
      this.cartItems = this.cartItems.filter(item => item.idProduto !== productId);
      localStorage.setItem('cart', JSON.stringify(this.cartItems));
    },
    checkLoginStatus() {
      this.isLoggedIn = !!localStorage.getItem('authToken');
    },
    async checkout() {
      if (!this.isLoggedIn) {
        this.orderError = 'Você precisa estar logado para finalizar o pedido.';
        return;
      }

      if (this.cartItems.length === 0) {
        this.orderError = 'Seu carrinho está vazio.';
        return;
      }

      try {
        this.orderError = null;
        this.orderSuccess = null;

        // Para simplificar, vamos usar um endereço fixo. Em um app real, o usuário informaria.
        const enderecoPedido = 'Endereço de Entrega Fictício';
        const produtoIds = this.cartItems.map(item => item.idProduto);

        const orderData = {
          enderecoPedido: enderecoPedido,
          produtoIds: produtoIds,
        };

        await api.post('/pedidos', orderData);
        this.orderSuccess = 'Pedido finalizado com sucesso!';
        localStorage.removeItem('cart'); // Limpa o carrinho
        this.loadCart(); // Recarrega o carrinho (agora vazio)
      } catch (err) {
        this.orderError = 'Erro ao finalizar pedido. Tente novamente.';
        console.error('Erro ao finalizar pedido:', err.response ? err.response.data : err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos para a página do Carrinho */
</style>