import { mount, flushPromises } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppNavbar from '../components/AppNavbar.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '../stores/auth';

// Mock useAuthStore
const mockAuthStore = {
  isLoggedIn: false,
  isRestaurantOwner: false,
  isDeliveryUser: false,
  user: null,
  logout: vi.fn(),
};

vi.mock('../stores/auth', () => ({
  useAuthStore: vi.fn(() => mockAuthStore),
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
    { path: '/delivery/dashboard', name: 'DeliveryDashboard', component: { template: '<div>Delivery Dashboard</div>' } },
    { path: '/orders', name: 'Orders', component: { template: '<div>Orders</div>' } },
    { path: '/profile', name: 'Profile', component: { template: '<div>Profile</div>' } },
  ],
});

describe('AppNavbar', () => {
  beforeEach(async () => {
    setActivePinia(createPinia());
    // Reset mockAuthStore before each test
    mockAuthStore.isLoggedIn = false;
    mockAuthStore.isRestaurantOwner = false;
    mockAuthStore.isDeliveryUser = false;
    mockAuthStore.user = null;
    mockAuthStore.logout.mockClear();

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
    await router.push('/'); // Reset router to home before each test
    await flushPromises();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('renders login and register links when not logged in', async () => {
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
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(true);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="logout-link"]').exists()).toBe(false);
  });

  it('renders logout link and user email when logged in', async () => {
    mockAuthStore.isLoggedIn = true;
    mockAuthStore.user = { email: 'test@example.com', roles: ['USER'] };
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
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(false);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(false);
    await wrapper.find('[data-testid="user-profile-dropdown-toggle"]').trigger('click'); // Open dropdown
    expect(wrapper.find('[data-testid="logout-link"]').exists()).toBe(true);
    expect(wrapper.find('.user-profile span').text()).toBe('test@example.com');
  });

  it('hides "Meu Restaurante" link for regular user', async () => {
    mockAuthStore.isLoggedIn = true;
    mockAuthStore.isRestaurantOwner = false;
    mockAuthStore.user = { email: 'test@example.com', roles: ['USER'] };
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
    expect(wrapper.find('[data-testid="restaurant-dashboard-link"]').exists()).toBe(false);
  });

  it('shows "Meu Restaurante" link for restaurant owner', async () => {
    mockAuthStore.isLoggedIn = true;
    mockAuthStore.isRestaurantOwner = true;
    mockAuthStore.user = { email: 'restaurant@example.com', roles: ['RESTAURANT'] };
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
    expect(wrapper.find('[data-testid="restaurant-dashboard-link"]').exists()).toBe(true);
  });

  it('logs out user and redirects to login', async () => {
    mockAuthStore.isLoggedIn = true;
    mockAuthStore.user = { email: 'test@example.com', roles: ['USER'] };
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

    await wrapper.find('[data-testid="user-profile-dropdown-toggle"]').trigger('click'); // Open dropdown
    await wrapper.find('[data-testid="logout-link"]').trigger('click');

    expect(mockAuthStore.logout).toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith('/login');
  });
});
