<template>
  <div class="container mt-5">
    <h1 class="mb-4">Nossos Produtos</h1>
    <div v-if="loading" class="text-center">Carregando produtos...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>
    <div class="row">
      <div class="col-md-4 mb-4" v-for="product in products" :key="product.idProduto">
        <div class="card h-100">
          <img :src="getProductImageUrl(product.caminhoImagem)" class="card-img-top" :alt="product.nomeProduto">
          <div class="card-body d-flex flex-column">
            <h5 class="card-title">{{ product.nomeProduto }}</h5>
            <p class="card-text flex-grow-1">{{ product.descricao }}</p>
            <p class="card-text"><strong>R$ {{ product.preco.toFixed(2) }}</strong></p>
            <button @click="addToCart(product)" class="btn btn-primary mt-auto">Adicionar ao Carrinho</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  name: 'AppProducts',
  data() {
    return {
      products: [],
      loading: true,
      error: null,
    };
  },
  created() {
    this.fetchProducts();
  },
  methods: {
    async fetchProducts() {
      try {
        const response = await api.get('/produtos');
        this.products = response.data;
      } catch (err) {
        this.error = 'Erro ao carregar produtos.';
        console.error('Erro ao buscar produtos:', err);
      } finally {
        this.loading = false;
      }
    },
    getProductImageUrl(imagePath) {
      // Assumindo que as imagens são servidas de um endpoint específico ou do mesmo backend
      // Ajuste esta URL conforme a configuração do seu servidor de imagens
      return `http://backend-app:8080/images/${imagePath}`;
    },
    addToCart(product) {
      let cart = JSON.parse(localStorage.getItem('cart') || '[]');
      cart.push(product);
      localStorage.setItem('cart', JSON.stringify(cart));
      alert(`${product.nomeProduto} adicionado ao carrinho!`);
    },
  },
};
</script>

<style scoped>
.card-img-top {
  max-height: 180px;
  object-fit: cover;
}
</style>