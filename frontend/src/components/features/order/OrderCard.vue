<template>
  <div class="order-card" role="article" :aria-label="'Pedido #' + order.id">
    <div class="order-card__header">
      <div class="order-info">
        <span class="order-id">Pedido #{{ order.id }}</span>
        <span class="order-date">{{ formatDate(order.orderDate) }}</span>
      </div>
      <div :class="['order-status', order.status.toLowerCase()]">
        {{ formatStatus(order.status) }}
      </div>
    </div>

    <div class="order-card__content">
      <div class="order-items">
        <p v-for="item in order.products" :key="item.id" class="order-item">
          <span class="item-quantity">1x</span> {{ item.name }}
        </p>
      </div>
      <div class="order-address">
        <BaseIcon name="map-pin" size="16" />
        <span>{{ order.deliveryAddress }}</span>
      </div>
    </div>

    <div class="order-card__footer">
      <div class="order-total">
        <span class="label">Total</span>
        <span class="value">{{ formatCurrency(order.total) }}</span>
      </div>
      <div class="order-actions">
        <BaseButton label="Ver Detalhes" variant="light" size="sm" @click="$emit('view-details', order.id)" />
        <BaseButton label="Refazer Pedido" variant="secondary" size="sm" @click="$emit('reorder', order.id)" />
      </div>
    </div>

    <slot name="delivery-tracker"></slot>
  </div>
</template>

<script setup>
import BaseButton from '@/components/base/BaseButton.vue';
import BaseIcon from '@/components/base/BaseIcon.vue';

const props = defineProps({
  order: {
    type: Object,
    required: true,
  },
});

defineEmits(['view-details', 'reorder']);

const formatDate = (dateString) => {
  if (!dateString) return 'Data não disponível';
  try {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return 'Data inválida';
  }
};

const formatCurrency = (value) => {
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};

const formatStatus = (status) => {
  const statuses = {
    'WAITING_PAYMENT': 'Aguardando Pagamento',
    'PAID': 'Pago',
    'PREPARING': 'Em Preparo',
    'IN_TRANSIT': 'A Caminho',
    'DELIVERED': 'Entregue',
    'CANCELLED': 'Cancelado'
  };
  return statuses[status] || status;
};
</script>

<style lang="scss" scoped>
.order-card {
  background-color: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  padding: var(--spacing-lg);
  box-shadow: var(--shadow-sm);

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: var(--spacing-md);
    padding-bottom: var(--spacing-md);
    border-bottom: 1px solid var(--color-border);

    .order-id {
      display: block;
      font-weight: var(--font-weight-bold);
      color: var(--color-text-dark);
    }
    .order-date {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
    }
  }

  .order-status {
    padding: 0.25rem 0.75rem;
    border-radius: var(--radius-full);
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-semibold);
    
    &.waiting_payment { background-color: #fff3cd; color: #856404; }
    &.paid { background-color: #d4edda; color: #155724; }
    &.preparing { background-color: #cce5ff; color: #004085; }
    &.in_transit { background-color: #d6d8db; color: #383d41; }
    &.delivered { background-color: #d1ecf1; color: #0c5460; }
    &.cancelled { background-color: #f8d7da; color: #721c24; }
  }

  &__content {
    margin-bottom: var(--spacing-lg);
    
    .order-items {
      margin-bottom: var(--spacing-md);
      .order-item {
        margin: 0 0 0.25rem;
        font-size: var(--font-size-md);
      }
    }

    .order-address {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
    }
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .order-total {
      .label {
        display: block;
        font-size: var(--font-size-xs);
        text-transform: uppercase;
        color: var(--color-text-muted);
      }
      .value {
        font-size: var(--font-size-lg);
        font-weight: var(--font-weight-bold);
        color: var(--color-primary);
      }
    }

    .order-actions {
      display: flex;
      gap: var(--spacing-sm);
    }
  }
}
</style>
