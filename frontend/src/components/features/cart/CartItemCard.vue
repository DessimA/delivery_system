<template>
  <div class="cart-item-card" role="listitem">
    <div class="item-image">
      <img :src="item.imageUrl || '/images/placeholder-food.svg'" :alt="item.name" loading="lazy" />
    </div>
    <div class="item-details">
      <h3 class="item-name">{{ item.name }}</h3>
      <p class="item-price">{{ formatCurrency(item.price) }}</p>
      <div class="item-quantity-control">
        <BaseButton 
          size="sm" 
          variant="light" 
          icon="minus" 
          @click="updateQuantity(item.quantity - 1)" 
          aria-label="Diminuir quantidade"
        />
        <span class="quantity-display" aria-live="polite">{{ item.quantity }}</span>
        <BaseButton 
          size="sm" 
          variant="light" 
          icon="plus" 
          @click="updateQuantity(item.quantity + 1)" 
          aria-label="Aumentar quantidade"
        />
      </div>
    </div>
    <div class="item-actions">
      <BaseButton 
        variant="danger" 
        size="sm" 
        icon="trash" 
        @click="removeItem" 
        aria-label="Remover item"
      />
    </div>
  </div>
</template>

<script setup>
import BaseButton from '@/components/base/BaseButton.vue';

const props = defineProps({
  item: {
    type: Object,
    required: true,
    validator: (i) => !!i.id && !!i.name && i.price !== undefined && i.quantity !== undefined
  },
});

const emit = defineEmits(['update-quantity', 'remove-item']);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
};

const updateQuantity = (newQuantity) => {
  emit('update-quantity', props.item.id, newQuantity);
};

const removeItem = () => {
  emit('remove-item', props.item.id);
};
</script>

<style lang="scss" scoped>
.cart-item-card {
  display: flex;
  align-items: center;
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  gap: var(--spacing-md);

  @media (max-width: 600px) {
    flex-wrap: wrap;
    justify-content: space-between;
  }
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.item-details {
  flex-grow: 1;

  @media (max-width: 600px) {
    width: 100%;
    margin-bottom: var(--spacing-sm);
  }
}

.item-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--spacing-xs);
  color: var(--color-text-dark);
}

.item-price {
  font-size: var(--font-size-md);
  color: var(--color-dark);
  margin-bottom: var(--spacing-sm);
}

.item-quantity-control {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);

  .quantity-display {
    font-size: var(--font-size-md);
    font-weight: var(--font-weight-medium);
    min-width: 24px;
    text-align: center;
  }
}

.item-actions {
  flex-shrink: 0;

  @media (max-width: 600px) {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
