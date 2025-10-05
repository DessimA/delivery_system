<template>
  <div class="empty-state">
    <Icon v-if="icon" :name="icon" class="empty-state-icon" />
    <h3 class="empty-state-title">{{ title }}</h3>
    <p class="empty-state-description">{{ description }}</p>
    <BaseButton
      v-if="actionLabel"
      :label="actionLabel"
      variant="primary"
      @click="$emit('action')"
    />
  </div>
</template>

<script setup>
import BaseIcon from './BaseIcon.vue';
import BaseButton from './BaseButton.vue';

defineProps({
  title: {
    type: String,
    required: true,
  },
  description: {
    type: String,
    default: '',
  },
  icon: {
    type: String,
    default: '',
  },
  actionLabel: {
    type: String,
    default: '',
  },
});

defineEmits(['action']);
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--spacing-xl);
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  margin: var(--spacing-xl) auto;
  max-width: 400px;
  color: var(--color-text-dark);
}

.empty-state-icon {
  font-size: 4rem; /* Adjust icon size */
  color: var(--color-primary);
  margin-bottom: var(--spacing-md);
}

.empty-state-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-sm);
}

.empty-state-description {
  font-size: var(--font-size-md);
  margin-bottom: var(--spacing-lg);
  color: var(--color-dark);
}
</style>
