<template>
  <div class="restaurant-card-skeleton" aria-hidden="true">
    <div class="skeleton-image"></div>
    <div class="skeleton-content">
      <div class="skeleton-line name"></div>
      <div class="skeleton-line category"></div>
      <div class="skeleton-line info"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.restaurant-card-skeleton {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  animation: pulse 1.5s infinite ease-in-out;
  border: 1px solid var(--color-border);

  .skeleton-image {
    width: 100%;
    height: 150px;
    background-color: var(--color-background);
  }

  .skeleton-content {
    padding: var(--spacing-md);
  }

  .skeleton-line {
    background-color: var(--color-background);
    border-radius: var(--radius-sm);
    margin-bottom: var(--spacing-sm);

    &.name { width: 70%; height: 20px; }
    &.category { width: 50%; height: 16px; }
    &.info { width: 100%; height: 16px; }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
