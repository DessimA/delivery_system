import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppHome from '../views/AppHome.vue';
import { createPinia, setActivePinia } from 'pinia';
import { useCartStore } from '../stores/cart';
import { productService } from '@/services/product.service';

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

vi.mock('@/services/product.service', () => ({
  productService: {
    getAll: vi.fn(() => Promise.resolve([])),
  },
}));

describe('AppHome', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    const cartStore = useCartStore();
    cartStore.items = [];
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders the hero section correctly', async () => {
    const wrapper = mount(AppHome, {
      global: {
        stubs: {
          BaseButton: true,
          BaseInput: true,
          EmptyState: true,
          ProductCard: true,
          ProductCardSkeleton: true,
          BaseIcon: true,
        },
      },
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.find('h1').text()).toBe('Delivery rápido e saboroso');
    expect(wrapper.find('.hero-content p').text()).toBe('Peça seus pratos favoritos com entrega garantida');
    expect(wrapper.find('img[alt="Delivery"]').exists()).toBe(true);
  });

  it('renders product list when data is available', async () => {
    const mockProducts = [
      { id: 1, name: 'Pizza', description: 'Yummy', price: 30.0, imageUrl: 'pizza.jpg' }
    ];
    productService.getAll.mockResolvedValue(mockProducts);

    const wrapper = mount(AppHome, {
      global: {
        stubs: {
          BaseButton: true,
          BaseInput: true,
          EmptyState: true,
          ProductCard: false,
          ProductCardSkeleton: true,
          BaseIcon: true,
        },
      },
    });

    await vi.dynamicImportSettled();
    await wrapper.vm.$nextTick();
    await wrapper.vm.$nextTick();

    expect(productService.getAll).toHaveBeenCalled();
    expect(wrapper.findAllComponents({ name: 'ProductCard' }).length).toBe(1);
    expect(wrapper.text()).toContain('Pizza');
  });
});
