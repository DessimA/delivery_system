import { createRouter, createWebHistory } from 'vue-router';
import AppHome from '../views/AppHome.vue';
import AppLogin from '../views/AppLogin.vue';
import AppRegister from '../views/AppRegister.vue';
import AppProducts from '../views/AppProducts.vue';
import AppCart from '../views/AppCart.vue';

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
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navegação de guarda para rotas protegidas
router.beforeEach((to, from, next) => {
  const publicPages = ['/', '/login', '/register', '/products']; // Produtos é pública para visualização
  const authRequired = !publicPages.includes(to.path);
  const loggedIn = localStorage.getItem('authToken');

  // Se a rota requer autenticação e o usuário não está logado, redireciona para a página de login
  if (authRequired && !loggedIn) {
    return next('/login');
  }

  next();
});

export default router;