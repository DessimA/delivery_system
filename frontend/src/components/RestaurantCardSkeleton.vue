<template>
  <div class="restaurant-card-skeleton">
    <div class="restaurant-card-skeleton__image"></div>
    <div class="restaurant-card-skeleton__content">
      <div class="restaurant-card-skeleton__name"></div>
      <div class="restaurant-card-skeleton__category"></div>
      <div class="restaurant-card-skeleton__info"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.restaurant-card-skeleton {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;

  &__image {
    width: 100%;
    height: 150px;
    background-color: #f0f0f0;
  }

  &__content {
    padding: 16px;
  }

  &__name {
    width: 70%;
    height: 20px;
    background-color: #f0f0f0;
    margin-bottom: 8px;
  }

  &__category {
    width: 50%;
    height: 16px;
    background-color: #f0f0f0;
    margin-bottom: 16px;
  }

  &__info {
    width: 100%;
    height: 16px;
    background-color: #f0f0f0;
  }
}
</style>
