<template>
  <div class="restaurant-card">
    <div class="restaurant-card__image">
      <img :src="restaurant.logoUrl || '/images/placeholder-food.svg'" :alt="restaurant.nome">
    </div>
    <div class="restaurant-card__content">
      <h3 class="restaurant-card__name">{{ restaurant.nome }}</h3>
      <p class="restaurant-card__category">{{ restaurant.category }}</p>
      <div class="restaurant-card__info">
        <span class="restaurant-card__rating">{{ restaurant.rating }}</span>
        <span class="restaurant-card__time">{{ restaurant.deliveryTime }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  restaurant: {
    type: Object,
    required: true
  }
});
</script>

<style lang="scss" scoped>
.restaurant-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-5px);
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
    padding: 16px;
  }

  &__name {
    font-size: 1.2rem;
    font-weight: bold;
    margin: 0 0 8px;
  }

  &__category {
    color: #666;
    margin: 0 0 16px;
  }

  &__info {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__rating {
    color: #f5a623;
    font-weight: bold;
  }

  &__time {
    color: #666;
  }
}
</style>
