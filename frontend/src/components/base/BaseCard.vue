<template>
  <div class="base-card">
    <slot></slot>
  </div>
</template>

<script setup>
// No script needed for a simple card wrapper
</script>

<style lang="scss" scoped>
.base-card {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}
</style>
