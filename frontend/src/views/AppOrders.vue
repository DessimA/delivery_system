<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>Meus Pedidos</h1>
      <div class="orders-filters">
        <select v-model="statusFilter" class="status-filter">
          <option value="">Todos os status</option>
          <option value="WAITING_PAYMENT">Aguardando Pagamento</option>
          <option value="PAID">Pago</option>
          <option value="PREPARING">Em Preparo</option>
          <option value="IN_TRANSIT">Em Rota</option>
          <option value="DELIVERED">Entregue</option>
          <option value="CANCELLED">Cancelado</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="orders-list">
      <OrderCardSkeleton v-for="n in 3" :key="n" />
    </div>

    <div v-else-if="filteredOrders.length > 0" class="orders-list">
      <OrderCard
        v-for="order in filteredOrders"
        :key="order.id"
        :order="order"
        @view-details="viewOrderDetails"
        @reorder="reorderItems"
      >
        <template v-if="order.delivery" #delivery-tracker>
          <DeliveryTracker :delivery="order.delivery" />
        </template>
      </OrderCard>
    </div>

    <EmptyState
      v-else
      title="Nenhum pedido encontrado"
      description="Você ainda não fez nenhum pedido ou não há pedidos com o status selecionado."
      icon="clipboard-list"
      action-label="Ver Cardápio"
      @action="$router.push('/restaurants')"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';
import { useWebSocket } from '@/composables/useWebSocket';
import { orderService } from '@/services/order.service';

import OrderCard from '@/components/features/order/OrderCard.vue';
import OrderCardSkeleton from '@/components/features/order/OrderCardSkeleton.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import DeliveryTracker from '@/components/features/delivery/DeliveryTracker.vue';

const orders = ref([]);
const statusFilter = ref('');

const { loading, execute } = useApi();
const { addNotification } = useNotifications();
const { subscribe, unsubscribe } = useWebSocket();

const subscriptions = ref([]); // To store WebSocket subscriptions

onMounted(() => {
  fetchOrders();
});

onUnmounted(() => {
  // Unsubscribe from all active WebSocket subscriptions
  subscriptions.value.forEach(sub => {
    unsubscribe(sub.topic);
  });
});

const fetchOrders = async () => {
  try {
    const data = await execute(() => orderService.getMyOrders());
    orders.value = data;
    orders.value.forEach(order => {
      if (order.id) {
        const handler = (message) => {
          const deliveryUpdate = JSON.parse(message.body);
          console.log(`Received update for order ${order.id}:`, deliveryUpdate);
          
          const index = orders.value.findIndex(o => o.id === order.id);
          if (index !== -1) {
            orders.value[index].delivery = deliveryUpdate;
          }
        };
        const topic = `/topic/orders/${order.id}`;
        subscribe(topic, handler);
        subscriptions.value.push({ topic, handler });
      }
    });
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao carregar seus pedidos.' });
    console.error('Error fetching orders:', err);
  }
};

const filteredOrders = computed(() => {
  if (!statusFilter.value) {
    return orders.value;
  }
  return orders.value.filter(order => order.status === statusFilter.value);
});

const viewOrderDetails = (orderId) => {
  addNotification({ type: 'info', message: `Funcionalidade 'Ver Detalhes' (Pedido #${orderId}) em desenvolvimento.` });
};

const reorderItems = (orderId) => {
  addNotification({ type: 'info', message: `Funcionalidade 'Reordenar' (Pedido #${orderId}) em desenvolvimento.` });
};
</script>

<style lang="scss" scoped>
.orders-container {
  padding: var(--spacing-lg) var(--spacing-md);
  max-width: 1000px;
  margin: 0 auto;
}

.orders-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);

  h1 {
    font-size: var(--font-size-h2);
    margin: 0;
    color: var(--color-text-dark);
  }

  .orders-filters {
    .status-filter {
      padding: var(--spacing-sm);
      border-radius: var(--radius-md);
      border: 1px solid var(--color-border);
      background-color: var(--color-background);
      color: var(--color-text-dark);
      font-size: var(--font-size-md);
    }
  }
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}
</style>
