<template>
  <div :class="['base-input-wrapper', { 'has-error': error }]">
    <label v-if="label" :for="id" class="base-input-label">{{ label }}</label>
    <div class="base-input-control">
      <Icon v-if="icon" :name="icon" class="base-input-icon" />
      <input
        :id="id"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        @input="updateValue"
        class="base-input"
      />
    </div>
    <p v-if="error" class="base-input-error">{{ error }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import BaseIcon from './BaseIcon.vue';

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: '',
  },
  label: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: 'text',
  },
  placeholder: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
  error: {
    type: String,
    default: '',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  id: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const updateValue = (event) => {
  emit('update:modelValue', event.target.value);
};
</script>

<style lang="scss" scoped>
.base-input-wrapper {
  margin-bottom: var(--spacing-md);

  &.has-error {
    .base-input {
      border-color: var(--color-danger);
    }
    .base-input-icon {
      color: var(--color-danger);
    }
  }
}

.base-input-label {
  display: block;
  margin-bottom: var(--spacing-xs);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-dark);
}

.base-input-control {
  position: relative;
  display: flex;
  align-items: center;
}

.base-input {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-md);
  color: var(--color-text-dark);
  transition: border-color var(--transition-fast);

  &:focus {
    outline: none;
    border-color: var(--color-primary);
  }

  &::placeholder {
    color: var(--color-text-dark);
    opacity: 0.7;
  }

  &[disabled] {
    background-color: var(--color-surface);
    cursor: not-allowed;
    opacity: 0.8;
  }
}

.base-input-icon {
  position: absolute;
  left: var(--spacing-sm);
  color: var(--color-dark);
  pointer-events: none;
  font-size: var(--font-size-lg);
}

.base-input-control .base-input {
  padding-left: calc(var(--spacing-sm) + var(--font-size-lg) + var(--spacing-xs)); /* Adjust padding for icon */
}

.base-input-error {
  color: var(--color-danger);
  font-size: var(--font-size-sm);
  margin-top: var(--spacing-xs);
}
</style>
