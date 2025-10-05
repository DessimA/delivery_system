<template>
  <button
    :class="['filter-chip', { 'filter-chip--active': active }]"
    @click="$emit('click')"
  >
    <slot>{{ label }}</slot>
  </button>
</template>

<script setup>
defineProps({
  label: {
    type: String,
    default: '',
  },
  active: {
    type: Boolean,
    default: false,
  },
});

defineEmits(['click']);
</script>

<style lang="scss" scoped>
.filter-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  color: var(--color-text-dark);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;

  &:hover {
    background-color: var(--color-border);
  }

  &--active {
    background-color: var(--color-primary);
    border-color: var(--color-primary);
    color: var(--color-text-light);

    &:hover {
      background-color: var(--color-primary-dark);
      border-color: var(--color-primary-dark);
    }
  }
}
</style>
