<template>
  <div class="delivery-tracker">
    <h3>Status da Entrega: {{ delivery.status }}</h3>
    <div class="stepper">
      <div 
        v-for="(step, index) in deliverySteps"
        :key="step.status"
        :class="['step', { 'active': isStepActive(step.status), 'completed': isStepCompleted(step.status) }]"
      >
        <div class="circle"></div>
        <div class="label">{{ step.label }}</div>
      </div>
    </div>
    <div v-if="delivery.entregador" class="delivery-info">
      <p><strong>Entregador:</strong> {{ delivery.entregador.nome }}</p>
      <p><strong>Contato:</strong> {{ delivery.entregador.telefone || 'Não disponível' }}</p>
      <p><strong>Tempo Estimado:</strong> {{ delivery.tempoEstimado || '--' }} minutos</p>
    </div>
    <div v-if="delivery.status === 'EM_ROTA'" class="map-simulation">
      <img src="/images/map.png" alt="Mapa simulado" />
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
  { status: 'PENDENTE', label: 'Aguardando Entregador' },
  { status: 'ACEITA', label: 'Entrega Aceita' },
  { status: 'COLETADA', label: 'Pedido Coletado' },
  { status: 'EM_ROTA', label: 'A Caminho' },
  { status: 'ENTREGUE', label: 'Entregue' },
];

const currentStepIndex = computed(() => {
  return deliverySteps.findIndex(step => step.status === props.delivery.status);
});

const isStepActive = (status) => {
  return status === props.delivery.status;
};

const isStepCompleted = (status) => {
  return deliverySteps.findIndex(step => step.status === status) < currentStepIndex.value;
};
</script>

<style lang="scss" scoped>
.delivery-tracker {
  margin-top: 1.5rem;
  padding: 1rem;
  background-color: var(--color-background);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

h3 {
  margin-bottom: 1rem;
  color: var(--color-primary);
}

.stepper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 2px;
    background-color: var(--color-border);
    transform: translateY(-50%);
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
      background-color: var(--color-border);
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 0.5rem;
      transition: background-color 0.3s ease;
    }

    .label {
      font-size: 0.85rem;
      text-align: center;
      color: var(--color-text-muted);
    }

    &.active .circle {
      background-color: var(--color-primary);
    }

    &.completed .circle {
      background-color: var(--color-success);
    }

    &.active .label,
    &.completed .label {
      color: var(--color-text-dark);
      font-weight: var(--font-weight-medium);
    }
  }
}

.delivery-info {
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid var(--color-border);

  p {
    margin-bottom: 0.5rem;
    color: var(--color-text-dark);
  }

  strong {
    color: var(--color-text-dark);
  }
}

.map-simulation {
  margin-top: 1.5rem;
  text-align: center;

  img {
    max-width: 100%;
    height: auto;
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-sm);
  }
}
</style>
