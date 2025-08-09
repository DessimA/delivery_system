import { mount, flushPromises } from '@vue/test-utils';
import { describe, it, expect, vi } from 'vitest';
import AppNavbar from '../components/AppNavbar.vue';
import { createRouter, createWebHistory } from 'vue-router';

// Mock router
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'Home', component: { template: '<div>Home</div>' } },
    { path: '/login', name: 'Login', component: { template: '<div>Login</div>' } },
    { path: '/products', name: 'Products', component: { template: '<div>Products</div>' } },
    { path: '/cart', name: 'Cart', component: { template: '<div>Cart</div>' } },
    { path: '/restaurant/dashboard', name: 'RestaurantDashboard', component: { template: '<div>Restaurant Dashboard</div>' } },
  ],
});

describe('AppNavbar', () => {
  beforeEach(async () => {
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn((key) => {
          if (key === 'authToken') return 'fake-jwt-token';
          if (key === 'user') return JSON.stringify({ email: 'test@example.com', roles: ['USER'] });
          return null;
        }),
        setItem: vi.fn(),
        removeItem: vi.fn(),
      },
      writable: true,
    });

    // Ensure router is ready
    await router.isReady();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders login and register links when not logged in', () => {
    localStorage.getItem.mockReturnValue(null); // Simulate not logged in
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
      },
    });
    expect(wrapper.find('a[href="/login"]').exists()).toBe(true);
    expect(wrapper.find('a[href="/register"]').exists()).toBe(true);
    expect(wrapper.find('a[href="#"][@click.prevent="logout"]').exists()).toBe(false);
  });

  it('renders logout link and user email when logged in', async () => {
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
      },
    });
    await flushPromises(); // Wait for created hook to finish
    expect(wrapper.find('a[href="/login"]').exists()).toBe(false);
    expect(wrapper.find('a[href="/register"]').exists()).toBe(false);
    expect(wrapper.find('a[href="#"][@click.prevent="logout"]').exists()).toBe(true);
    expect(wrapper.find('.dropdown-toggle').text()).toBe('test@example.com');
  });

  it('hides "Meu Restaurante" link for regular user', async () => {
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
      },
    });
    await flushPromises();
    expect(wrapper.find('a[href="/restaurant/dashboard"]').exists()).toBe(false);
  });

  it('shows "Meu Restaurante" link for restaurant owner', async () => {
    localStorage.getItem.mockImplementation((key) => {
      if (key === 'authToken') return 'fake-jwt-token';
      if (key === 'user') return JSON.stringify({ email: 'restaurant@example.com', roles: ['RESTAURANT'] });
      return null;
    });
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
      },
    });
    await flushPromises();
    expect(wrapper.find('a[href="/restaurant/dashboard"]').exists()).toBe(true);
  });

  it('logs out user and redirects to login', async () => {
    const pushMock = vi.spyOn(router, 'push');
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
      },
    });
    await flushPromises();

    await wrapper.find('a[href="#"][@click.prevent="logout"]').trigger('click');

    expect(localStorage.removeItem).toHaveBeenCalledWith('authToken');
    expect(localStorage.removeItem).toHaveBeenCalledWith('user');
    expect(wrapper.vm.isLoggedIn).toBe(false);
    expect(wrapper.vm.user).toBe(null);
    expect(pushMock).toHaveBeenCalledWith('/login');
  });
});
