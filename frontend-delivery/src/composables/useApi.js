import { ref } from 'vue';

export function useApi() {
  const loading = ref(false);
  const error = ref(null);
  
  const execute = async (apiCall) => {
    try {
      loading.value = true;
      error.value = null;
      const result = await apiCall();
      return result;
    } catch (err) {
      error.value = err.response?.data?.message || 'Erro desconhecido';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  return { loading, error, execute };
}
