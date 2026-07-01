<template>
  <div class="order-card-skeleton" aria-hidden="true">
    <div class="skeleton-header">
      <div class="skeleton-line id"></div>
      <div class="skeleton-badge"></div>
    </div>
    <div class="skeleton-content">
      <div class="skeleton-line item"></div>
      <div class="skeleton-line address"></div>
    </div>
    <div class="skeleton-footer">
      <div class="skeleton-line price"></div>
      <div class="skeleton-buttons"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.order-card-skeleton {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  padding: var(--spacing-lg);
  animation: pulse 1.5s infinite ease-in-out;

  .skeleton-line {
    background-color: var(--color-background);
    border-radius: var(--radius-sm);
    margin-bottom: var(--spacing-sm);
  }

  .skeleton-header {
    display: flex;
    justify-content: space-between;
    margin-bottom: var(--spacing-md);
    .id { width: 40%; height: 24px; }
    .skeleton-badge { width: 80px; height: 24px; background-color: var(--color-background); border-radius: var(--radius-full); }
  }

  .skeleton-content {
    margin-bottom: var(--spacing-lg);
    .item { width: 60%; height: 18px; }
    .address { width: 80%; height: 16px; }
  }

  .skeleton-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .price { width: 30%; height: 24px; margin: 0; }
    .skeleton-buttons { width: 150px; height: 32px; background-color: var(--color-background); border-radius: var(--radius-md); }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
