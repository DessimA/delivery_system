<template>
  <div class="delivery-tracker" role="region" aria-label="Rastreamento de entrega">
    <h3>Status da Entrega: {{ formatStatus(delivery.status) }}</h3>
    <div class="stepper">
      <div 
        v-for="step in deliverySteps"
        :key="step.status"
        :class="['step', { 'active': isStepActive(step.status), 'completed': isStepCompleted(step.status) }]"
      >
        <div class="circle"></div>
        <div class="label">{{ step.label }}</div>
      </div>
    </div>
    <div v-if="delivery.courier" class="delivery-info">
      <p><strong>Entregador:</strong> {{ delivery.courier.name }}</p>
      <p><strong>Contato:</strong> {{ delivery.courier.phone || 'Não disponível' }}</p>
      <p><strong>Tempo Estimado:</strong> {{ delivery.estimatedTime || '--' }} minutos</p>
    </div>
    <div v-if="delivery.status === 'IN_TRANSIT'" class="map-simulation">
      <img src="/images/map.png" alt="Mapa simulado da entrega" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  delivery: {
    type: Object,
    required: true,
  },
});

const deliverySteps = [
  { status: 'PENDING', label: 'Aguardando Entregador' },
  { status: 'ACCEPTED', label: 'Entrega Aceita' },
  { status: 'PICKED_UP', label: 'Pedido Coletado' },
  { status: 'IN_TRANSIT', label: 'A Caminho' },
  { status: 'DELIVERED', label: 'Entregue' },
];

const currentStepIndex = computed(() => {
  return deliverySteps.findIndex(step => step.status === props.delivery.status);
});

const isStepActive = (status) => status === props.delivery.status;

const isStepCompleted = (status) => {
  const index = deliverySteps.findIndex(step => step.status === status);
  return index < currentStepIndex.value;
};

const formatStatus = (status) => {
    return deliverySteps.find(s => s.status === status)?.label || status;
};
</script>

<style lang="scss" scoped>
.delivery-tracker {
  margin-top: 1.5rem;
  padding: 1.5rem;
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

h3 {
  margin-bottom: 1.5rem;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-dark);
}

.stepper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 12px;
    left: 0;
    right: 0;
    height: 2px;
    background-color: var(--color-border);
    z-index: 0;
  }

  .step {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    z-index: 1;
    flex: 1;

    .circle {
      width: 24px;
      height: 24px;
      border-radius: 50%;
      background-color: var(--color-surface);
      border: 2px solid var(--color-border);
      margin-bottom: 0.5rem;
      transition: all 0.3s ease;
    }

    .label {
      font-size: 0.75rem;
      text-align: center;
      color: var(--color-text-muted);
      max-width: 80px;
    }

    &.active .circle {
      background-color: var(--color-primary);
      border-color: var(--color-primary);
      box-shadow: 0 0 0 4px rgba(255, 107, 53, 0.2);
    }

    &.completed .circle {
      background-color: var(--color-success);
      border-color: var(--color-success);
    }

    &.active .label,
    &.completed .label {
      color: var(--color-text-dark);
      font-weight: var(--font-weight-semibold);
    }
  }
}

.delivery-info {
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);
  p { margin-bottom: 0.5rem; font-size: var(--font-size-sm); }
}

.map-simulation {
  margin-top: 1.5rem;
  img { width: 100%; border-radius: var(--radius-md); }
}
</style>
