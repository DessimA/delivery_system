import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Import views
import AppHome from '../views/AppHome.vue';
import AppLogin from '../views/AppLogin.vue';
import AppRegister from '../views/AppRegister.vue';
import AppCart from '../views/AppCart.vue';
import AppOrders from '../views/AppOrders.vue';
import AppProfile from '../views/AppProfile.vue';
import RestaurantDashboard from '../views/RestaurantDashboard.vue';
import DeliveryDashboard from '../views/DeliveryDashboard.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import RestaurantsList from '../views/RestaurantsList.vue';
import RestaurantDetail from '../views/RestaurantDetail.vue';
import CheckoutPix from '../views/CheckoutPix.vue';

const routes = [
  { path: '/', name: 'Home', component: AppHome },
  { path: '/login', name: 'Login', component: AppLogin },
  { path: '/register', name: 'Register', component: AppRegister },
  { path: '/restaurants', name: 'Restaurants', component: RestaurantsList },
  { path: '/restaurants/:id', name: 'RestaurantDetail', component: RestaurantDetail },
  {
    path: '/checkout/pix',
    name: 'CheckoutPix',
    component: CheckoutPix,
    meta: { requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: AppCart,
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: AppOrders,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: AppProfile,
    meta: { requiresAuth: true }
  },
  {
    path: '/restaurant/dashboard',
    name: 'RestaurantDashboard',
    component: RestaurantDashboard,
    meta: { requiresAuth: true, roles: ['RESTAURANT'] }
  },
  {
    path: '/delivery/dashboard',
    name: 'DeliveryDashboard',
    component: DeliveryDashboard,
    meta: { requiresAuth: true, roles: ['DELIVERY'] }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  },
  // TODO: Add a '/unauthorized' page for users who lack required roles
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation Guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();

  // Ensure the auth store is initialized before checking routes
  if (!authStore.isInitialized) {
    await authStore.initializeAuth();
  }

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiredRoles = to.meta.roles || [];
  const isAuthenticated = authStore.isAuthenticated;

  if (requiresAuth && !isAuthenticated) {
    // Redirect to login if auth is required and user is not logged in
    return next({ name: 'Login', query: { redirect: to.fullPath } });
  }

  if (isAuthenticated && requiredRoles.length > 0) {
    const userRoles = authStore.userRoles;
    // Check if user has at least one of the required roles
    const hasRequiredRole = requiredRoles.some(role => userRoles.includes(role));
    if (!hasRequiredRole) {
      // Redirect to home or an 'unauthorized' page if user lacks the role
      return next({ name: 'Home' }); // Or next('/unauthorized')
    }
  }

  // If all checks pass, proceed
  next();
});

export default router;
