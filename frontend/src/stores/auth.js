import { defineStore } from 'pinia';
import { authService } from '@/services/auth.service';
import { storage } from '@/utils/storage';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: null,
    isInitialized: false,
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    userRoles: (state) => state.user?.roles || [],
    isAdmin: (state) => (state.user?.roles || []).includes('ROLE_ADMIN'),
    isRestaurant: (state) => (state.user?.roles || []).includes('ROLE_RESTAURANT'),
    isDelivery: (state) => (state.user?.roles || []).includes('ROLE_DELIVERY'),
  },

  actions: {
    async login(credentials) {
      try {
        const data = await authService.login(credentials.email, credentials.password);
        const { accessToken } = data;
        
        this.token = accessToken;
        storage.set('authToken', accessToken);
        
        await this.fetchUser();
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    async fetchUser() {
      if (!this.token) return;
      
      try {
        const data = await authService.getCurrentUser();
        this.user = data;
        storage.set('user', this.user);
      } catch (error) {
        this.logout();
        throw error;
      }
    },

    logout() {
      this.token = null;
      this.user = null;
      storage.remove('authToken');
      storage.remove('user');
    },

    setUser(userData) {
      this.user = userData;
      storage.set('user', userData);
    },

    async initializeAuth() {
      if (this.isInitialized) return;

      const savedToken = storage.get('authToken');
      const savedUser = storage.get('user');

      if (savedToken) {
        this.token = savedToken;
        this.user = savedUser;
        
        // Refresh user data in background
        this.fetchUser().catch(() => {});
      }
      
      this.isInitialized = true;
    }
  },
});