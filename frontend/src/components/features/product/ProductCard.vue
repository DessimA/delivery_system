<template>
  <div class="product-card" @click="viewProduct" role="article" :aria-label="product.name">
    <div class="product-image">
      <img
        :src="getProductImage(product)"
        :alt="product.name"
        @error="handleImageError"
        loading="lazy"
      />
    </div>

    <div class="product-info">
      <h3 class="product-name">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>

      <div class="product-footer">
        <div class="product-price">
          <span class="current-price" aria-live="polite">{{ formatCurrency(product.price) }}</span>
        </div>

        <BaseButton
          label="Adicionar"
          variant="primary"
          size="sm"
          icon="plus"
          @click.stop="addToCart"
          aria-label="Adicionar ao carrinho"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import BaseButton from '@/components/base/BaseButton.vue';

const props = defineProps({
  product: {
    type: Object,
    required: true,
    validator: (p) => !!p.id && !!p.name && p.price !== undefined
  },
});

const emit = defineEmits(['add-to-cart', 'view-product']);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
};

const getProductImage = (product) => {
  if (product.imageUrl) {
    if (!product.imageUrl.startsWith('http')) {
        // Use environment variable for backend URL if possible
        const backendUrl = import.meta.env.VITE_APP_BACKEND_URL || 'http://localhost:8080';
        return `${backendUrl}/uploads/${product.imageUrl}`;
    }
    return product.imageUrl;
  }
  return '/images/placeholder-food.svg';
};

const handleImageError = (event) => {
  event.target.src = '/images/placeholder-food.svg';
};

const addToCart = () => {
  emit('add-to-cart', props.product);
};

const viewProduct = () => {
  emit('view-product', props.product);
};
</script>

<style lang="scss" scoped>
.product-card {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--color-border);
  overflow: hidden;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-md);
  }

  &:focus-within {
    outline: 2px solid var(--color-primary);
    outline-offset: 2px;
  }
}

.product-image {
  width: 100%;
  height: 180px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .product-card:hover & img {
    transform: scale(1.05);
  }
}

.product-info {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-family: var(--font-headings);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-dark);
}

.product-description {
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
  margin-bottom: var(--spacing-md);
  flex-grow: 1;
  line-height: 1.4;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: var(--spacing-sm);
}

.product-price {
  .current-price {
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-dark);
  }
}
</style>
