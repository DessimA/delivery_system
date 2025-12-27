import { mount } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppHome from '../views/AppHome.vue';
import { createPinia, setActivePinia } from 'pinia';
import { useCartStore } from '../stores/cart';

// Mock the useApi composable
vi.mock('../composables/useApi', () => ({
  useApi: () => ({
    loading: false,
    execute: vi.fn(() => Promise.resolve({ data: [] })),
  }),
}));

// Mock the useNotifications composable
vi.mock('../composables/useNotifications', () => ({
  useNotifications: () => ({
    addNotification: vi.fn(),
  }),
}));

describe('AppHome', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    const cartStore = useCartStore();
    cartStore.items = []; // Clear cart items before each test
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
          FilterChip: true,
          ProductCard: true,
          ProductCardSkeleton: true,
          Icon: true,
        },
      },
    });

    // Wait for the component to render after data fetching (if any)
    await wrapper.vm.$nextTick();

    // Check if the main title is rendered
    expect(wrapper.find('h1').text()).toBe('Delivery rápido e saboroso');

    // Check if the subtitle is rendered
    expect(wrapper.find('.hero-content p').text()).toBe('Peça seus pratos favoritos com entrega garantida');

    // Check if the hero image is present
    expect(wrapper.find('img[alt="Delivery"]').exists()).toBe(true);
  });
});
