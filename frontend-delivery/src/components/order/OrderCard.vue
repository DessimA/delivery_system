<template>
  <div class="order-card">
    <div class="order-header">
      <h3 class="order-id">Pedido #{{ order.codigoPedido }}</h3>
      <span v-if="order.status" :class="['order-status', `order-status--${order.status.toLowerCase()}`]">{{ order.status }}</span>
    </div>
    <div class="order-details">
      <p v-if="order.dataPedido" class="order-date">Data: {{ formatDate(order.dataPedido) }}</p>
      <p class="order-total">Total: {{ formatCurrency(order.valorTotal) }}</p>
      <ul v-if="order.produtos && order.produtos.length" class="order-items">
        <li v-for="produto in order.produtos" :key="produto.idProduto">
          {{ produto.nomeProduto }}
        </li>
      </ul>
    </div>
    <slot name="delivery-tracker"></slot>
    <div class="order-actions">
      <BaseButton label="Ver Detalhes" variant="secondary" size="sm" @click="$emit('view-details', order.codigoPedido)" />
      <BaseButton label="Reordenar" variant="primary" size="sm" @click="$emit('reorder', order.codigoPedido)" />
    </div>
  </div>
</template>

<script setup>
import BaseButton from '@/components/base/BaseButton.vue';

defineProps({
  order: {
    type: Object,
    required: true,
  },
});

defineEmits(['view-details', 'reorder']);

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(value);
};

const formatDate = (dateString) => {
  const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  return new Date(dateString).toLocaleDateString('pt-BR', options);
};
</script>

<style lang="scss" scoped>
.order-card {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--color-border);
  padding-bottom: var(--spacing-sm);
}

.order-id {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-dark);
  margin: 0;
}

.order-status {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-light);

  &--pendente {
    background-color: var(--color-warning);
  }
  &--confirmado {
    background-color: var(--color-info);
  }
  &--entregue {
    background-color: var(--color-success);
  }
  &--cancelado {
    background-color: var(--color-danger);
  }
}

.order-details {
  margin-bottom: var(--spacing-md);

  p {
    margin-bottom: var(--spacing-xs);
    color: var(--color-dark);
  }
}

.order-items {
  list-style: none;
  padding-left: 0;
  margin-top: var(--spacing-sm);

  li {
    font-size: var(--font-size-md);
    color: var(--color-text-dark);
    margin-bottom: var(--spacing-xs);
  }
}

.order-actions {
  display: flex;
  gap: var(--spacing-sm);
  justify-content: flex-end;
  border-top: 1px solid var(--color-border);
  padding-top: var(--spacing-md);
}
</style>
