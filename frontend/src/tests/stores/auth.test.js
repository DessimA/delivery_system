import { describe, it, expect, vi, beforeEach } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';
import { useAuthStore } from '@/stores/auth';
import api from '@/plugins/axios';
import { storage } from '@/utils/storage';

// Mocks
vi.mock('@/plugins/axios', () => ({
  default: {
    post: vi.fn(),
    get: vi.fn()
  }
}));

vi.mock('@/utils/storage', () => ({
  storage: {
    get: vi.fn(),
    set: vi.fn(),
    remove: vi.fn(),
    clear: vi.fn()
  }
}));

describe('Auth Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it('should initialize with null user and token', () => {
    const authStore = useAuthStore();
    expect(authStore.user).toBeNull();
    expect(authStore.token).toBeNull();
    expect(authStore.isAuthenticated).toBe(false);
  });

  it('should login successfully and store token', async () => {
    const authStore = useAuthStore();
    const mockToken = 'fake-jwt';
    const mockUser = { id: 1, email: 'test@test.com', roles: ['USER'] };

    api.post.mockResolvedValueOnce({ data: { accessToken: mockToken } });
    api.get.mockResolvedValueOnce({ data: mockUser });

    await authStore.login({ email: 'test@test.com', password: 'password' });

    expect(authStore.token).toBe(mockToken);
    expect(authStore.user).toEqual(mockUser);
    expect(storage.set).toHaveBeenCalledWith('authToken', mockToken);
    expect(storage.set).toHaveBeenCalledWith('user', mockUser);
  });

  it('should logout and clear storage', () => {
    const authStore = useAuthStore();
    authStore.token = 'some-token';
    authStore.user = { id: 1 };

    authStore.logout();

    expect(authStore.token).toBeNull();
    expect(authStore.user).toBeNull();
    expect(storage.remove).toHaveBeenCalledWith('authToken');
    expect(storage.remove).toHaveBeenCalledWith('user');
  });

  it('should initialize auth from storage if token exists', async () => {
    const authStore = useAuthStore();
    const mockToken = 'saved-token';
    const mockUser = { id: 1, email: 'saved@test.com' };

    storage.get.mockImplementation((key) => {
      if (key === 'authToken') return mockToken;
      if (key === 'user') return mockUser;
      return null;
    });

    api.get.mockResolvedValueOnce({ data: mockUser });

    await authStore.initializeAuth();

    expect(authStore.token).toBe(mockToken);
    expect(authStore.user).toEqual(mockUser);
    expect(authStore.isInitialized).toBe(true);
  });
});
