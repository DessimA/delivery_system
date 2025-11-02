<template>
  <div class="restaurant-detail-page">
    <div v-if="loading" class="loading-state">
      <LoadingSpinner />
    </div>

    <div v-else-if="restaurant" class="restaurant-content">
      <div class="restaurant-header">
        <h1 class="restaurant-name">{{ restaurant.nome }}</h1>
        <p class="restaurant-address">{{ restaurant.endereco }}</p>
      </div>

      <div v-if="products.length > 0" class="products-grid">
        <ProductCard
          v-for="product in products"
          :key="product.idProduto"
          :product="product"
          @add-to-cart="addToCart(product)"
        />
      </div>

      <EmptyState
        v-else
        title="Nenhum produto disponível"
        description="Este restaurante ainda não possui produtos cadastrados."
        icon="box"
      />
    </div>

    <div v-else class="error-state">
      <p>Restaurante não encontrado.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { useApi } from '../composables/useApi';
import { useCartStore } from '../stores/cart';
import ProductCard from '../components/product/ProductCard.vue';
import LoadingSpinner from '../components/base/LoadingSpinner.vue';
import EmptyState from '../components/base/EmptyState.vue';

const route = useRoute();
const api = useApi();
const cartStore = useCartStore();

const restaurant = ref(null);
const products = ref([]);
const loading = ref(true);

onMounted(async () => {
  const restaurantId = route.params.id;
  try {
    const { data: restaurantData } = await api.get(`/estabelecimentos/${restaurantId}`);
    restaurant.value = restaurantData;

    const { data: productsData } = await api.get(`/estabelecimentos/${restaurantId}/produtos`);
    products.value = productsData;
  } catch (error) {
    console.error('Erro ao buscar detalhes do restaurante:', error);
  } finally {
    loading.value = false;
  }
});

const addToCart = (product) => {
  cartStore.addToCart(product);
};
</script>

<style lang="scss" scoped>
.restaurant-detail-page {
  padding: 2rem;
}

.loading-state, .error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
}

.restaurant-header {
  margin-bottom: 2rem;
}

.restaurant-name {
  font-size: 2.5rem;
  font-weight: bold;
}

.restaurant-address {
  font-size: 1.2rem;
  color: #666;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}
</style>