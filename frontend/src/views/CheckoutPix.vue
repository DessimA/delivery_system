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
        <BaseInput id="pixCopiaCola" :modelValue="pixData.copyPaste" readonly />
        <BaseButton @click="copyToClipboard(pixData.copyPaste)">Copiar</BaseButton>
      </div>
      <p class="expiration-info">Expira em: {{ formatDate(pixData.expiresAt) }}</p>
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
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';
import { paymentService } from '@/services/payment.service';
import LoadingSpinner from '@/components/base/LoadingSpinner.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const route = useRoute();
const router = useRouter();
const { loading, execute } = useApi();
const { addNotification } = useNotifications();

const pixData = ref(null);
let paymentCheckInterval = null;

onMounted(async () => {
  const orderId = route.query.pedidoId;
  const amount = route.query.valor;

  if (!orderId || !amount) {
    addNotification({ type: 'error', message: 'Dados do pedido incompletos para pagamento PIX.' });
    router.push({ name: 'Home' });
    return;
  }

  try {
    const data = await execute(() => paymentService.generatePix(parseInt(orderId), parseFloat(amount)));
    pixData.value = data;
    startPaymentCheck(orderId);
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

const startPaymentCheck = (orderId) => {
  paymentCheckInterval = setInterval(async () => {
    try {
      const status = await paymentService.getStatus(orderId);
      if (status === 'CONFIRMADO' || status === 'PAID') {
        addNotification({ type: 'success', message: 'Pagamento PIX confirmado com sucesso!' });
        clearInterval(paymentCheckInterval);
        router.push({ name: 'Orders' });
      }
    } catch (error) {
      console.error('Erro ao verificar status do pagamento:', error);
    }
  }, 5000);
};

const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text);
    addNotification({ type: 'success', message: 'Código PIX copiado!' });
  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao copiar código PIX.' });
  }
};

const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleString('pt-BR');
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