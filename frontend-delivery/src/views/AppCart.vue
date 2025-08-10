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
      </div>

      <div class="card mb-4">
        <div class="card-header">Informações de Pagamento</div>
        <div class="card-body">
          <form @submit.prevent="processPayment">
            <div class="mb-3">
              <label for="metodoPagamento" class="form-label">Método de Pagamento</label>
              <select class="form-select" id="metodoPagamento" v-model="paymentMethod" required>
                <option value="CREDIT_CARD">Cartão de Crédito</option>
                <option value="DEBIT_CARD">Cartão de Débito</option>
                <option value="PIX">PIX</option>
              </select>
            </div>
            <div class="mb-3">
              <label for="numeroCartao" class="form-label">Número do Cartão (apenas simulação)</label>
              <input type="text" class="form-control" id="numeroCartao" v-model="cardNumber" placeholder="Apenas simulação: use '1111' para recusar" required>
            </div>
            <div v-if="paymentError" class="alert alert-danger">{{ paymentError }}</div>
            <button type="submit" class="btn btn-primary" :disabled="!isLoggedIn || cartItems.length === 0">Processar Pagamento</button>
          </form>
        </div>
      </div>

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
      paymentMethod: 'CREDIT_CARD',
      cardNumber: '',
      paymentError: null,
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
    async processPayment() {
      if (!this.isLoggedIn) {
        this.paymentError = 'Você precisa estar logado para processar o pagamento.';
        return;
      }

      if (this.cartItems.length === 0) {
        this.paymentError = 'Seu carrinho está vazio.';
        return;
      }

      this.paymentError = null;
      this.orderError = null;
      this.orderSuccess = null;

      try {
        // Primeiro, cria o pedido
        const enderecoPedido = 'Endereço de Entrega Fictício'; // Simplificado
        const produtoIds = this.cartItems.map(item => item.idProduto);

        const orderData = {
          enderecoPedido: enderecoPedido,
          produtoIds: produtoIds,
        };

        const orderResponse = await api.post('/pedidos', orderData);
        const pedidoId = orderResponse.data.codigoPedido;

        // Em seguida, processa o pagamento para o pedido recém-criado
        const paymentData = {
          codigoPedido: pedidoId,
          metodoPagamento: this.paymentMethod,
          valor: this.cartTotal,
          numeroCartao: this.cardNumber, // Apenas para simulação
        };

        const paymentResponse = await api.post('/pagamentos/processar', paymentData);

        if (paymentResponse.data.status === 'APROVADO') {
          this.orderSuccess = 'Pagamento aprovado e pedido finalizado com sucesso!';
          localStorage.removeItem('cart');
          this.loadCart();
        } else {
          this.paymentError = `Pagamento ${paymentResponse.data.status.toLowerCase()}. Tente novamente ou use outro cartão.`;
        }

      } catch (err) {
        this.paymentError = 'Erro ao processar pagamento. Verifique os dados e tente novamente.';
        this.orderError = 'Erro ao finalizar pedido. Tente novamente.';
        console.error('Erro no pagamento/pedido:', err.response ? err.response.data : err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos para a página do Carrinho */
</style>