import api from '@/plugins/axios';

const publicApi = {
  post(path, data) {
    return fetch(import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api' + path, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    });
  }
};

export const paymentService = {
  async generatePix(orderId, amount) {
    const response = await api.post('/payments/pix/generate', { orderId, amount });
    return response.data;
  },

  async getStatus(orderId) {
    const response = await api.get(`/payments/${orderId}/status`);
    return response.data;
  },

  async confirm(transactionId) {
    const baseUrl = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080/api';
    const response = await fetch(`${baseUrl}/payments/confirm/${transactionId}`, {
      method: 'POST'
    });
    if (!response.ok) {
      const error = new Error('Payment confirmation failed');
      error.response = response;
      throw error;
    }
  }
};
