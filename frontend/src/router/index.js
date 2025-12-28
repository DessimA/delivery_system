import { createRouter, createWebHistory } from 'vue-router';
import { authGuard } from './guards';

const routes = [
  { 
    path: '/', 
    name: 'Home', 
    component: () => import('../views/AppHome.vue') 
  },
  { 
    path: '/login', 
    name: 'Login', 
    component: () => import('../views/AppLogin.vue') 
  },
  { 
    path: '/register', 
    name: 'Register', 
    component: () => import('../views/AppRegister.vue') 
  },
  { 
    path: '/restaurants', 
    name: 'Restaurants', 
    component: () => import('../views/RestaurantsList.vue') 
  },
  { 
    path: '/restaurants/:id', 
    name: 'RestaurantDetail', 
    component: () => import('../views/RestaurantDetail.vue') 
  },
  {
    path: '/checkout/pix',
    name: 'CheckoutPix',
    component: () => import('../views/CheckoutPix.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/AppCart.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/AppOrders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/AppProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/restaurant/dashboard',
    name: 'RestaurantDashboard',
    component: () => import('../views/RestaurantDashboard.vue'),
    meta: { requiresAuth: true, roles: ['RESTAURANT'] }
  },
  {
    path: '/delivery/dashboard',
    name: 'DeliveryDashboard',
    component: () => import('../views/DeliveryDashboard.vue'),
    meta: { requiresAuth: true, roles: ['DELIVERY'] }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(authGuard);

export default router;
