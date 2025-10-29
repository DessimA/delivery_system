import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import AppLogin from '../views/AppLogin.vue';
import api from '@/api';
import { createPinia, setActivePinia } from 'pinia';

describe('AppLogin', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn(() => null),
        setItem: vi.fn(),
        removeItem: vi.fn(),
      },
      writable: true,
    });

    // Mock api.post
    vi.spyOn(api, 'post').mockResolvedValue({
      data: { accessToken: 'fake-jwt-token' },
    });

    // Mock api.get for user profile
    vi.spyOn(api, 'get').mockResolvedValue({
      data: { email: 'test@example.com', roles: ['USER'] },
    });

    // Mock router push
    vi.mock('vue-router', () => ({
      useRouter: () => ({
        push: vi.fn(),
      }),
      useRoute: () => ({
        query: {},
      }),
      createWebHistory: vi.fn(),
      createRouter: vi.fn(() => ({
        beforeEach: vi.fn(),
      })),
    }));
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders correctly', () => {
    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          Icon: true,
        },
        plugins: [createPinia()],
      },
    });
    expect(wrapper.find('h1').exists()).toBe(true); // h1 now exists in template
    expect(wrapper.find('form').exists()).toBe(true);
    expect(wrapper.find('#email').exists()).toBe(true);
    expect(wrapper.find('#password').exists()).toBe(true);
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true);
  });

  it('displays error message on failed login', async () => {
    const mockAddNotification = vi.fn();
    vi.mock('@/composables/useNotifications', () => ({
      useNotifications: () => ({
        addNotification: mockAddNotification,
      }),
    }));

    vi.spyOn(api, 'post').mockRejectedValue({
      response: {
        status: 401,
        data: { message: 'Credenciais inválidas' },
      },
    });

    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          Icon: true,
        },
        plugins: [createPinia()],
      },
    });

    await wrapper.find('#email').setValue('test@example.com');
    await wrapper.find('#password').setValue('wrongpassword');
    await wrapper.find('form').trigger('submit');

    expect(mockAddNotification).toHaveBeenCalledWith({
      type: 'error',
      message: 'Credenciais inválidas',
    });
    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      senha: 'wrongpassword',
    });
  });

  it('successfully logs in and redirects', async () => {
    const mockRouterPush = vi.fn();
    vi.mock('vue-router', () => ({
      useRouter: () => ({
        push: mockRouterPush,
      }),
      useRoute: () => ({
        query: {},
      }),
      createWebHistory: vi.fn(),
      createRouter: vi.fn(() => ({
        beforeEach: vi.fn(),
      })),
    }));

    const wrapper = mount(AppLogin, {
      global: {
        stubs: {
          RouterLink: true,
          Icon: true,
        },
        plugins: [createPinia()],
      },
    });

    await wrapper.find('#email').setValue('test@example.com');
    await wrapper.find('#password').setValue('password123');
    await wrapper.find('form').trigger('submit');

    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      senha: 'password123',
    });
    expect(localStorage.setItem).toHaveBeenCalledWith('authToken', 'fake-jwt-token');
    expect(api.get).toHaveBeenCalledWith('/usuarios/me');
    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify({ email: 'test@example.com', roles: ['USER'] }));
    expect(mockRouterPush).toHaveBeenCalledWith('/');
  });
});
