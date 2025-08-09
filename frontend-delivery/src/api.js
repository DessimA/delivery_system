import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api'; // URL da sua API Spring Boot

const api = axios.create({
  baseURL: API_BASE_URL,
});

// Interceptor para adicionar o token de autenticação (se existir)
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken'); // Ou sessionStorage
    if (token) {
      config.headers.Authorization = `Basic ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api;
