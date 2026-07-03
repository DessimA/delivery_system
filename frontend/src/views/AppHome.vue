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
          id="search-products"
          v-model="searchTerm"
          placeholder="Buscar produtos..."
          icon="search"
        />
      </div>
      <!-- <div class="filter-chips">
        <FilterChip
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :active="activeCategory === category.id"
          @click="filterByCategory(category.id)"
        />
      </div> -->
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
import { ref, onMounted, computed, watch } from 'vue';
import { useApi } from '@/composables/useApi';
import { useCartStore } from '@/stores/cart';
import { useDebounce } from '@/composables/useDebounce';
import { useNotifications } from '@/composables/useNotifications';
import { productService } from '@/services/product.service';

import BaseButton from '@/components/base/BaseButton.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import ProductCard from '@/components/features/product/ProductCard.vue';
import ProductCardSkeleton from '@/components/features/product/ProductCardSkeleton.vue';

const products = ref([]);
const searchTerm = ref('');
const searchQuery = ref('');
const productsSection = ref(null);

const { loading, execute } = useApi();
const cartStore = useCartStore();
const { addNotification } = useNotifications();

const debouncedSearch = useDebounce((value) => {
  searchQuery.value = value;
}, 300);

watch(searchTerm, (value) => {
  debouncedSearch(value);
});

onMounted(() => {
  fetchProducts();
});

const fetchProducts = async () => {
  try {
    const data = await execute(() => productService.getAll());
    products.value = data;
  } catch (err) {
    console.error('Error fetching products:', err);
  }
};

const filteredProducts = computed(() => {
  let filtered = products.value;

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
      p => p.name.toLowerCase().includes(query) || p.description.toLowerCase().includes(query)
    );
  }

  return filtered;
});

const scrollToProducts = () => {
  if (productsSection.value) {
    productsSection.value.scrollIntoView({ behavior: 'smooth' });
  }
};

const addToCart = (product) => {
  cartStore.addItem(product);
  addNotification({ type: 'success', message: `${product.name} adicionado ao carrinho!` });
};
</script>

<style lang="scss" scoped>
.home-container {
  animation: fadeIn 0.5s ease-out;
}

.hero-section {
  background-color: var(--color-surface);
  padding: var(--spacing-xxl) var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
  display: grid;
  align-items: center;
  gap: var(--spacing-xl);

  @media (min-width: 768px) {
    grid-template-columns: 1fr 1fr;
    padding: 6rem var(--spacing-xl);
  }
}

.hero-content {
  text-align: center;
  max-width: 550px;
  justify-self: center;

  @media (min-width: 768px) {
    text-align: left;
    justify-self: start;
  }

  h1 {
    font-family: var(--font-headings);
    font-size: clamp(2.5rem, 5vw, 3.5rem); // Responsive font size
    font-weight: 700;
    line-height: 1.2;
    margin-bottom: var(--spacing-md);
    color: var(--color-text-dark);
  }

  p {
    font-size: var(--font-size-lg);
    color: var(--color-text-muted);
    margin-bottom: var(--spacing-xl);
    max-width: 450px;
  }
}

.hero-image {
  display: flex;
  justify-content: center;
  align-items: center;

  img {
    max-width: 100%;
    height: auto;
    max-height: 400px;
  }
}

.filters-section {
  max-width: 700px;
  margin: 0 auto var(--spacing-xl);
  padding: 0 var(--spacing-lg);
}

.products-section {
  max-width: 1280px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}

.section-header {
  text-align: center;
  margin-bottom: var(--spacing-xl);

  h2 {
    font-family: var(--font-headings);
    font-size: var(--font-size-h2);
    font-weight: 700;
    margin-bottom: var(--spacing-sm);
  }

  p {
    color: var(--color-text-muted);
    font-size: var(--font-size-lg);
  }
}

.products-grid {
  display: grid;
  gap: var(--spacing-lg);

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
