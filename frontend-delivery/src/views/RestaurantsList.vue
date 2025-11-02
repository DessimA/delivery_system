<template>
  <div class="restaurants-list-page">
    <h1 class="page-title">Restaurantes</h1>
    <div v-if="loading" class="restaurants-grid">
      <RestaurantCardSkeleton v-for="n in 6" :key="n" />
    </div>
    <div v-else-if="restaurants.length > 0" class="restaurants-grid">
      <router-link v-for="restaurant in restaurants" :key="restaurant.id" :to="`/restaurants/${restaurant.id}`">
        <RestaurantCard :restaurant="restaurant" />
      </router-link>
    </div>
    <EmptyState
      v-else
      title="Nenhum restaurante encontrado"
      description="Não há restaurantes disponíveis no momento."
      icon="store"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useApi } from '../composables/useApi';
import RestaurantCard from '../components/RestaurantCard.vue';
import RestaurantCardSkeleton from '../components/RestaurantCardSkeleton.vue';
import EmptyState from '../components/base/EmptyState.vue';

const api = useApi();
const restaurants = ref([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const { data } = await api.get('/estabelecimentos');
    restaurants.value = data;
  } catch (error) {
    console.error('Erro ao buscar restaurantes:', error);
  } finally {
    loading.value = false;
  }
});
</script>

<style lang="scss" scoped>
.restaurants-list-page {
  padding: 2rem;
}

.page-title {
  margin-bottom: 2rem;
}

.restaurants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}
</style>
