<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1>Meu Cardápio</h1>
      <BaseButton @click="showProductModal()">Adicionar Produto</BaseButton>
    </div>

    <div v-if="loading" class="loading-state">
      <p>Carregando produtos...</p>
    </div>

    <div v-else-if="products.length > 0" class="products-list">
      <div class="list-header">
        <span>Produto</span>
        <span>Preço</span>
        <span>Ações</span>
      </div>
      <div v-for="product in products" :key="product.idProduto" class="product-item">
        <span class="product-name">{{ product.nomeProduto }}</span>
        <span class="product-price">{{ formatCurrency(product.preco) }}</span>
        <div class="actions">
          <BaseButton size="sm" variant="secondary" @click="showProductModal(product)">Editar</BaseButton>
          <BaseButton size="sm" variant="danger" @click="handleDeleteProduct(product.idProduto)">Excluir</BaseButton>
        </div>
      </div>
    </div>
    
    <EmptyState v-else title="Nenhum produto encontrado" description="Comece adicionando produtos ao seu cardápio." />

    <!-- Modal para Adicionar/Editar Produto -->
    <BaseModal v-model="isModalOpen" :title="modalTitle" @close="isModalOpen = false">
      <form @submit.prevent="handleSubmitProduct" class="product-form">
        <BaseInput v-model="currentProduct.nomeProduto" label="Nome do Produto" required />
        <BaseInput v-model="currentProduct.descricao" label="Descrição" required />
        <BaseInput v-model="currentProduct.preco" label="Preço" type="number" step="0.01" required />
        
        <!-- TODO: Implementar a lógica de upload de arquivo e envio como multipart/form-data -->
        <label for="productImage">Imagem do Produto</label>
        <input type="file" @change="handleFileSelect" id="productImage" accept="image/*" />

      </form>
      <template #footer>
        <BaseButton variant="secondary" @click="isModalOpen = false">Cancelar</BaseButton>
        <BaseButton type="submit" @click="handleSubmitProduct" :loading="isSubmitting">Salvar</BaseButton>
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import api from '@/api';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import EmptyState from '@/components/base/EmptyState.vue';

const products = ref([]);
const isModalOpen = ref(false);
const isEditing = ref(false);
const imageFile = ref(null);

const currentProduct = reactive({
  idProduto: null,
  nomeProduto: '',
  descricao: '',
  preco: 0,
});

const { loading, execute: fetchProductsExecute } = useApi();
const { loading: isSubmitting, execute: submitExecute } = useApi();
const { addNotification } = useNotifications();

const modalTitle = computed(() => isEditing.value ? 'Editar Produto' : 'Adicionar Produto');

onMounted(fetchProducts);

async function fetchProducts() {
  try {
    const response = await fetchProductsExecute(() => api.get('/restaurante/me/produtos'));
    products.value = response.data;
  } catch (err) {
    console.error('Failed to fetch products:', err);
  }
}

function showProductModal(product = null) {
  imageFile.value = null;
  if (product) {
    isEditing.value = true;
    Object.assign(currentProduct, product);
  } else {
    isEditing.value = false;
    Object.assign(currentProduct, { idProduto: null, nomeProduto: '', descricao: '', preco: 0 });
  }
  isModalOpen.value = true;
}

function handleFileSelect(event) {
  imageFile.value = event.target.files[0];
}

async function handleSubmitProduct() {
  const productData = {
    nomeProduto: currentProduct.nomeProduto,
    descricao: currentProduct.descricao,
    preco: currentProduct.preco,
  };

  // O backend espera multipart/form-data para criar, mas JSON para editar.
  // Esta é uma limitação do backend que precisa ser tratada.
  try {
    if (isEditing.value) {
      // PUT request com JSON, pois o backend não suporta multipart/form-data para PUT.
      await submitExecute(() => api.put(`/produtos/${currentProduct.idProduto}`, productData));
      addNotification({ type: 'success', message: 'Produto atualizado com sucesso!' });
    } else {
      // POST request com multipart/form-data
      const formData = new FormData();
      formData.append('produto', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
      if (imageFile.value) {
        formData.append('imagem', imageFile.value);
      }
      await submitExecute(() => api.post('/produtos', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      }));
      addNotification({ type: 'success', message: 'Produto criado com sucesso!' });
    }
    isModalOpen.value = false;
    fetchProducts(); // Recarrega a lista
  } catch (err) {
    console.error('Failed to submit product:', err);
  }
}

async function handleDeleteProduct(productId) {
  if (confirm('Tem certeza que deseja excluir este produto?')) {
    try {
      await submitExecute(() => api.delete(`/produtos/${productId}`));
      addNotification({ type: 'success', message: 'Produto excluído com sucesso!' });
      fetchProducts(); // Recarrega a lista
    } catch (err) {
      console.error('Failed to delete product:', err);
    }
  }
}

const formatCurrency = (value) => {
  if (typeof value !== 'number') return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: var(--spacing-lg) var(--spacing-md);
  max-width: 1000px;
  margin: 0 auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.products-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.list-header, .product-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr;
  gap: var(--spacing-md);
  align-items: center;
  padding: var(--spacing-sm) var(--spacing-md);
}

.list-header {
  font-weight: var(--font-weight-bold);
  color: var(--color-dark);
  border-bottom: 2px solid var(--color-border);
}

.product-item {
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s ease;

  &:hover {
    box-shadow: var(--shadow-md);
  }
}

.actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
}

.product-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
</style>