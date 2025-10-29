import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import { useAuthStore } from '@/stores/auth';

// UI Framework: Bootstrap + BootstrapVueNext
import { createBootstrap } from 'bootstrap-vue-next';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-vue-next/dist/bootstrap-vue-next.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

// Global Styles
import './styles/global.scss';

// Create and configure the app
const app = createApp(App);
const pinia = createPinia();

app.use(pinia);

// Initialize auth store before mounting the app
const authStore = useAuthStore();
authStore.initializeAuth().then(() => {
  app.use(router);
  app.use(createBootstrap());
  app.mount('#app');
});