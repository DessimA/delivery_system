import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authGuard } from '@/router/guards';
import { useAuthStore } from '@/stores/auth';
import { createPinia, setActivePinia } from 'pinia';

vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn()
}));

describe('Router Guards', () => {
  let mockAuthStore;
  let mockNext;

  beforeEach(() => {
    setActivePinia(createPinia());
    mockNext = vi.fn();
    mockAuthStore = {
      isInitialized: true,
      isAuthenticated: false,
      userRoles: [],
      initializeAuth: vi.fn()
    };
    useAuthStore.mockReturnValue(mockAuthStore);
  });

  it('should redirect to login if route requires auth and user is not authenticated', async () => {
    const to = { matched: [{ meta: { requiresAuth: true } }], fullPath: '/orders' };
    await authGuard(to, {}, mockNext);
    expect(mockNext).toHaveBeenCalledWith({ name: 'Login', query: { redirect: '/orders' } });
  });

  it('should allow access if route does not require auth', async () => {
    const to = { matched: [{ meta: { requiresAuth: false } }] };
    await authGuard(to, {}, mockNext);
    expect(mockNext).toHaveBeenCalledWith();
  });

  it('should redirect to home if user lacks required role', async () => {
    mockAuthStore.isAuthenticated = true;
    mockAuthStore.userRoles = ['ROLE_USER'];
    const to = { 
        matched: [{ meta: { requiresAuth: true } }], 
        meta: { roles: ['ADMIN'] } 
    };
    await authGuard(to, {}, mockNext);
    expect(mockNext).toHaveBeenCalledWith({ name: 'Home' });
  });

  it('should allow access if user has required role', async () => {
    mockAuthStore.isAuthenticated = true;
    mockAuthStore.userRoles = ['ROLE_ADMIN'];
    const to = { 
        matched: [{ meta: { requiresAuth: true } }], 
        meta: { roles: ['ADMIN'] } 
    };
    await authGuard(to, {}, mockNext);
    expect(mockNext).toHaveBeenCalledWith();
  });
});
