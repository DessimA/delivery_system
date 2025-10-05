<template>
  <div class="product-card" @click="viewProduct">
    <div class="product-image">
      <img
        :src="product.caminhoImagem || '/images/placeholder-food.svg'"
        :alt="product.nomeProduto"
        @error="handleImageError"
      />
      <div class="product-badge" v-if="product.promocao">
        <span>-{{ product.desconto }}%</span>
      </div>
    </div>

    <div class="product-info">
      <h3 class="product-name">{{ product.nomeProduto }}</h3>
      <p class="product-description">{{ product.descricao }}</p>

      <div class="product-footer">
        <div class="product-price">
          <span class="current-price">{{ formatCurrency(product.preco) }}</span>
          <span v-if="product.precoOriginal" class="original-price">
            {{ formatCurrency(product.precoOriginal) }}
          </span>
        </div>

        <BaseButton
          variant="primary"
          size="sm"
          icon="plus"
          @click.stop="addToCart"
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
  },
});

const emit = defineEmits(['add-to-cart', 'view-product']);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
};

const handleImageError = (event) => {
  event.target.src = '/images/placeholder-food.svg';
};

const addToCart = () => {
  // Apenas emite o evento. O componente pai cuidará da lógica.
  emit('add-to-cart', props.product);
};

const viewProduct = () => {
  emit('view-product', props.product);
  // Opcionalmente, navegar para a página de detalhes do produto
  // router.push(`/products/${props.product.idProduto}`);
};
</script>

<style lang="scss" scoped>
.product-card {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;

  &:hover {
    transform: translateY(-5px);
    box-shadow: var(--shadow-md);
  }
}

.product-image {
  position: relative;
  width: 100%;
  height: 180px; /* Fixed height for images */
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--color-surface);

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .product-badge {
    position: absolute;
    top: var(--spacing-sm);
    left: var(--spacing-sm);
    background-color: var(--color-danger);
    color: var(--color-text-light);
    padding: var(--spacing-xs) var(--spacing-sm);
    border-radius: var(--radius-md);
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-bold);
  }
}

.product-info {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.product-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-dark);
}

.product-description {
  font-size: var(--font-size-sm);
  color: var(--color-dark);
  margin-bottom: var(--spacing-md);
  flex-grow: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* Limit to 2 lines */
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-sm);
}

.product-price {
  display: flex;
  flex-direction: column;

  .current-price {
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-bold);
    color: var(--color-primary);
  }

  .original-price {
    font-size: var(--font-size-sm);
    color: var(--color-dark);
    text-decoration: line-through;
  }
}
</style>
