import { ref } from 'vue';

const notifications = ref([]);

export function useNotifications() {
  const addNotification = ({ type, message, duration = 5000 }) => {
    const notification = {
      id: Date.now() + Math.random(),
      type, // success, error, warning, info
      message,
      timestamp: new Date(),
    };
    notifications.value.push(notification);
    if (duration > 0) {
      setTimeout(() => removeNotification(notification.id), duration);
    }
  };

  const removeNotification = (id) => {
    notifications.value = notifications.value.filter((n) => n.id !== id);
  };

  return { notifications, addNotification, removeNotification };
}
