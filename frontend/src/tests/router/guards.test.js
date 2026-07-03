import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authGuard } from '@/router/guards';
import { useAuthStore } from '@/stores/auth';
import { createPinia, setActivePinia } from 'pinia';

vi.mock('@/stores/auth', () => ({
  useAuthStore: vi.fn()
}));

describe('Router Guards', () => {
  let mockAuthStore;

  beforeEach(() => {
    setActivePinia(createPinia());
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
    const result = await authGuard(to, {});
    expect(result).toEqual({ name: 'Login', query: { redirect: '/orders' } });
  });

  it('should allow access if route does not require auth', async () => {
    const to = { matched: [{ meta: { requiresAuth: false } }] };
    const result = await authGuard(to, {});
    expect(result).toBeUndefined();
  });

  it('should redirect to home if user lacks required role', async () => {
    mockAuthStore.isAuthenticated = true;
    mockAuthStore.userRoles = ['ROLE_USER'];
    const to = { 
        matched: [{ meta: { requiresAuth: true } }], 
        meta: { roles: ['ADMIN'] } 
    };
    const result = await authGuard(to, {});
    expect(result).toEqual({ name: 'Home' });
  });

  it('should allow access if user has required role', async () => {
    mockAuthStore.isAuthenticated = true;
    mockAuthStore.userRoles = ['ROLE_ADMIN'];
    const to = { 
        matched: [{ meta: { requiresAuth: true } }], 
        meta: { roles: ['ADMIN'] } 
    };
    const result = await authGuard(to, {});
    expect(result).toBeUndefined();
  });
});
