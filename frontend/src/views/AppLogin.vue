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
import { useAuth } from '@/composables/useAuth';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';

import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const { login } = useAuth();
const router = useRouter();
const route = useRoute();
const { loading, execute } = useApi();
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

// Função de validação do formulário
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
    await execute(() => login({
      email: form.email,
      password: form.password,
    }));
    
    addNotification({ type: 'success', message: 'Login realizado com sucesso!' });
    
    const redirectPath = route.query.redirect || '/';
    router.push(redirectPath);

  } catch (err) {
    console.error('Login failed in component:', err);
    const errorMessage = err.response?.data?.message || 'Erro ao fazer login. Verifique suas credenciais.';
    addNotification({ type: 'error', message: errorMessage });
  }
};
</script>

<style lang="scss" scoped>
/* Styles remain the same */
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  background: linear-gradient(135deg, var(--color-surface) 0%, #e8ecf1 100%);
  padding: var(--spacing-xl);
}

.auth-card {
  background-color: var(--color-background);
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  padding: var(--spacing-xxl);
  width: 100%;
  max-width: 420px;
  text-align: center;
  border: 1px solid var(--color-border);
}

.auth-header {
  margin-bottom: var(--spacing-xl);

  .auth-logo {
    width: 80px;
    height: 80px;
    margin-bottom: var(--spacing-md);
    filter: drop-shadow(0 2px 4px rgba(255, 107, 53, 0.3));
  }

  h1 {
    font-size: var(--font-size-h3);
    color: var(--color-text-dark);
    margin-bottom: var(--spacing-sm);
    font-weight: var(--font-weight-bold);
  }

  p {
    font-size: var(--font-size-md);
    color: var(--color-text-muted);
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
  color: var(--color-text-muted);
  margin-top: var(--spacing-md);

  a {
    color: var(--color-primary);
    font-weight: var(--font-weight-semibold);
    text-decoration: none;
    transition: color 0.2s ease;

    &:hover {
      color: var(--color-primary-dark);
      text-decoration: underline;
    }
  }
}
</style>
