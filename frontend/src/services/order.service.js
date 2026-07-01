import api from '@/plugins/axios';

export const orderService = {
  async getAll() {
    const response = await api.get('/orders');
    return response.data;
  },

  async create(orderData) {
    const response = await api.post('/orders', orderData);
    return response.data;
  },

  async getMyOrders() {
    const response = await api.get('/orders/me'); // Adjusted to English path pattern
    return response.data;
  }
};
