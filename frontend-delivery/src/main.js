import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // Importe o roteador

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js'; // Para funcionalidades JS do Bootstrap (dropdowns, etc.)

createApp(App).use(router).mount('#app'); // Use o roteador