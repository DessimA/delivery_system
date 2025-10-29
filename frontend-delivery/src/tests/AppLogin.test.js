import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import AppLogin from '../views/AppLogin.vue';
import api from '@/api';
import { createPinia, setActivePinia } from 'pinia';
import { ref } from 'vue';

// Mock dos módulos ANTES de qualquer coisa
vi.mock('vue-router', async () => {
  const actual = await vi.importActual('vue-router');
  return {
    ...actual,
    useRouter: vi.fn(),
    useRoute: vi.fn(),
    createRouter: vi.fn(() => ({
      beforeEach: vi.fn(),
    })),
    createWebHistory: vi.fn(),
  };
});

vi.mock('@/composables/useNotifications', () => ({
  useNotifications: vi.fn(),
}));

vi.mock('@/composables/useApi', () => ({
  useApi: vi.fn(),
}));

const mockRouterPush = vi.fn();
const mockAddNotification = vi.fn();
const mockExecute = vi.fn();

describe('AppLogin', () => {
  beforeEach(async () => {
    setActivePinia(createPinia());

    // Importar os mocks
    const { useRouter, useRoute } = await import('vue-router');
    const { useNotifications } = await import('@/composables/useNotifications');
    const { useApi } = await import('@/composables/useApi');

    // Configurar retornos dos mocks
    useRouter.mockReturnValue({
      push: mockRouterPush,
    });

    useRoute.mockReturnValue({
      query: {},
    });

    useNotifications.mockReturnValue({
      addNotification: mockAddNotification,
    });

    // IMPORTANTE: retornar o valor booleano, não o ref
    useApi.mockReturnValue({
      loading: ref(false),
      error: ref(null),
      execute: mockExecute,
    });

    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn(() => null),
        setItem: vi.fn(),
        removeItem: vi.fn(),
      },
      writable: true,
    });

    // Mock api.post - sucesso por padrão
    vi.spyOn(api, 'post').mockResolvedValue({
      data: { accessToken: 'fake-jwt-token' },
    });

    // Mock api.get - sucesso por padrão
    vi.spyOn(api, 'get').mockResolvedValue({
      data: { email: 'test@example.com', roles: ['USER'] },
    });

    // Configurar mockExecute para executar a função passada
    mockExecute.mockImplementation(async (fn) => {
      return await fn();
    });

    // Limpar chamadas anteriores
    mockRouterPush.mockClear();
    mockAddNotification.mockClear();
    mockExecute.mockClear();
  });

  afterEach(() => {
    vi.clearAllMocks();
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
    expect(wrapper.find('h1').exists()).toBe(true);
    expect(wrapper.find('form').exists()).toBe(true);
    expect(wrapper.find('#email').exists()).toBe(true);
    expect(wrapper.find('#password').exists()).toBe(true);
    expect(wrapper.find('button[type="submit"]').exists()).toBe(true);
  });

  it('displays error message on failed login', async () => {
    const errorResponse = {
      response: {
        status: 401,
        data: { message: 'Credenciais inválidas' },
      },
    };

    // Mock para retornar erro
    vi.spyOn(api, 'post').mockRejectedValue(errorResponse);

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

    // Aguardar todas as promises
    await wrapper.vm.$nextTick();
    await new Promise(resolve => setTimeout(resolve, 50));

    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      senha: 'wrongpassword',
    });

    expect(mockAddNotification).toHaveBeenCalledWith({
      type: 'error',
      message: 'Credenciais inválidas',
    });
  });

  it('successfully logs in and redirects', async () => {
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
    
    // Aguardar todas as promises
    await wrapper.vm.$nextTick();
    await new Promise(resolve => setTimeout(resolve, 50));

    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'test@example.com',
      senha: 'password123',
    });
    expect(localStorage.setItem).toHaveBeenCalledWith('authToken', 'fake-jwt-token');
    expect(api.get).toHaveBeenCalledWith('/usuarios/me');
    expect(localStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify({ email: 'test@example.com', roles: ['USER'] }));
    expect(mockAddNotification).toHaveBeenCalledWith({
      type: 'success',
      message: 'Login realizado com sucesso!',
    });
    expect(mockRouterPush).toHaveBeenCalledWith('/');
  });
});