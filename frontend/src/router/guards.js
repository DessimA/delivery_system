import { useAuthStore } from '@/stores/auth';

export async function authGuard(to, from) {
  const authStore = useAuthStore();

  if (!authStore.isInitialized) {
    await authStore.initializeAuth();
  }

  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth);
  const requiredRoles = to.meta?.roles || [];
  const isAuthenticated = authStore.isAuthenticated;

  if (requiresAuth && !isAuthenticated) {
    return { name: 'Login', query: { redirect: to.fullPath } };
  }

  if (isAuthenticated && requiredRoles.length > 0) {
    const userRoles = authStore.userRoles;
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(`ROLE_${role}`));
    
    if (!hasRequiredRole) {
      return { name: 'Home' }; 
    }
  }
}
