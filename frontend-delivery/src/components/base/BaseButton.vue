<template>
  <button 
    :class="buttonClasses" 
    :disabled="loading || disabled"
    @click="$emit('click', $event)"
  >
    <LoadingSpinner v-if="loading" size="sm" />
    <Icon v-if="icon && !loading" :name="icon" />
    <span v-if="label">{{ label }}</span>
    <slot v-else></slot>
  </button>
</template>

<script setup>
import { computed } from 'vue';
import LoadingSpinner from './LoadingSpinner.vue';
import BaseIcon from './BaseIcon.vue';

const props = defineProps({
  label: {
    type: String,
    default: '',
  },
  variant: {
    type: String,
    default: 'primary', // primary, secondary, success, danger, warning, info, light, dark, link
  },
  size: {
    type: String,
    default: 'md', // sm, md, lg
  },
  loading: {
    type: Boolean,
    default: false,
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  icon: {
    type: String,
    default: '',
  },
  block: {
    type: Boolean,
    default: false,
  },
});

const buttonClasses = computed(() => [
  'base-button',
  `base-button--${props.variant}`,
  `base-button--${props.size}`,
  {
    'base-button--loading': props.loading,
    'base-button--disabled': props.disabled,
    'base-button--block': props.block,
  },
]);

defineEmits(['click']);
</script>

<style lang="scss" scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
  transition: var(--transition-normal);
  cursor: pointer;
  white-space: nowrap;

  &:hover:not(.base-button--disabled):not(.base-button--loading) {
    opacity: 0.9;
  }

  &--primary {
    background-color: var(--color-primary);
    color: var(--color-text-light);
    &:hover {
      background-color: var(--color-primary-dark);
    }
  }

  &--secondary {
    background-color: var(--color-secondary);
    color: var(--color-text-light);
  }

  &--success {
    background-color: var(--color-success);
    color: var(--color-text-light);
  }

  &--danger {
    background-color: var(--color-danger);
    color: var(--color-text-light);
  }

  &--warning {
    background-color: var(--color-warning);
    color: var(--color-text-dark);
  }

  &--info {
    background-color: var(--color-info);
    color: var(--color-text-light);
  }

  &--light {
    background-color: var(--color-light);
    color: var(--color-text-dark);
    border: 1px solid var(--color-border);
  }

  &--dark {
    background-color: var(--color-dark);
    color: var(--color-text-light);
  }

  &--link {
    background: none;
    color: var(--color-primary);
    padding: 0;
    text-decoration: underline;
    &:hover {
      text-decoration: none;
    }
  }

  &--sm {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--font-size-sm);
  }

  &--md {
    padding: var(--spacing-sm) var(--spacing-md);
    font-size: var(--font-size-md);
  }

  &--lg {
    padding: var(--spacing-md) var(--spacing-lg);
    font-size: var(--font-size-lg);
  }

  &--block {
    width: 100%;
  }

  &--loading,
  &--disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}
</style>
