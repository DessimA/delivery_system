import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import AppLogin from '../views/AppLogin.vue';
import api from '@/plugins/axios';
import { createPinia, setActivePinia } from 'pinia';
import { ref } from 'vue';

// Stable mock functions for mocks that are called during setup
const mockAddNotification = vi.fn();

// Mock storage util
vi.mock('@/utils/storage', () => ({
  storage: {
    get: vi.fn(),
    set: vi.fn(),
    remove: vi.fn(),
  },
}));

// Mock router
vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router');
  return {
    ...actual,
    useRouter: vi.fn(),
    useRoute: vi.fn(),
  };
});

// Mock composables
vi.mock('@/composables/useNotifications', () => ({
  useNotifications: vi.fn(() => ({
    addNotification: mockAddNotification,
  })),
}));

vi.mock('@/composables/useApi', () => ({
  useApi: vi.fn(() => ({
    loading: ref(false),
    error: ref(null),
    execute: vi.fn((fn) => fn()),
  })),
}));

describe('AppLogin', () => {
  let mockRouterPush;

  beforeEach(async () => {
    setActivePinia(createPinia());

    const { useRouter, useRoute } = await import('vue-router');

    mockRouterPush = vi.fn();
    useRouter.mockReturnValue({
      push: mockRouterPush,
    });

    useRoute.mockReturnValue({
      query: {},
    });

    // Default API mocks
    vi.spyOn(api, 'post').mockResolvedValue({
      data: { accessToken: 'fake-jwt-token' },
    });

    vi.spyOn(api, 'get').mockResolvedValue({
      data: { name: 'Test User', email: 'test@example.com', roles: ['ROLE_USER'] },
    });

    // Clear mocks
    mockAddNotification.mockClear();
    mockRouterPush.mockClear();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('renders correctly', () => {
    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
        plugins: [createPinia()],
      },
    });
    expect(wrapper.find('h1').exists()).toBe(true);
    expect(wrapper.find('form').exists()).toBe(true);
    expect(wrapper.find('#email').exists()).toBe(true);
    expect(wrapper.find('#password').exists()).toBe(true);
  });

  it('displays error message on failed login', async () => {
    const errorResponse = {
      response: {
        status: 401,
        data: { message: 'Credenciais inválidas' },
      },
    };

    vi.spyOn(api, 'post').mockRejectedValue(errorResponse);

    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
        plugins: [createPinia()],
      },
    });

    await wrapper.find('#email').setValue('test@example.com');
    await wrapper.find('#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit');

    // Wait for async operations
    await wrapper.vm.$nextTick();
    await new Promise(resolve => setTimeout(resolve, 50));

    expect(mockAddNotification).toHaveBeenCalledWith({
      type: 'error',
      message: 'Credenciais inválidas',
    });
  });

  it('successfully logs in and redirects', async () => {
    const { storage } = await import('@/utils/storage');
    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
        plugins: [createPinia()],
      },
    });

    await wrapper.find('#email').setValue('test@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('form').trigger('submit');
    
    await wrapper.vm.$nextTick();
    await new Promise(resolve => setTimeout(resolve, 50));

    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      password: 'password123',
    });
    expect(storage.set).toHaveBeenCalledWith('authToken', 'fake-jwt-token');
    expect(mockRouterPush).toHaveBeenCalledWith('/');
  });
});