<template>
  <div class="checkout-pix-page">
    <h1>Pagamento PIX</h1>
    <div v-if="loading">
      <LoadingSpinner />
      <p>Gerando QR Code PIX...</p>
    </div>
    <div v-else-if="pixData">
      <div class="qr-code-container">
        <img :src="`data:image/png;base64,${pixData.qrCode}`" alt="QR Code PIX" />
      </div>
      <div class="copy-paste-container">
        <p>Ou copie e cole:</p>
        <BaseInput id="pixCopiaCola" :modelValue="pixData.copiaCola" readonly />
        <BaseButton @click="copyToClipboard(pixData.copiaCola)">Copiar</BaseButton>
      </div>
      <p class="expiration-info">Expira em: {{ pixData.expiraEm }}</p>
      <div class="waiting-payment">
        <LoadingSpinner />
        <p>Aguardando confirmação do pagamento...</p>
      </div>
    </div>
    <div v-else>
      <p>Erro ao gerar pagamento PIX.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useApi } from '../composables/useApi';
import { useNotifications } from '../composables/useNotifications';
import LoadingSpinner from '../components/base/LoadingSpinner.vue';
import BaseInput from '../components/base/BaseInput.vue';
import BaseButton from '../components/base/BaseButton.vue';
import api from '../plugins/axios'; // Import the configured axios instance

const route = useRoute();
const router = useRouter();
const { loading, execute } = useApi(); // Destructure loading and execute
const { addNotification } = useNotifications();

const pixData = ref(null);
let paymentCheckInterval = null;

onMounted(async () => {
  const pedidoId = route.query.pedidoId;
  const valor = route.query.valor;

  if (!pedidoId || !valor) {
    addNotification({ type: 'error', message: 'Dados do pedido incompletos para pagamento PIX.' });
    router.push({ name: 'Home' });
    return;
  }

  try {
    const response = await execute(() => api.post('/pagamentos/pix/gerar', { // Use execute for PIX generation
      pedidoId: parseInt(pedidoId),
      valor: parseFloat(valor)
    }));
    pixData.value = response.data;
    startPaymentCheck(pedidoId);
  } catch (error) {
    console.error('Erro ao gerar PIX:', error);
    addNotification({ type: 'error', message: 'Falha ao gerar pagamento PIX.' });
  }
});

onUnmounted(() => {
  if (paymentCheckInterval) {
    clearInterval(paymentCheckInterval);
  }
});

const startPaymentCheck = (pedidoId) => {
  paymentCheckInterval = setInterval(async () => {
    try {
      const response = await execute(() => api.get(`/pagamentos/${pedidoId}/status`)); // Use execute for status check
      const status = response.data;
      if (status === 'CONFIRMADO') {
        addNotification({ type: 'success', message: 'Pagamento PIX confirmado com sucesso!' });
        clearInterval(paymentCheckInterval);
        router.push({ name: 'Orders' });
      }
    } catch (error) {
      console.error('Erro ao verificar status do pagamento:', error);
      addNotification({ type: 'error', message: 'Falha ao verificar status do pagamento.' });
      clearInterval(paymentCheckInterval);
    }
  }, 5000); // Check every 5 seconds
};

const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    addNotification({ type: 'success', message: 'Código PIX copiado!' });
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao copiar código PIX.' });
  }
};
</script>

<style lang="scss" scoped>
.checkout-pix-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

h1 {
  margin-bottom: 2rem;
}

.qr-code-container {
  margin-bottom: 1.5rem;
  padding: 1rem;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  background-color: var(--color-surface);

  img {
    display: block;
    max-width: 250px;
    height: auto;
  }
}

.copy-paste-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  margin-bottom: 1.5rem;

  .base-input {
    flex-grow: 1;
  }
}

.expiration-info {
  font-size: 0.9rem;
  color: var(--color-text-muted);
  margin-bottom: 2rem;
}

.waiting-payment {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  color: var(--color-text-muted);
}
</style>