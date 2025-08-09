import { createRouter, createWebHistory } from 'vue-router';
import AppHome from '../views/AppHome.vue';
import AppLogin from '../views/AppLogin.vue';
import AppRegister from '../views/AppRegister.vue';
import AppProducts from '../views/AppProducts.vue';
import AppCart from '../views/AppCart.vue';
import RestaurantDashboard from '../views/RestaurantDashboard.vue';
import DeliveryDashboard from '../views/DeliveryDashboard.vue'; // Importar o novo componente

const routes = [
  {
    path: '/',
    name: 'Home',
    component: AppHome,
  },
  {
    path: '/login',
    name: 'Login',
    component: AppLogin,
  },
  {
    path: '/register',
    name: 'Register',
    component: AppRegister,
  },
  {
    path: '/products',
    name: 'Products',
    component: AppProducts,
  },
  {
    path: '/cart',
    name: 'Cart',
    component: AppCart,
    meta: { requiresAuth: true } // Rota protegida geral
  },
  {
    path: '/restaurant/dashboard',
    name: 'RestaurantDashboard',
    component: RestaurantDashboard,
    meta: { requiresAuth: true, roles: ['RESTAURANT'] } // Rota protegida por role
  },
  {
    path: '/delivery/dashboard',
    name: 'DeliveryDashboard',
    component: DeliveryDashboard,
    meta: { requiresAuth: true, roles: ['DELIVERY'] } // Rota protegida por role
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navegação de guarda para rotas protegidas
router.beforeEach((to, from, next) => {
  const loggedIn = localStorage.getItem('authToken');
  const user = JSON.parse(localStorage.getItem('user'));

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!loggedIn) {
      // Redireciona para o login se a rota exige autenticação e não há token
      return next('/login');
    }

    // Verifica as roles se a rota exigir
    if (to.meta.roles) {
      if (user && user.roles && to.meta.roles.some(role => user.roles.includes(role))) {
        // Se o usuário tem a role necessária, permite o acesso
        next();
      } else {
        // Se não tem a role, redireciona para uma página de acesso negado ou para a home
        next('/'); // Ou para uma página '/unauthorized'
      }
    } else {
      // Se a rota só exige autenticação, permite o acesso
      next();
    }
  } else {
    // Se a rota não exige autenticação, permite o acesso
    next();
  }
});

export default router;
