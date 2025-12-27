<template>
  <teleport to="body">
    <transition name="modal-fade">
      <div v-if="modelValue" class="base-modal-backdrop" @click.self="closeModal">
        <div class="base-modal" role="dialog" aria-labelledby="modalTitle" aria-describedby="modalDescription">
          <header class="base-modal-header">
            <h3 id="modalTitle">{{ title }}</h3>
            <button type="button" class="btn-close" @click="closeModal" aria-label="Close modal">
              <BaseIcon name="x" />
            </button>
          </header>

          <section class="base-modal-body" id="modalDescription">
            <slot></slot>
          </section>

          <footer v-if="$slots.footer" class="base-modal-footer">
            <slot name="footer"></slot>
          </footer>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { watch } from 'vue';
import BaseIcon from './BaseIcon.vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: 'Modal',
  },
  closeOnBackdropClick: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const closeModal = () => {
  if (props.closeOnBackdropClick) {
    emit('update:modelValue', false);
  }
};

// Add/remove body-no-scroll class to prevent scrolling when modal is open
watch(() => props.modelValue, (newValue) => {
  console.log('BaseModal modelValue prop changed:', newValue);
  if (newValue) {
    document.body.classList.add('body-no-scroll');
  } else {
    document.body.classList.remove('body-no-scroll');
  }
}, { immediate: true });
</script>

<style lang="scss" scoped>
.base-modal-backdrop {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.base-modal {
  background: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
  max-width: 500px; /* Adjust as needed */
  width: 90%;
  margin: var(--spacing-lg);
  z-index: 1001;
}

.base-modal-header {
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--color-border);
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-semibold);
  }

  .btn-close {
    background: none;
    border: none;
    font-size: var(--font-size-lg);
    cursor: pointer;
    color: var(--color-dark);
    padding: var(--spacing-xs);
    border-radius: var(--radius-sm);
    &:hover {
      background-color: var(--color-surface);
    }
  }
}

.base-modal-body {
  padding: var(--spacing-lg);
  overflow-y: auto;
  flex-grow: 1;
}

.base-modal-footer {
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--color-border);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* Transition styles */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-active .base-modal,
.modal-fade-leave-active .base-modal {
  transition: all 0.3s ease-out;
}

.modal-fade-enter-from .base-modal,
.modal-fade-leave-to .base-modal {
  transform: translateY(-20px);
  opacity: 0;
}

/* Global style for body when modal is open */
body.body-no-scroll {
  overflow: hidden;
}
</style>
