import { computed } from 'vue';
import { useAuthStore } from '@/stores/auth';

export function useAuth() {
  const authStore = useAuthStore();

  const user = computed(() => authStore.user);
  const isAuthenticated = computed(() => authStore.isAuthenticated);
  const isAdmin = computed(() => authStore.isAdmin);
  const isRestaurant = computed(() => authStore.isRestaurant);
  const isDelivery = computed(() => authStore.isDelivery);

  const login = async (credentials) => {
    return await authStore.login(credentials);
  };

  const logout = () => {
    authStore.logout();
  };

  const hasRole = (role) => {
    return authStore.userRoles.includes(role);
  };

  return {
    user,
    isAuthenticated,
    isAdmin,
    isRestaurant,
    isDelivery,
    login,
    logout,
    hasRole
  };
}
