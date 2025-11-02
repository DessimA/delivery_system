<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div v-if="loadingEstablishment">
        <h1 class="loading-h1">Carregando...</h1>
      </div>
      <div v-else-if="establishment">
        <h1>{{ establishment.nome }}</h1>
        <p>{{ establishment.endereco }}</p>
      </div>
      <BaseButton label="Adicionar Produto" icon="plus" @click="openProductModal(null)" />
    </header>

    <div v-if="loadingProducts" class="loading-state">
      <LoadingSpinner />
      <p>Carregando produtos...</p>
    </div>

    <div v-else-if="products.length > 0" class="products-table-container">
      <table class="products-table">
        <thead>
          <tr>
            <th>Produto</th>
            <th>Preço</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="product in products" :key="product.idProduto">
            <td>
              <div class="product-info">
                <img :src="product.caminhoImagem || '/images/placeholder-food.svg'" alt="Imagem do produto" class="product-image">
                <div>
                  <div class="product-name">{{ product.nomeProduto }}</div>
                  <div class="product-description">{{ product.descricao }}</div>
                </div>
              </div>
            </td>
            <td>{{ formatCurrency(product.preco) }}</td>
            <td>
              <div class="action-buttons">
                <BaseButton variant="secondary" size="sm" icon="edit-2" @click="openProductModal(product)" />
                <BaseButton variant="danger" size="sm" icon="trash-2" @click="openDeleteModal(product)" />
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <EmptyState
      v-else
      title="Nenhum produto encontrado"
      description="Comece adicionando produtos ao seu cardápio."
    />

    <!-- Modal para Adicionar/Editar Produto -->
    <ProductFormModal
      :visible="isProductModalVisible"
      :product="selectedProduct"
      :saving="saving"
      @close="closeProductModal"
      @save="handleProductSave"
    />

    <!-- Modal de Confirmação de Exclusão -->
    <BaseModal :visible="isDeleteModalVisible" @close="closeDeleteModal" title="Confirmar Exclusão">
      <p>Você tem certeza que deseja remover o produto "<strong>{{ selectedProduct?.nomeProduto }}</strong>"? Esta ação não pode ser desfeita.</p>
      <template #footer>
        <BaseButton variant="secondary" label="Cancelar" @click="closeDeleteModal" />
        <BaseButton variant="danger" label="Sim, Excluir" @click="deleteProduct" :loading="deleting" />
      </template>
    </BaseModal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import LoadingSpinner from '@/components/base/LoadingSpinner.vue';
import ProductFormModal from '@/components/ProductFormModal.vue';

const products = ref([]);
const establishment = ref(null);
const isProductModalVisible = ref(false);
const isDeleteModalVisible = ref(false);
const selectedProduct = ref(null);
const saving = ref(false);
const deleting = ref(false);

const api = useApi();
const { addNotification } = useNotifications();

const loadingProducts = ref(true);
const loadingEstablishment = ref(true);

onMounted(() => {
  fetchEstablishment();
  fetchProducts();
});

const fetchEstablishment = async () => {
  loadingEstablishment.value = true;
  try {
    const { data } = await api.get('/restaurante/meu-estabelecimento');
    establishment.value = data;
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao carregar dados do estabelecimento.' });
  } finally {
    loadingEstablishment.value = false;
  }
};

const fetchProducts = async () => {
  loadingProducts.value = true;
  try {
    const { data } = await api.get('/restaurante/meus-produtos');
    products.value = data;
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao carregar produtos.' });
  } finally {
    loadingProducts.value = false;
  }
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};

const openProductModal = (product) => {
  selectedProduct.value = product ? { ...product } : null;
  isProductModalVisible.value = true;
};

const closeProductModal = () => {
  isProductModalVisible.value = false;
  selectedProduct.value = null;
};

const openDeleteModal = (product) => {
  selectedProduct.value = product;
  isDeleteModalVisible.value = true;
};

const closeDeleteModal = () => {
  isDeleteModalVisible.value = false;
  selectedProduct.value = null;
};

const handleProductSave = async (productData) => {
  saving.value = true;
  try {
    if (productData.idProduto) {
      await api.put(`/restaurante/produtos/${productData.idProduto}`, productData);
      addNotification({ type: 'success', message: 'Produto atualizado com sucesso!' });
    } else {
      await api.post('/restaurante/produtos', productData);
      addNotification({ type: 'success', message: 'Produto adicionado com sucesso!' });
    }
    closeProductModal();
    fetchProducts();
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao salvar produto.' });
  } finally {
    saving.value = false;
  }
};

const deleteProduct = async () => {
  if (!selectedProduct.value) return;
  deleting.value = true;
  try {
    await api.delete(`/restaurante/produtos/${selectedProduct.value.idProduto}`);
    addNotification({ type: 'success', message: 'Produto removido com sucesso!' });
    closeDeleteModal();
    fetchProducts();
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao remover produto.' });
  } finally {
    deleting.value = false;
  }
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);

  h1 {
    font-family: var(--font-headings);
    font-size: var(--font-size-h2);
    margin-bottom: var(--spacing-sm);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  margin-top: var(--spacing-xxl);

  h2 {
    font-family: var(--font-headings);
    font-size: var(--font-size-h3);
    margin: 0;
  }
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-xxl) 0;
  color: var(--color-text-muted);
}

.products-table-container,
.orders-list {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  overflow: hidden;
}

.products-table {
  width: 100%;
  border-collapse: collapse;

  th, td {
    padding: var(--spacing-md);
    text-align: left;
    border-bottom: 1px solid var(--color-border);
  }

  th {
    background-color: var(--color-background);
    font-weight: var(--font-weight-semibold);
    color: var(--color-text-muted);
    font-size: var(--font-size-sm);
    text-transform: uppercase;
  }

  tbody tr:last-child td {
    border-bottom: none;
  }

  tbody tr:hover {
    background-color: var(--color-background);
  }
}

.product-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  object-fit: cover;
}

.product-name {
  font-weight: var(--font-weight-semibold);
}

.product-description {
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
}
</style>