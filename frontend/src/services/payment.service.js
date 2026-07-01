import api from '@/plugins/axios';

export const paymentService = {
  async generatePix(orderId, amount) {
    const response = await api.post('/payments/pix/generate', { orderId, amount });
    return response.data;
  },

  async getStatus(orderId) {
    const response = await api.get(`/payments/${orderId}/status`);
    return response.data;
  }
};
