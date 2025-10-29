import axios from 'axios';
import { useAuthStore } from './stores/auth'; // Importe o store de autenticação
import { useNotifications } from './composables/useNotifications'; // Importe o composable de notificações
import router from './router'; // Importe o roteador

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api'; // Fallback para desenvolvimento local

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Interceptor para adicionar o token de autenticação (se existir)
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    console.log('API Request:', config.method, config.url, config.data);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de erros de resposta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const { addNotification } = useNotifications();
    const authStore = useAuthStore();

    if (error.response) {
      const status = error.response.status;
      const message = error.response.data?.message || `Erro ${status}: ${error.response.statusText}`;

      if (status === 401) {
        // Unauthorized - token expirado ou inválido
        authStore.logout();
        addNotification('error', 'Sessão expirada. Por favor, faça login novamente.');
        router.push('/login');
      } else if (status === 403) {
        // Forbidden - sem permissão
        addNotification('error', 'Você não tem permissão para realizar esta ação.');
      } else if (status >= 500) {
        // Server error
        addNotification('error', 'Erro interno do servidor. Tente novamente mais tarde.');
      } else {
        // Other client errors (4xx)
        addNotification('error', message);
      }
    } else if (error.request) {
      // The request was made but no response was received
      addNotification('error', 'Sem resposta do servidor. Verifique sua conexão.');
    } else {
      // Something happened in setting up the request that triggered an Error
      addNotification('error', 'Erro ao configurar a requisição.');
    }

    return Promise.reject(error);
  }
);

export default api;
