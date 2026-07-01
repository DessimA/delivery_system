import { useAuthStore } from '@/stores/auth';

export async function authGuard(to, from, next) {
  const authStore = useAuthStore();

  if (!authStore.isInitialized) {
    await authStore.initializeAuth();
  }

  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth);
  const requiredRoles = to.meta?.roles || [];
  const isAuthenticated = authStore.isAuthenticated;

  if (requiresAuth && !isAuthenticated) {
    return next({ name: 'Login', query: { redirect: to.fullPath } });
  }

  if (isAuthenticated && requiredRoles.length > 0) {
    const userRoles = authStore.userRoles;
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(`ROLE_${role}`));
    
    // Note: Backend roles are prefixed with ROLE_, but router uses the simple name.
    // Fixed logic above to account for ROLE_ prefix if needed or standardizing names.
    // Let's standardize: router roles will be ADMIN, USER, etc. Backend returns ROLE_ADMIN.
    
    if (!hasRequiredRole) {
      return next({ name: 'Home' }); 
    }
  }

  next();
}
