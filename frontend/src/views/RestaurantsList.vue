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
import { useNotifications } from '../composables/useNotifications';
import api from '../plugins/axios';
import RestaurantCard from '../components/RestaurantCard.vue';
import RestaurantCardSkeleton from '../components/RestaurantCardSkeleton.vue';
import EmptyState from '../components/base/EmptyState.vue';

const { loading, execute } = useApi();
const { addNotification } = useNotifications();
const restaurants = ref([]);

onMounted(async () => {
  try {
    const response = await execute(() => api.get('/estabelecimentos'));
    restaurants.value = response.data;
  } catch (error) {
    addNotification({
      type: 'error',
      message: 'Erro ao buscar restaurantes. Por favor, tente novamente mais tarde.',
    });
    console.error('Erro ao buscar restaurantes:', error);
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
