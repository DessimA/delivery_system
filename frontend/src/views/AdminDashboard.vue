<template>
  <div class="admin-dashboard-container">
    <header class="dashboard-header">
      <h1>Painel do Administrador</h1>
      <p>Gerencie produtos, usuários e pedidos do sistema.</p>
    </header>

    <div class="dashboard-content">
      <section class="product-management">
        <div class="section-header">
          <h2>Gerenciamento de Produtos</h2>
          <BaseButton label="Adicionar Produto" icon="plus" @click="openProductModal(null)" />
        </div>

        <div v-if="loading" class="loading-state">
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
                    <img :src="getProductImage(product)" alt="Imagem do produto" class="product-image">
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
          description="Comece adicionando um novo produto para exibi-lo aqui."
        />
      </section>
    </div>

    <!-- Modal para Adicionar/Editar Produto -->
    <ProductFormModal
      v-model="isProductModalVisible"
      :product="selectedProduct"
      :establishments="establishments"
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
import { BACKEND_URL } from '@/config/env';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';
import { productService } from '@/services/product.service';
import { establishmentService } from '@/services/establishment.service';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseModal from '@/components/base/BaseModal.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import LoadingSpinner from '@/components/base/LoadingSpinner.vue';
import ProductFormModal from '@/components/features/product/ProductFormModal.vue';

const { loading, execute } = useApi();
const { addNotification } = useNotifications();

const products = ref([]);
const establishments = ref([]);
const isProductModalVisible = ref(false);
const isDeleteModalVisible = ref(false);
const selectedProduct = ref(null);
const saving = ref(false);
const deleting = ref(false);

const fetchProducts = async () => {
  try {
    const data = await execute(() => productService.getAll());
    products.value = data;
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao carregar produtos.' });
  }
};

const fetchEstablishments = async () => {
  try {
    const data = await establishmentService.getAll();
    establishments.value = data;
  } catch (error) {
    console.error("Failed to fetch establishments", error);
  }
};

onMounted(() => {
  fetchProducts();
  fetchEstablishments();
});

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};

const getProductImage = (product) => {
  if (product.imageUrl) {
    if (!product.imageUrl.startsWith('http')) {
        return `${BACKEND_URL}/uploads/${product.imageUrl}`;
    }
    return product.imageUrl;
  }
  return '/images/placeholder-food.svg';
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

const handleProductSave = async (data) => {
  saving.value = true;
  try {
    const productData = {
      name: data.name,
      description: data.description,
      price: data.price,
      establishmentId: data.establishmentId
    };

    if (data.id) { // Update
      await execute(() => productService.update(data.id, productData));
      addNotification({ type: 'success', message: 'Produto atualizado com sucesso!' });
    } else {
      // Create - Multipart
      await execute(() => productService.create(productData, data.imageFile));
      addNotification({ type: 'success', message: 'Produto adicionado com sucesso!' });
    }
    closeProductModal();
    fetchProducts();
  } catch (error) {
    addNotification({ type: 'error', message: 'Falha ao salvar produto.' });
    console.error(error);
  } finally {
    saving.value = false;
  }
};

const deleteProduct = async () => {
  if (!selectedProduct.value) return;
  deleting.value = true;
  try {
    await execute(() => productService.delete(selectedProduct.value.id)); 
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
.admin-dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.dashboard-header {
  margin-bottom: var(--spacing-xl);
  h1 {
    font-family: var(--font-headings);
    font-size: var(--font-size-h2);
    margin-bottom: var(--spacing-sm);
  }
  p {
    color: var(--color-text-muted);
    font-size: var(--font-size-lg);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);

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

.products-table-container {
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
}
</style>
