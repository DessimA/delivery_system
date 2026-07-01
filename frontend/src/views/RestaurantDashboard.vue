<template>
  <div class="dashboard-container">
    <header class="dashboard-header">
      <div v-if="loadingEstablishment">
        <h1 class="loading-h1">Carregando...</h1>
      </div>
      <div v-else-if="establishment">
        <h1>{{ establishment.name }}</h1>
        <p>{{ establishment.address }}</p>
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
          <tr v-for="product in products" :key="product.id">
            <td>
              <div class="product-info">
                <img :src="product.imageUrl || '/images/placeholder-food.svg'" alt="Imagem do produto" class="product-image">
                <div>
                  <div class="product-name">{{ product.name }}</div>
                  <div class="product-description">{{ product.description }}</div>
                </div>
              </div>
            </td>
            <td>{{ formatCurrency(product.price) }}</td>
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
      v-model="isProductModalVisible"
      :product="selectedProduct"
      :saving="saving"
      @save="handleProductSave"
    />

    <!-- Modal de Confirmação de Exclusão -->
    <BaseModal v-model="isDeleteModalVisible" title="Confirmar Exclusão">
      <p>Você tem certeza que deseja remover o produto "<strong>{{ selectedProduct?.name }}</strong>"? Esta ação não pode ser desfeita.</p>
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
import { establishmentService } from '@/services/establishment.service';
import { productService } from '@/services/product.service';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import LoadingSpinner from '@/components/base/LoadingSpinner.vue';
import ProductFormModal from '@/components/features/product/ProductFormModal.vue';

const products = ref([]);
const establishment = ref(null);
const isProductModalVisible = ref(false);
const isDeleteModalVisible = ref(false);
const selectedProduct = ref(null);
const saving = ref(false);
const deleting = ref(false);

const { loading: loadingEstablishment, execute: executeEstablishment } = useApi();
const { loading: loadingProducts, execute: executeProducts } = useApi();
const { addNotification } = useNotifications();

onMounted(() => {
  fetchEstablishment();
  fetchProducts();
});

const fetchEstablishment = async () => {
  try {
    const response = await executeEstablishment(() => establishmentService.getMyEstablishment());
    establishment.value = response;
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao carregar dados do estabelecimento.' });
    console.error('Failed to fetch establishment:', error);
  }
};

const fetchProducts = async () => {
  try {
    const response = await executeProducts(() => productService.getMyProducts());
    products.value = response;
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao carregar produtos.' });
    console.error('Failed to fetch products:', error);
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
    const payload = {
      name: productData.name,
      description: productData.description,
      price: productData.price,
      establishmentId: productData.establishmentId
    };

    if (productData.id) {
      await executeProducts(() => productService.update(productData.id, payload));
      addNotification({ type: 'success', message: 'Produto atualizado com sucesso!' });
    } else {
      await executeProducts(() => productService.create(payload, productData.imageFile));
      addNotification({ type: 'success', message: 'Produto adicionado com sucesso!' });
    }
    closeProductModal();
    fetchProducts();
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao salvar produto.' });
    console.error('Failed to save product:', error);
  } finally {
    saving.value = false;
  }
};

const deleteProduct = async () => {
  if (!selectedProduct.value) return;
  deleting.value = true;
  try {
    await executeProducts(() => productService.delete(selectedProduct.value.id));
    addNotification({ type: 'success', message: 'Produto removido com sucesso!' });
    closeDeleteModal();
    fetchProducts();
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao remover produto.' });
    console.error('Failed to remove product:', error);
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