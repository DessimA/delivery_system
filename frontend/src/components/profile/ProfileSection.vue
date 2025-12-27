<template>
  <div class="profile-section" @click="$emit('click')">
    <div class="section-icon">
      <Icon :name="icon" />
    </div>
    <div class="section-content">
      <h3 class="section-title">{{ title }}</h3>
      <p class="section-description">{{ description }}</p>
    </div>
    <div class="section-arrow">
      <Icon name="chevron-right" />
    </div>
  </div>
</template>

<script setup>
import BaseIcon from '@/components/base/BaseIcon.vue';

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
});

defineEmits(['click']);
</script>

<style lang="scss" scoped>
.profile-section {
  display: flex;
  align-items: center;
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-md);
  }
}

.section-icon {
  font-size: 2rem;
  color: var(--color-primary);
  margin-right: var(--spacing-md);
  flex-shrink: 0;
}

.section-content {
  flex-grow: 1;
}

.section-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-dark);
}

.section-description {
  font-size: var(--font-size-sm);
  color: var(--color-dark);
  margin: 0;
}

.section-arrow {
  font-size: 1.5rem;
  color: var(--color-dark);
  margin-left: var(--spacing-md);
  flex-shrink: 0;
}
</style>
