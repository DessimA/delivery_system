<template>
  <div class="profile-section" @click="$emit('click')" role="button" tabindex="0">
    <div class="profile-section__icon">
      <BaseIcon :name="icon" size="24" />
    </div>
    <div class="profile-section__content">
      <h3 class="title">{{ title }}</h3>
      <p class="description">{{ description }}</p>
    </div>
    <div class="profile-section__arrow">
      <BaseIcon name="chevron-right" size="20" />
    </div>
  </div>
</template>

<script setup>
import BaseIcon from '@/components/base/BaseIcon.vue';

defineProps({
  title: String,
  description: String,
  icon: String
});

defineEmits(['click']);
</script>

<style lang="scss" scoped>
.profile-section {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  background-color: var(--color-surface);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover {
    background-color: var(--color-background);
    transform: translateX(4px);
    border-color: var(--color-primary-light);
  }

  &__icon {
    width: 48px;
    height: 48px;
    background-color: rgba(255, 107, 53, 0.1);
    color: var(--color-primary);
    border-radius: var(--radius-full);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: var(--spacing-lg);
  }

  &__content {
    flex-grow: 1;
    .title {
      font-size: var(--font-size-lg);
      font-weight: var(--font-weight-semibold);
      margin: 0 0 0.25rem;
    }
    .description {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
      margin: 0;
    }
  }

  &__arrow {
    color: var(--color-text-muted);
  }
}
</style>
