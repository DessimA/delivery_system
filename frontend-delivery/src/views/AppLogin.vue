<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <img src="/images/logo.svg" alt="Logo" class="auth-logo" />
        <h1>Bem-vindo ao DeliveryApp</h1>
        <p>Entre na sua conta para continuar</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <BaseInput
          v-model="form.email"
          type="email"
          label="E-mail"
          icon="mail"
          :error="errors.email"
          placeholder="seu@email.com"
          id="email"
        />
        <BaseInput
          v-model="form.password"
          type="password"
          label="Senha"
          icon="lock"
          :error="errors.password"
          placeholder="••••••••"
          id="password"
        />

        <BaseButton
          type="submit"
          label="Entrar"
          variant="primary"
          size="lg"
          :loading="loading"
          block
        />
      </form>

      <div class="auth-footer">
        <p>
          Não tem conta?
          <router-link to="/register">Cadastre-se</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';

import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const authStore = useAuthStore();
const router = useRouter();
const route = useRoute(); // Get current route to access query params
const { loading, error, execute } = useApi();
const { addNotification } = useNotifications();

const form = reactive({
  email: '',
  password: '',
});

const errors = reactive({
  email: '',
  password: '',
});

// Clear validation errors on input
watch(() => form.email, () => { errors.email = ''; });
watch(() => form.password, () => { errors.password = ''; });

// Show notification when API error occurs
watch(error, (newError) => {
  if (newError) {
    addNotification({ type: 'error', message: newError });
  }
});

const validateForm = () => {
  // Reset errors before validation
  errors.email = '';
  errors.password = '';
  let isValid = true;

  if (!form.email) {
    errors.email = 'E-mail é obrigatório.';
    isValid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = 'Por favor, insira um e-mail válido.';
    isValid = false;
  }

  if (!form.password) {
    errors.password = 'Senha é obrigatória.';
    isValid = false;
  }
  
  return isValid;
};

const handleLogin = async () => {
  if (!validateForm()) {
    return;
  }

  try {
    await execute(() => authStore.login({
      email: form.email,
      senha: form.password, // Backend expects 'senha'
    }));
    
    addNotification({ type: 'success', message: 'Login realizado com sucesso!' });
    
    // Redirect to intended page or home
    const redirectPath = route.query.redirect || '/';
    router.push(redirectPath);

  } catch (err) {
    // The 'watch' on the error ref already handles showing the notification.
    // We just log the full error for debugging purposes.
    console.error('Login failed in component:', err);
  }
};
</script>

<style lang="scss" scoped>
/* Styles remain the same */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - var(--spacing-xxl)); /* Adjust for navbar/footer if present */
  background-color: var(--color-surface);
  padding: var(--spacing-lg);
}

.auth-card {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-lg);
  padding: var(--spacing-xl);
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.auth-header {
  margin-bottom: var(--spacing-lg);

  .auth-logo {
    width: 80px;
    height: 80px;
    margin-bottom: var(--spacing-md);
  }

  h1 {
    font-size: var(--font-size-h3);
    color: var(--color-text-dark);
    margin-bottom: var(--spacing-xs);
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-dark);
  }
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.auth-footer {
  font-size: var(--font-size-sm);
  color: var(--color-dark);

  a {
    color: var(--color-primary);
    font-weight: var(--font-weight-medium);
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
