import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';
const API_TIMEOUT = 15000;

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
});

export function setupAxiosInterceptors() {
  const authStore = useAuthStore();

  api.interceptors.request.use(
    (config) => {
      if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  api.interceptors.response.use(
    (response) => response,
    async (error) => {
      if (error.response && error.response.status === 401) {
        authStore.logout();
        router.push('/login');
      }

      return Promise.reject(error);
    }
  );
}

export default api;