import api from '@/plugins/axios';

export const deliveryService = {
  async getAvailable() {
    const response = await api.get('/deliveries/available');
    return response.data;
  },

  async getMine() {
    const response = await api.get('/deliveries/mine');
    return response.data;
  },

  async accept(id) {
    const response = await api.post(`/deliveries/${id}/accept`);
    return response.data;
  },

  async updateStatus(id, status) {
    const response = await api.put(`/deliveries/${id}/status`, null, { params: { status } });
    return response.data;
  }
};
