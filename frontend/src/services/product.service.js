import api from '@/plugins/axios';

export const productService = {
  async getAll() {
    const response = await api.get('/products');
    return response.data;
  },

  async getById(id) {
    const response = await api.get(`/products/${id}`);
    return response.data;
  },

  async create(productData, imageFile) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    
    if (imageFile) {
      formData.append('image', imageFile);
    }

    const response = await api.post('/products', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data;
  },

  async update(id, productData) {
    const response = await api.put(`/products/${id}`, productData);
    return response.data;
  },

  async delete(id) {
    await api.delete(`/products/${id}`);
  },

  async getMyProducts() {
    const response = await api.get('/restaurant/my-products');
    return response.data;
  }
};
