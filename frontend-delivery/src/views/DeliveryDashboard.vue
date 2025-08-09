<template>
  <div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Entregas Disponíveis</h1>
    </div>

    <div v-if="loading" class="text-center">Carregando entregas...</div>
    <div v-if="error" class="alert alert-danger">{{ error }}</div>

    <table v-if="availableDeliveries.length" class="table table-striped">
      <thead>
        <tr>
          <th scope="col">Pedido ID</th>
          <th scope="col">Origem</th>
          <th scope="col">Destino</th>
          <th scope="col">Valor</th>
          <th scope="col">Tempo Estimado (min)</th>
          <th scope="col">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="delivery in availableDeliveries" :key="delivery.id">
          <td>{{ delivery.codigoPedido }}</td>
          <td>{{ delivery.enderecoOrigem }}</td>
          <td>{{ delivery.enderecoDestino }}</td>
          <td>R$ {{ delivery.valorEntrega.toFixed(2) }}</td>
          <td>{{ delivery.tempoEstimado }}</td>
          <td>
            <button @click="acceptDelivery(delivery.id)" class="btn btn-sm btn-success">Aceitar</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else-if="!loading" class="alert alert-info">Nenhuma entrega disponível no momento.</div>

    <hr class="my-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1>Minhas Entregas</h1>
    </div>

    <div v-if="myDeliveriesLoading" class="text-center">Carregando minhas entregas...</div>
    <div v-if="myDeliveriesError" class="alert alert-danger">{{ myDeliveriesError }}</div>

    <table v-if="myDeliveries.length" class="table table-striped">
      <thead>
        <tr>
          <th scope="col">Entrega ID</th>
          <th scope="col">Pedido ID</th>
          <th scope="col">Status</th>
          <th scope="col">Destino</th>
          <th scope="col">Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="delivery in myDeliveries" :key="delivery.id">
          <td>{{ delivery.id }}</td>
          <td>{{ delivery.codigoPedido }}</td>
          <td>{{ delivery.status }}</td>
          <td>{{ delivery.enderecoDestino }}</td>
          <td>
            <button v-if="delivery.status === 'ACEITA'" @click="updateDeliveryStatus(delivery.id, 'EM_ROTA')" class="btn btn-sm btn-info me-2">Em Rota</button>
            <button v-if="delivery.status === 'EM_ROTA'" @click="updateDeliveryStatus(delivery.id, 'ENTREGUE')" class="btn btn-sm btn-primary">Entregue</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-else-if="!myDeliveriesLoading" class="alert alert-info">Você não possui entregas atribuídas.</div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  name: 'DeliveryDashboard',
  data() {
    return {
      availableDeliveries: [],
      loading: true,
      error: null,
      myDeliveries: [],
      myDeliveriesLoading: true,
      myDeliveriesError: null,
    };
  },
  async created() {
    await this.fetchAvailableDeliveries();
    await this.fetchMyDeliveries();
  },
  methods: {
    async fetchAvailableDeliveries() {
      try {
        this.loading = true;
        this.error = null;
        const response = await api.get('/entregas/disponiveis');
        this.availableDeliveries = response.data;
      } catch (err) {
        this.error = 'Erro ao carregar entregas disponíveis.';
        console.error(err);
      } finally {
        this.loading = false;
      }
    },
    async fetchMyDeliveries() {
      try {
        this.myDeliveriesLoading = true;
        this.myDeliveriesError = null;
        const response = await api.get('/entregas/minhas');
        this.myDeliveries = response.data;
      } catch (err) {
        this.myDeliveriesError = 'Erro ao carregar suas entregas.';
        console.error(err);
      } finally {
        this.myDeliveriesLoading = false;
      }
    },
    async acceptDelivery(id) {
      try {
        await api.post(`/entregas/${id}/aceitar`);
        alert('Entrega aceita com sucesso!');
        // Atualiza as listas após aceitar
        await this.fetchAvailableDeliveries();
        await this.fetchMyDeliveries();
      } catch (err) {
        alert('Erro ao aceitar entrega.');
        console.error(err);
      }
    },
    async updateDeliveryStatus(id, newStatus) {
      try {
        await api.put(`/entregas/${id}/status?novoStatus=${newStatus}`);
        alert(`Status da entrega ${id} atualizado para ${newStatus}!`);
        // Atualiza as listas após atualizar status
        await this.fetchAvailableDeliveries();
        await this.fetchMyDeliveries();
      } catch (err) {
        alert(`Erro ao atualizar status da entrega ${id}.`);
        console.error(err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos, se necessário */
</style>
