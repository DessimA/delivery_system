<template>
  <div class="payment-confirm-page">
    <div v-if="loading">
      <LoadingSpinner />
      <p>Confirmando pagamento...</p>
    </div>
    <div v-else-if="confirmed" class="status-success">
      <BaseIcon name="check-circle" />
      <h1>Pagamento confirmado!</h1>
      <p>Seu pagamento foi processado com sucesso.</p>
      <p>Você ja pode fechar esta pagina.</p>
    </div>
    <div v-else-if="alreadyConfirmed" class="status-success">
      <BaseIcon name="check-circle" />
      <h1>Pagamento ja confirmado</h1>
      <p>Este pagamento ja foi processado anteriormente.</p>
    </div>
    <div v-else-if="expired" class="status-error">
      <BaseIcon name="alert-circle" />
      <h1>Link expirado</h1>
      <p>O prazo para pagamento expirou. Gere um novo QR Code para tentar novamente.</p>
    </div>
    <div v-else class="status-error">
      <BaseIcon name="x-circle" />
      <h1>Erro na confirmacao</h1>
      <p>{{ errorMessage || 'Nao foi possivel confirmar o pagamento.' }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { paymentService } from '@/services/payment.service';
import LoadingSpinner from '@/components/base/LoadingSpinner.vue';
import BaseIcon from '@/components/base/BaseIcon.vue';

const route = useRoute();

const loading = ref(true);
const confirmed = ref(false);
const alreadyConfirmed = ref(false);
const expired = ref(false);
const errorMessage = ref('');

onMounted(async () => {
  const transactionId = route.params.transactionId;

  if (!transactionId) {
    loading.value = false;
    errorMessage.value = 'Link de pagamento invalido.';
    return;
  }

  try {
    await paymentService.confirm(transactionId);
    confirmed.value = true;
  } catch (error) {
    const response = error.response;
    if (response) {
      if (response.status === 410) {
        expired.value = true;
      } else if (response.status === 400 && response.data && response.data.message === 'Payment not found.') {
        errorMessage.value = 'Pagamento nao encontrado.';
      } else {
        alreadyConfirmed.value = true;
      }
    } else {
      errorMessage.value = 'Erro de conexao com o servidor.';
    }
  } finally {
    loading.value = false;
  }
});
</script>

<style lang="scss" scoped>
.payment-confirm-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 2rem;
  text-align: center;
}

.status-success {
  color: var(--color-success, #28a745);
}

.status-error {
  color: var(--color-danger, #dc3545);
}

h1 {
  margin: 1rem 0;
}

p {
  color: var(--color-text-muted);
  margin-bottom: 0.5rem;
}
</style>
