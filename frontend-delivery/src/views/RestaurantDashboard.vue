<template>
  <div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Meu Cardápio</h1>
      <button class="btn btn-primary">Adicionar Produto</button>
    </div>

    <div v-if="loading" class="text-center">Carregando produtos...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <table v-if="products.length" class="table table-striped">
      <thead>
        <tr>
          <th scope="col">Nome</th>
          <th scope="col">Descrição</th>
          <th scope="col">Preço</th>
          <th scope="col">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in products" :key="product.idProduto">
          <td>{{ product.nomeProduto }}</td>
          <td>{{ product.descricao }}</td>
          <td>R$ {{ product.preco.toFixed(2) }}</td>
          <td>
            <button class="btn btn-sm btn-secondary me-2">Editar</button>
            <button class="btn btn-sm btn-danger">Excluir</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else-if="!loading" class="alert alert-info">Nenhum produto encontrado.</div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  name: 'RestaurantDashboard',
  data() {
    return {
      products: [],
      loading: true,
      error: null,
    };
  },
  async created() {
    try {
      const response = await api.get('/restaurante/me/produtos');
      this.products = response.data;
    } catch (err) {
      this.error = 'Erro ao carregar os produtos do seu restaurante.';
      console.error(err);
    } finally {
      this.loading = false;
    }
  },
};
</script>

<style scoped>
/* Estilos específicos, se necessário */
</style>
