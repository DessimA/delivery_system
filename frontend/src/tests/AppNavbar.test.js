import { mount, flushPromises } from '@vue/test-utils';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import AppNavbar from '../components/layout/AppNavbar.vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia, setActivePinia } from 'pinia';

// Mock useAuth composable
const mockLogout = vi.fn();
vi.mock('@/composables/useAuth', () => ({
  useAuth: () => ({
    isAuthenticated: mockAuth.isAuthenticated,
    isAdmin: mockAuth.isAdmin,
    isRestaurant: mockAuth.isRestaurant,
    isDelivery: mockAuth.isDelivery,
    user: mockAuth.user,
    logout: mockLogout,
  }),
}));

const mockAuth = {
  isAuthenticated: false,
  isAdmin: false,
  isRestaurant: false,
  isDelivery: false,
  user: null,
};

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
    // Reset mockAuth before each test
    mockAuth.isAuthenticated = false;
    mockAuth.isAdmin = false;
    mockAuth.isRestaurant = false;
    mockAuth.isDelivery = false;
    mockAuth.user = null;
    mockLogout.mockClear();

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
          BaseIcon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(true);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(true);
    expect(wrapper.find('[data-testid="logout-link"]').exists()).toBe(false);
  });

  it('renders logout link and user name when logged in', async () => {
    mockAuth.isAuthenticated = true;
    mockAuth.user = { name: 'Test User', email: 'test@example.com', roles: ['ROLE_USER'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('router-link-stub[to="/login"]').exists()).toBe(false);
    expect(wrapper.find('router-link-stub[to="/register"]').exists()).toBe(false);
    await wrapper.find('[data-testid="user-profile-dropdown-toggle"]').trigger('click'); // Open dropdown
    expect(wrapper.find('[data-testid="logout-link"]').exists()).toBe(true);
    expect(wrapper.find('.user-name').text()).toBe('Test User');
  });

  it('hides "Meu Restaurante" link for regular user', async () => {
    mockAuth.isAuthenticated = true;
    mockAuth.isRestaurant = false;
    mockAuth.user = { name: 'User', email: 'test@example.com', roles: ['ROLE_USER'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('[data-testid="restaurant-dashboard-link"]').exists()).toBe(false);
  });

  it('shows "Meu Restaurante" link for restaurant owner', async () => {
    mockAuth.isAuthenticated = true;
    mockAuth.isRestaurant = true;
    mockAuth.user = { name: 'Owner', email: 'restaurant@example.com', roles: ['ROLE_RESTAURANT'] };
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
      },
    });
    await flushPromises();
    expect(wrapper.find('[data-testid="restaurant-dashboard-link"]').exists()).toBe(true);
  });

  it('logs out user and redirects to login', async () => {
    mockAuth.isAuthenticated = true;
    mockAuth.user = { name: 'User', email: 'test@example.com', roles: ['ROLE_USER'] };
    const pushMock = vi.spyOn(router, 'push');
    const wrapper = mount(AppNavbar, {
      global: {
        plugins: [router],
        stubs: {
          RouterLink: true,
          BaseIcon: true,
        },
      },
    });
    await flushPromises();

    await wrapper.find('[data-testid="user-profile-dropdown-toggle"]').trigger('click'); // Open dropdown
    await wrapper.find('[data-testid="logout-link"]').trigger('click');

    expect(mockLogout).toHaveBeenCalled();
    expect(pushMock).toHaveBeenCalledWith('/login');
  });
});
