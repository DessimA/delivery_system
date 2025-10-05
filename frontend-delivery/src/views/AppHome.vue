<template>
  <div class="home-container">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>Delivery rápido e saboroso</h1>
        <p>Peça seus pratos favoritos com entrega garantida</p>
        <BaseButton label="Ver Cardápio" size="lg" @click="scrollToProducts" />
      </div>
      <div class="hero-image">
        <img src="/images/hero-delivery.svg" alt="Delivery" />
      </div>
    </section>

    <!-- Filters and Search -->
    <section class="filters-section">
      <div class="search-bar">
        <BaseInput
          v-model="searchQuery"
          placeholder="Buscar produtos..."
          icon="search"
          @input="handleSearch"
        />
      </div>
      <div class="filter-chips">
        <FilterChip
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :active="activeCategory === category.id"
          @click="filterByCategory(category.id)"
        />
      </div>
    </section>

    <!-- Product Grid -->
    <section class="products-section" ref="productsSection">
      <div class="section-header">
        <h2>Cardápio</h2>
        <p>{{ filteredProducts.length }} produtos disponíveis</p>
      </div>

      <div v-if="loading" class="products-grid">
        <ProductCardSkeleton v-for="n in 8" :key="n" />
      </div>

      <div v-else-if="filteredProducts.length > 0" class="products-grid">
        <ProductCard
          v-for="product in filteredProducts"
          :key="product.id"
          :product="product"
          @add-to-cart="addToCart"
        />
      </div>

      <EmptyState
        v-else
        title="Nenhum produto encontrado"
        description="Tente buscar por outro termo ou categoria."
        icon="search-x"
      />
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '@/api';
import { useApi } from '@/composables/useApi';
import { useCartStore } from '@/stores/cart';
import { useNotifications } from '@/composables/useNotifications';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import FilterChip from '@/components/base/FilterChip.vue';
import ProductCard from '@/components/product/ProductCard.vue';
import ProductCardSkeleton from '@/components/product/ProductCardSkeleton.vue';

const products = ref([]);
// TODO: As categorias devem ser buscadas da API quando o backend suportar essa funcionalidade.
const categories = ref([
  { id: 1, name: 'Pizzas' },
  { id: 2, name: 'Hambúrgueres' },
  { id: 3, name: 'Bebidas' },
  { id: 4, name: 'Sobremesas' },
]);
const searchQuery = ref('');
const activeCategory = ref(null);
const productsSection = ref(null);

const { loading, execute } = useApi();
const cartStore = useCartStore();
const { addNotification } = useNotifications();

onMounted(() => {
  fetchProducts();
});

const fetchProducts = async () => {
  try {
    const response = await execute(() => api.get('/produtos'));
    products.value = response.data;
  } catch (err) {
    // O interceptor global em api.js já exibe uma notificação de erro.
    console.error('Error fetching products:', err);
  }
};

const filteredProducts = computed(() => {
  let filtered = products.value;

  // TODO: A filtragem por categoria não está funcional pois o backend não fornece
  // o `categoriaId` no DTO do produto. Esta lógica é um placeholder.
  if (activeCategory.value) {
    // filtered = filtered.filter(p => p.categoriaId === activeCategory.value);
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
      p => p.nomeProduto.toLowerCase().includes(query) || p.descricao.toLowerCase().includes(query)
    );
  }

  return filtered;
});

const filterByCategory = (categoryId) => {
  // TODO: Descomentar e ajustar quando a filtragem por categoria for implementada no backend.
  // activeCategory.value = categoryId === activeCategory.value ? null : categoryId;
  addNotification({ type: 'info', message: 'Filtro por categoria ainda não implementado.' });
};

const scrollToProducts = () => {
  if (productsSection.value) {
    productsSection.value.scrollIntoView({ behavior: 'smooth' });
  }
};

const addToCart = (product) => {
  cartStore.addItem(product);
  addNotification({ type: 'success', message: `${product.nomeProduto} adicionado ao carrinho!` });
};
</script>

<style lang="scss" scoped>
.home-container {
  padding-bottom: var(--spacing-xl);
}

.hero-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  background-color: var(--color-primary);
  color: var(--color-text-light);
  padding: var(--spacing-xxl) var(--spacing-md);
  border-bottom-left-radius: var(--radius-lg);
  border-bottom-right-radius: var(--radius-lg);
  margin-bottom: var(--spacing-xl);

  @media (min-width: 768px) {
    flex-direction: row;
    text-align: left;
    padding: var(--spacing-xxl) var(--spacing-xl);
    justify-content: space-between;
  }
}

.hero-content {
  max-width: 600px;
  margin-bottom: var(--spacing-lg);

  h1 {
    font-size: var(--font-size-h1);
    margin-bottom: var(--spacing-md);
    color: inherit;
  }

  p {
    font-size: var(--font-size-lg);
    margin-bottom: var(--spacing-xl);
    color: inherit;
  }

  @media (min-width: 768px) {
    margin-bottom: 0;
    margin-right: var(--spacing-xl);
  }
}

.hero-image {
  img {
    max-width: 100%;
    height: auto;
    max-height: 300px;
  }
}

.filters-section {
  padding: 0 var(--spacing-md);
  margin-bottom: var(--spacing-xl);

  @media (min-width: 768px) {
    padding: 0 var(--spacing-xl);
  }
}

.search-bar {
  margin-bottom: var(--spacing-lg);
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  justify-content: center;

  @media (min-width: 768px) {
    justify-content: flex-start;
  }
}

.products-section {
  padding: 0 var(--spacing-md);

  @media (min-width: 768px) {
    padding: 0 var(--spacing-xl);
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: var(--spacing-lg);

  h2 {
    font-size: var(--font-size-h2);
    margin: 0;
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-dark);
    margin: 0;
  }
}

.products-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-md);

  @media (min-width: 640px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (min-width: 1280px) {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>
