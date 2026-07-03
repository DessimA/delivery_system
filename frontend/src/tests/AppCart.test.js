import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppCart from '../views/AppCart.vue';
import { createPinia, setActivePinia } from 'pinia';
import { useCartStore } from '../stores/cart';
import { useAuthStore } from '../stores/auth';
import { orderService } from '@/services/order.service';

vi.mock('@/composables/useApi', () => ({
  useApi: () => ({
    loading: false,
    execute: vi.fn((fn) => fn()),
  }),
}));

vi.mock('@/composables/useNotifications', () => ({
  useNotifications: () => ({
    addNotification: vi.fn(),
  }),
}));

vi.mock('@/services/order.service', () => ({
  orderService: {
    create: vi.fn(),
  },
}));

vi.mock('@/plugins/axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  },
}));

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: vi.fn(),
  }),
  useRoute: () => ({
    query: {},
  }),
  createRouter: vi.fn(() => ({
    beforeEach: vi.fn(),
    push: vi.fn(),
  })),
  createWebHistory: vi.fn(),
}));

describe('AppCart', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    const authStore = useAuthStore();
    authStore.user = { id: 1, name: 'Test', address: 'Rua Teste' };
    authStore.isInitialized = true;
    const cartStore = useCartStore();
    cartStore.items = [];
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders empty state when cart has no items', async () => {
    const wrapper = mount(AppCart, {
      global: {
        stubs: {
          BaseButton: true,
          BaseInput: true,
          BaseModal: true,
          CartItemCard: true,
          EmptyState: true,
          BaseIcon: true,
        },
      },
    });

    await wrapper.vm.$nextTick();
    expect(wrapper.findComponent({ name: 'EmptyState' }).exists()).toBe(true);
  });

  it('renders cart items when cart is not empty', async () => {
    const cartStore = useCartStore();
    cartStore.items = [
      { id: 1, name: 'Pizza', price: 30.0, quantity: 2, imageUrl: '' }
    ];

    const wrapper = mount(AppCart, {
      global: {
        stubs: {
          BaseButton: true,
          BaseInput: true,
          BaseModal: true,
          CartItemCard: true,
          EmptyState: true,
          BaseIcon: true,
        },
      },
    });

    await wrapper.vm.$nextTick();
    expect(wrapper.find('.cart-header h1').text()).toBe('Seu Carrinho');
    expect(wrapper.findComponent({ name: 'CartItemCard' }).exists()).toBe(true);
  });
});
