<template>
  <div class="dashboard-container">
    <!-- Available Deliveries Section -->
    <section class="delivery-section">
      <h2>Entregas Disponíveis</h2>
      <div v-if="loadingAvailable" class="loading-state">
        <p>Carregando entregas disponíveis...</p>
      </div>
      <div v-else-if="availableDeliveries.length > 0" class="deliveries-list">
        <div v-for="delivery in availableDeliveries" :key="delivery.id" class="delivery-card">
          <div class="info">
            <p><strong>Pedido:</strong> #{{ delivery.codigoPedido }}</p>
            <p><strong>Origem:</strong> {{ delivery.enderecoOrigem }}</p>
            <p><strong>Destino:</strong> {{ delivery.enderecoDestino }}</p>
            <p><strong>Valor:</strong> {{ formatCurrency(delivery.valorEntrega) }}</p>
          </div>
          <div class="actions">
            <BaseButton size="sm" @click="handleAcceptDelivery(delivery.id)" :loading="isActionLoading">Aceitar</BaseButton>
          </div>
        </div>
      </div>
      <EmptyState v-else title="Nenhuma entrega disponível" description="Volte mais tarde para verificar novas entregas." />
    </section>

    <hr class="divider" />

    <!-- My Deliveries Section -->
    <section class="delivery-section">
      <h2>Minhas Entregas</h2>
      <div v-if="loadingMine" class="loading-state">
        <p>Carregando suas entregas...</p>
      </div>
      <div v-else-if="myDeliveries.length > 0" class="deliveries-list">
        <div v-for="delivery in myDeliveries" :key="delivery.id" class="delivery-card">
          <div class="info">
            <p><strong>Pedido:</strong> #{{ delivery.codigoPedido }}</p>
            <p><strong>Destino:</strong> {{ delivery.enderecoDestino }}</p>
            <p><strong>Status:</strong> <span :class="['status-badge', `status--${delivery.status.toLowerCase()}`]">{{ delivery.status }}</span></p>
          </div>
          <div class="actions">
            <BaseButton v-if="delivery.status === 'ACEITA'" size="sm" variant="secondary" @click="handleUpdateStatus(delivery.id, 'COLETADA')" :loading="isActionLoading">Coletei o Pedido</BaseButton>
            <BaseButton v-if="delivery.status === 'COLETADA'" size="sm" variant="secondary" @click="handleUpdateStatus(delivery.id, 'EM_ROTA')" :loading="isActionLoading">Iniciar Rota</BaseButton>
            <BaseButton v-if="delivery.status === 'EM_ROTA'" size="sm" variant="primary" @click="handleUpdateStatus(delivery.id, 'ENTREGUE')" :loading="isActionLoading">Finalizar Entrega</BaseButton>
          </div>
        </div>
        <div v-if="delivery.status === 'EM_ROTA'" class="map-simulation">
          <img src="/images/map.png" alt="Mapa simulado" />
        </div>
      </div>
      <EmptyState v-else title="Nenhuma entrega atribuída" description="Aceite uma entrega da lista acima para começar." />
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/plugins/axios';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';

import BaseButton from '@/components/base/BaseButton.vue';
import EmptyState from '@/components/base/EmptyState.vue';

const availableDeliveries = ref([]);
const myDeliveries = ref([]);

const { loading: loadingAvailable, execute: execAvailable } = useApi();
const { loading: loadingMine, execute: execMine } = useApi();
const { loading: isActionLoading, execute: execAction } = useApi();
const { addNotification } = useNotifications();

onMounted(() => {
  fetchAllDeliveries();
});

async function fetchAllDeliveries() {
  fetchAvailableDeliveries();
  fetchMyDeliveries();
}

async function fetchAvailableDeliveries() {
  try {
    const response = await execAvailable(() => api.get('/entregas/disponiveis'));
    availableDeliveries.value = response.data;
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao carregar entregas disponíveis.' });
    console.error('Failed to fetch available deliveries:', err);
  }
}

async function fetchMyDeliveries() {
  try {
    const response = await execMine(() => api.get('/entregas/minhas'));
    myDeliveries.value = response.data;
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao carregar suas entregas.' });
    console.error('Failed to fetch my deliveries:', err);
  }
}

async function handleAcceptDelivery(deliveryId) {
  try {
    await execAction(() => api.post(`/entregas/${deliveryId}/aceitar`));
    addNotification({ type: 'success', message: 'Entrega aceita com sucesso!' });
    fetchAllDeliveries();
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao aceitar entrega.' });
    console.error('Failed to accept delivery:', err);
  }
}

async function handleUpdateStatus(deliveryId, newStatus) {
  try {
    await execAction(() => api.put(`/entregas/${deliveryId}/status`, { novoStatus }));
    addNotification({ type: 'success', message: `Status da entrega atualizado para ${newStatus}!` });
    fetchAllDeliveries();
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao atualizar status da entrega.' });
    console.error('Failed to update delivery status:', err);
  }
}

const formatCurrency = (value) => {
  if (typeof value !== 'number') return 'R$ 0,00';
  return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
};
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: var(--spacing-lg) var(--spacing-md);
  max-width: 1200px;
  margin: 0 auto;
}
.delivery-section {
  margin-bottom: var(--spacing-xl);
  h2 {
    margin-bottom: var(--spacing-lg);
  }
}
.deliveries-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
.delivery-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}
.info p {
  margin: 0 0 4px 0;
  color: var(--color-dark);
}
.info p strong {
  color: var(--color-text-dark);
}
.actions {
  display: flex;
  gap: var(--spacing-sm);
}
.divider {
  margin: var(--spacing-xl) 0;
}
.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: bold;
  color: white;
  text-transform: uppercase;
}
.status--aceita { background-color: #007bff; }
.status--em_rota { background-color: #ffc107; color: #212529; }
.status--entregue { background-color: #28a745; }
.status--pendente { background-color: #6c757d; }
</style>