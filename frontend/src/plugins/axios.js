import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import { useNotifications } from '@/composables/useNotifications';
import router from '@/router';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
});

export function setupAxiosInterceptors() {
  // Ensure Pinia store is accessible
  const authStore = useAuthStore();
  const { addNotification } = useNotifications();

  api.interceptors.request.use(
    (config) => {
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`;
        console.log('JWT Token in Interceptor:', authStore.token);
        console.log('Authorization Header:', config.headers.Authorization);
      }
      console.log('API Request:', config.method, config.url, config.data);
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response) {
        const status = error.response.status;
        const message = error.response.data?.message || `Erro ${status}: ${error.response.statusText}`;

        if (status === 401) {
          authStore.logout();
          addNotification('error', 'Sessão expirada. Por favor, faça login novamente.');
          router.push('/login');
        } else if (status === 403) {
          addNotification('error', 'Você não tem permissão para realizar esta ação.');
        } else if (status >= 500) {
          addNotification('error', 'Erro interno do servidor. Tente novamente mais tarde.');
        } else {
          addNotification('error', message);
        }
      } else if (error.request) {
        addNotification('error', 'Sem resposta do servidor. Verifique sua conexão.');
      } else {
        addNotification('error', 'Erro ao configurar a requisição.');
      }

      return Promise.reject(error);
    }
  );
}

export default api;