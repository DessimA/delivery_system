import api from '@/plugins/axios';

export const establishmentService = {
  async getAll() {
    const response = await api.get('/establishments');
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/establishments/${id}`);
    return response.data;
  },

  async getProducts(id) {
    const response = await api.get(`/establishments/${id}/products`);
    return response.data;
  }
};
