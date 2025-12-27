<template>
  <div class="order-card-skeleton">
    <div class="skeleton-header">
      <div class="skeleton-line short"></div>
      <div class="skeleton-line status"></div>
    </div>
    <div class="skeleton-details">
      <div class="skeleton-line medium"></div>
      <div class="skeleton-line medium"></div>
      <div class="skeleton-line long"></div>
      <div class="skeleton-line long"></div>
    </div>
    <div class="skeleton-actions">
      <div class="skeleton-button"></div>
      <div class="skeleton-button"></div>
    </div>
  </div>
</template>

<script setup>
// No script needed for skeleton
</script>

<style lang="scss" scoped>
.order-card-skeleton {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  animation: pulse 1.5s infinite ease-in-out;
}

.skeleton-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-surface);
  padding-bottom: var(--spacing-sm);
}

.skeleton-line {
  background-color: var(--color-surface);
  border-radius: var(--radius-sm);
  height: 16px;
  margin-bottom: var(--spacing-sm);

  &.short {
    width: 30%;
  }
  &.medium {
    width: 60%;
  }
  &.long {
    width: 80%;
  }
  &.status {
    width: 20%;
    height: 24px;
  }
}

.skeleton-details {
  margin-bottom: var(--spacing-md);
}

.skeleton-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
  border-top: 1px solid var(--color-surface);
  padding-top: var(--spacing-md);

  .skeleton-button {
    width: 100px;
    height: 36px;
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
