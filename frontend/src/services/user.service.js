import api from '@/plugins/axios';

export const userService = {
  async register(userData) {
    const response = await api.post('/users/register', userData);
    return response.data;
  },

  async updateProfile(userData) {
    const response = await api.put('/users/me', userData);
    return response.data;
  }
};
