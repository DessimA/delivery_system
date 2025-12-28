<template>
  <div class="restaurant-card" role="article" :aria-label="restaurant.name">
    <div class="restaurant-card__image">
      <img :src="restaurant.logoUrl || '/images/placeholder-food.svg'" :alt="restaurant.name" loading="lazy">
    </div>
    <div class="restaurant-card__content">
      <h3 class="restaurant-card__name">{{ restaurant.name }}</h3>
      <p class="restaurant-card__category">{{ restaurant.category }}</p>
      <div class="restaurant-card__info">
        <span class="restaurant-card__rating" v-if="restaurant.rating">{{ restaurant.rating }}</span>
        <span class="restaurant-card__time" v-if="restaurant.deliveryTime">{{ restaurant.deliveryTime }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
  restaurant: {
    type: Object,
    required: true,
    validator: (r) => !!r.id && !!r.name
  }
});
</script>

<style lang="scss" scoped>
.restaurant-card {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  transition: all var(--transition-fast);
  border: 1px solid var(--color-border);

  &:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-md);
  }

  &__image {
    width: 100%;
    height: 150px;
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  &__content {
    padding: var(--spacing-md);
  }

  &__name {
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-bold);
    margin: 0 0 var(--spacing-xs);
    color: var(--color-text-dark);
  }

  &__category {
    color: var(--color-text-muted);
    margin: 0 0 var(--spacing-md);
    font-size: var(--font-size-sm);
  }

  &__info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: var(--font-size-sm);
  }

  &__rating {
    color: #f5a623;
    font-weight: bold;
  }

  &__time {
    color: var(--color-text-muted);
  }
}
</style>
