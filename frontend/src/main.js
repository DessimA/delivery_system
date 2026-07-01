import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { createPinia } from 'pinia';
import { useAuthStore } from '@/stores/auth';
import api, { setupAxiosInterceptors } from './plugins/axios'; // Import api and setupAxiosInterceptors

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

app.use(pinia); // Pinia is now installed on the app

// Setup Axios interceptors after Pinia is initialized
setupAxiosInterceptors();

// Now it's safe to get the authStore instance
const authStore = useAuthStore();

// Now initialize auth and then mount the app
authStore.initializeAuth().then(() => {
  app.use(router);
  app.mount('#app');
});