<template>
  <div class="product-card-skeleton">
    <div class="skeleton-image"></div>
    <div class="skeleton-info">
      <div class="skeleton-line short"></div>
      <div class="skeleton-line medium"></div>
      <div class="skeleton-line long"></div>
      <div class="skeleton-footer">
        <div class="skeleton-line price"></div>
        <div class="skeleton-button"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
// No script needed for skeleton
</script>

<style lang="scss" scoped>
.product-card-skeleton {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-image {
  width: 100%;
  height: 180px;
  background-color: var(--color-surface);
}

.skeleton-info {
  padding: var(--spacing-md);
}

.skeleton-line {
  height: 16px;
  background-color: var(--color-surface);
  border-radius: var(--radius-sm);
  margin-bottom: var(--spacing-sm);

  &.short {
    width: 70%;
  }
  &.medium {
    width: 90%;
  }
  &.long {
    width: 60%;
  }
}

.skeleton-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-md);

  .skeleton-line.price {
    width: 40%;
    height: 24px;
  }

  .skeleton-button {
    width: 40px;
    height: 40px;
    background-color: var(--color-surface);
    border-radius: var(--radius-md);
  }
}

@keyframes pulse {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
}
</style>
