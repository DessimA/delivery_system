<template>
  <div class="orders-container">
    <div class="orders-header">
      <h1>Meus Pedidos</h1>
      <div class="orders-filters">
        <select v-model="statusFilter" class="status-filter">
          <option value="">Todos os status</option>
          <option value="AGUARDANDO_PAGAMENTO">Aguardando Pagamento</option>
          <option value="PAGO">Pago</option>
          <option value="EM_PREPARO">Em Preparo</option>
          <option value="EM_ROTA">Em Rota</option>
          <option value="ENTREGUE">Entregue</option>
          <option value="CANCELADO">Cancelado</option>
        </select>
      </div>
    </div>

    <div v-if="loading" class="orders-list">
      <OrderCardSkeleton v-for="n in 3" :key="n" />
    </div>

    <div v-else-if="filteredOrders.length > 0" class="orders-list">
      <OrderCard
        v-for="order in filteredOrders"
        :key="order.codigoPedido"
        :order="order"
        @view-details="viewOrderDetails"
        @reorder="reorderItems"
      >
        <template v-if="order.entrega" #delivery-tracker>
          <DeliveryTracker :delivery="order.entrega" />
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
import { ref, onMounted, computed } from 'vue';
import api from '@/api';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';
import { useWebSocket } from '@/composables/useWebSocket';

import OrderCard from '@/components/order/OrderCard.vue';
import OrderCardSkeleton from '@/components/order/OrderCardSkeleton.vue';
import EmptyState from '@/components/base/EmptyState.vue';
import DeliveryTracker from '@/components/DeliveryTracker.vue';

const orders = ref([]);
const statusFilter = ref('');

const { loading, execute } = useApi();
const { addNotification } = useNotifications();
const { subscribe, unsubscribe } = useWebSocket();

onMounted(() => {
  fetchOrders();
});

const fetchOrders = async () => {
  try {
    const response = await execute(() => api.get('/pedidos/meus-pedidos'));
    orders.value = response.data;
    orders.value.forEach(order => {
      if (order.codigoPedido) {
        subscribe(`/topic/pedidos/${order.codigoPedido}`, (message) => {
          const updatedOrder = JSON.parse(message.body);
          const index = orders.value.findIndex(o => o.codigoPedido === updatedOrder.codigoPedido);
          if (index !== -1) {
            orders.value[index].entrega = updatedOrder.entrega; // Update only delivery part
            orders.value[index].status = updatedOrder.status; // Update order status
          }
        });
      }
    });
  } catch (err) {
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
