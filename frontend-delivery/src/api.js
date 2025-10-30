import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api'; // Fallback para desenvolvimento local

const api = axios.create({
  baseURL: API_BASE_URL,
});

export default api;
