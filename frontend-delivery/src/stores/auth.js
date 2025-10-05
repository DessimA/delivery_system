import { defineStore } from 'pinia';
import api from '@/api'; // Use the global, pre-configured api instance

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    isInitialized: false, // To track if initial auth check is complete
  }),

  getters: {
    isAuthenticated: (state) => !!state.user,
    userRoles: (state) => state.user?.roles?.map(role => role.authority) || [],
    
    // Role checks
    isRestaurantOwner() {
      return this.userRoles.includes('RESTAURANT');
    },
    isDeliveryUser() {
      return this.userRoles.includes('DELIVERY');
    },
    isAdmin() {
      return this.userRoles.includes('ADMIN');
    },
  },

  actions: {
    async login(credentials) {
      try {
        const response = await api.post('/auth/login', credentials);
        this.token = response.data.accessToken;
        localStorage.setItem('authToken', this.token);
        
        await this.fetchUser();

      } catch (error) {
        console.error('Login failed:', error);
        this.logout();
        throw error;
      }
    },

    async fetchUser() {
      if (!this.token) {
        throw new Error('No token available to fetch user.');
      }
      try {
        const response = await api.get('/usuarios/me');
        this.user = response.data;
      } catch (error) {
        console.error('Failed to fetch user:', error);
        this.logout();
        throw error;
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      localStorage.removeItem('authToken');
    },

    setUser(newUser) {
      this.user = newUser;
    },

    async initializeAuth() {
      if (this.isInitialized) return;

      const localToken = localStorage.getItem('authToken');
      if (localToken) {
        this.token = localToken;
        try {
          await this.fetchUser();
        } catch (e) {
          // fetchUser already handles logout on failure
        }
      }
      
      this.isInitialized = true;
    },
  },
});