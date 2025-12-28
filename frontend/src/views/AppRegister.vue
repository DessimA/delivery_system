<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <img src="/images/logo.svg" alt="Logo" class="auth-logo" />
        <h1>Crie sua conta</h1>
        <p>Preencha os dados abaixo para se registrar</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <BaseInput
          id="reg-nome"
          v-model="form.nome"
          label="Nome Completo"
          icon="user"
          :error="errors.nome"
          placeholder="Seu nome"
        />
        <BaseInput
          id="reg-cpf"
          v-model="form.cpf"
          label="CPF"
          icon="credit-card"
          :error="errors.cpf"
          placeholder="000.000.000-00"
        />
        <BaseInput
          id="reg-dataNascimento"
          v-model="form.dataNascimento"
          type="date"
          label="Data de Nascimento"
          icon="calendar"
          :error="errors.dataNascimento"
        />
        <BaseInput
          id="reg-endereco"
          v-model="form.endereco"
          label="Endereço"
          icon="map-pin"
          :error="errors.endereco"
          placeholder="Rua, Número, Bairro, Cidade"
        />
        <BaseInput
          id="reg-email"
          v-model="form.email"
          type="email"
          label="E-mail"
          icon="mail"
          :error="errors.email"
          placeholder="seu@email.com"
        />
        <BaseInput
          id="reg-senha"
          v-model="form.senha"
          type="password"
          label="Senha"
          icon="lock"
          :error="errors.senha"
          placeholder="••••••••"
        />
        <BaseInput
          id="reg-confirmSenha"
          v-model="form.confirmSenha"
          type="password"
          label="Confirmar Senha"
          icon="lock"
          :error="errors.confirmSenha"
          placeholder="••••••••"
        />

        <BaseButton
          type="submit"
          label="Registrar"
          variant="primary"
          size="lg"
          :loading="loading"
          block
        />
      </form>

      <div class="auth-footer">
        <p>
          Já tem conta?
          <router-link to="/login">Faça Login</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useApi } from '@/composables/useApi';
import { useNotifications } from '@/composables/useNotifications';
import { userService } from '@/services/user.service';

import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';

const router = useRouter();
const { loading, execute } = useApi();
const { addNotification } = useNotifications();

const form = reactive({
  nome: '',
  cpf: '',
  dataNascimento: '',
  endereco: '',
  email: '',
  senha: '',
  confirmSenha: '',
});

const errors = reactive({ ...form });

// Clear errors when input changes
Object.keys(form).forEach(key => {
  watch(() => form[key], () => {
    if (errors[key]) {
      errors[key] = '';
    }
  });
});

const validateForm = () => {
  Object.keys(errors).forEach(key => { errors[key] = ''; });
  let isValid = true;

  if (!form.nome) { errors.nome = 'Nome é obrigatório.'; isValid = false; }
  
  // TODO: Implement a proper CPF validation algorithm (checksum)
  if (!form.cpf) { errors.cpf = 'CPF é obrigatório.'; isValid = false; }
  else if (!/^(\d{3}\.){2}\d{3}-\d{2}$/.test(form.cpf)) { errors.cpf = 'CPF inválido. Use o formato 000.000.000-00'; isValid = false; }

  if (!form.dataNascimento) { errors.dataNascimento = 'Data de Nascimento é obrigatória.'; isValid = false; }
  else if (new Date(form.dataNascimento) >= new Date()) { errors.dataNascimento = 'Data de Nascimento deve ser no passado.'; isValid = false; }

  if (!form.endereco) { errors.endereco = 'Endereço é obrigatório.'; isValid = false; }

  if (!form.email) { errors.email = 'E-mail é obrigatório.'; isValid = false; }
  else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) { errors.email = 'Formato de e-mail inválido.'; isValid = false; }

  if (!form.senha) { errors.senha = 'Senha é obrigatória.'; isValid = false; }
  else if (form.senha.length < 6) { errors.senha = 'A senha deve ter no mínimo 6 caracteres.'; isValid = false; }

  if (form.senha !== form.confirmSenha) { errors.confirmSenha = 'As senhas não coincidem.'; isValid = false; }

  return isValid;
};

const handleRegister = async () => {
  if (!validateForm()) {
    addNotification({ type: 'warning', message: 'Por favor, revise os campos do formulário.' });
    return;
  }

  try {
    const payload = { 
      name: form.nome,
      cpf: form.cpf,
      birthDate: form.dataNascimento,
      address: form.endereco,
      email: form.email,
      password: form.senha
    };

    await execute(() => userService.register(payload));
    
    addNotification({ type: 'success', message: 'Registro realizado com sucesso! Você já pode fazer login.' });
    router.push('/login');

  } catch (err) {
    addNotification({ type: 'error', message: 'Falha ao registrar. Verifique seus dados e tente novamente.' });
    console.error('Registration failed in component:', err);
  }
};
</script>

<style lang="scss" scoped>
/* Styles are consistent with AppLogin.vue */
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
  max-width: 520px;
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
