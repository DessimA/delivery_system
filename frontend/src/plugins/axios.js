import axios from 'axios';
import { useAuthStore } from '@/stores/auth';
import router from '@/router';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
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
      const originalRequest = error.config;

      if (error.response) {
        const { status } = error.response;

        if (status === 401 && !originalRequest._retry) {
          // Future: Implement refresh token logic here
          authStore.logout();
          router.push('/login');
        }
      }

      return Promise.reject(error);
    }
  );
}

export default api;