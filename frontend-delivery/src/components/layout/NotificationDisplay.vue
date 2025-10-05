<template>
  <div class="notification-container">
    <transition-group name="notification-list" tag="div">
      <div
        v-for="notification in notifications"
        :key="notification.id"
        :class="['notification-item', `notification-item--${notification.type}`]"
      >
        <div class="notification-content">
          <Icon :name="getIcon(notification.type)" class="notification-icon" />
          <p>{{ notification.message }}</p>
        </div>
        <button class="close-btn" @click="removeNotification(notification.id)">
          <Icon name="x" />
        </button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useNotifications } from '@/composables/useNotifications';
import BaseIcon from '@/components/base/BaseIcon.vue';

const { notifications, removeNotification } = useNotifications();

const getIcon = (type) => {
  switch (type) {
    case 'success': return 'check-circle';
    case 'error': return 'x-circle';
    case 'warning': return 'alert-triangle';
    case 'info': return 'info';
    default: return 'bell';
  }
};
</script>

<style lang="scss" scoped>
.notification-container {
  position: fixed;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  z-index: 2000; /* Higher than modals */
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);

  @media (max-width: 600px) {
    top: auto;
    bottom: var(--spacing-lg);
    left: var(--spacing-lg);
    right: var(--spacing-lg);
  }
}

.notification-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  color: var(--color-text-light);
  min-width: 280px;
  max-width: 350px;

  &--success {
    background-color: var(--color-success);
  }
  &--error {
    background-color: var(--color-danger);
  }
  &--warning {
    background-color: var(--color-warning);
    color: var(--color-text-dark);
  }
  &--info {
    background-color: var(--color-info);
  }
}

.notification-content {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-grow: 1;

  .notification-icon {
    font-size: 1.5rem;
    flex-shrink: 0;
  }

  p {
    margin: 0;
    font-size: var(--font-size-md);
    line-height: 1.4;
  }
}

.close-btn {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 1rem;
  margin-left: var(--spacing-md);
  flex-shrink: 0;
  opacity: 0.8;

  &:hover {
    opacity: 1;
  }
}

/* Transition styles */
.notification-list-enter-active, .notification-list-leave-active {
  transition: all 0.5s ease;
}
.notification-list-enter-from, .notification-list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
.notification-list-move {
  transition: transform 0.5s ease;
}
</style>
