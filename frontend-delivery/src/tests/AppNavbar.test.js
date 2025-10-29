import { mount, flushPromises } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppNavbar from '../components/AppNavbar.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../stores/auth';

// Mock useAuthStore
vi.mock('../stores/auth', () => ({
  useAuthStore: vi.fn(() => ({
    isLoggedIn: false,
    isRestaurantOwner: false,
    isDeliveryUser: false,
    user: null,
    logout: vi.fn(),
  })),
}));

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
    setActivePinia(createPinia());
    const authStore = useAuthStore();
    authStore.isLoggedIn = false;
    authStore.isRestaurantOwner = false;
    authStore.isDeliveryUser = false;
    authStore.user = null;

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
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders login and register links when not logged in', () => {
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          Icon: true,
        },
      },
    });
    console.log(wrapper.html());
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(true);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(true);
    expect(wrapper.find('.user-dropdown .dropdown-item').filter(w => w.text().includes('Sair')).exists()).toBe(false);
  });

  it('renders logout link and user email when logged in', async () => {
    const authStore = useAuthStore();
    authStore.isLoggedIn = true;
    authStore.user = { email: 'test@example.com', roles: ['USER'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          Icon: true,
        },
      },
    });
    await flushPromises(); // Wait for created hook to finish
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(false);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(false);
    await wrapper.find('.user-profile').trigger('click'); // Open dropdown
    expect(wrapper.find('.user-dropdown .dropdown-item').filter(w => w.text().includes('Sair')).exists()).toBe(true);
    expect(wrapper.find('.dropdown-toggle').text()).toBe('test@example.com');
  });

  it('hides "Meu Restaurante" link for regular user', async () => {
    const authStore = useAuthStore();
    authStore.isLoggedIn = true;
    authStore.isRestaurantOwner = false;
    authStore.user = { email: 'test@example.com', roles: ['USER'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          Icon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('router-link-stub[to="/restaurant/dashboard"]').exists()).toBe(false);
  });

  it('shows "Meu Restaurante" link for restaurant owner', async () => {
    const authStore = useAuthStore();
    authStore.isLoggedIn = true;
    authStore.isRestaurantOwner = true;
    authStore.user = { email: 'restaurant@example.com', roles: ['RESTAURANT'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          Icon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('router-link-stub[to="/restaurant/dashboard"]').exists()).toBe(true);
  });

  it('logs out user and redirects to login', async () => {
    const authStore = useAuthStore();
    authStore.isLoggedIn = true;
    authStore.user = { email: 'test@example.com', roles: ['USER'] };
    const pushMock = vi.spyOn(router, 'push');
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          Icon: true,
        },
      },
    });
    await flushPromises();

    await wrapper.find('.user-profile').trigger('click'); // Open dropdown
    await wrapper.find('.user-profile').trigger('click'); // Open dropdown
    await wrapper.find('.user-dropdown .dropdown-item').filter(w => w.text().includes('Sair')).trigger('click');

    expect(localStorage.removeItem).toHaveBeenCalledWith('authToken');
    expect(localStorage.removeItem).toHaveBeenCalledWith('user');
    expect(wrapper.vm.isLoggedIn).toBe(false);
    expect(wrapper.vm.user).toBe(null);
    expect(pushMock).toHaveBeenCalledWith('/login');
  });
});
