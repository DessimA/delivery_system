<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <div class="card">
          <div class="card-header">Registrar Nova Conta</div>
          <div class="card-body">
            <form @submit.prevent="registerUser">
              <div class="mb-3">
                <label for="nome" class="form-label">Nome</label>
                <input type="text" class="form-control" id="nome" v-model="user.nome" required>
              </div>
              <div class="mb-3">
                <label for="cpf" class="form-label">CPF</label>
                <input type="text" class="form-control" id="cpf" v-model="user.cpf" required>
              </div>
              <div class="mb-3">
                <label for="dataNascimento" class="form-label">Data de Nascimento</label>
                <input type="date" class="form-control" id="dataNascimento" v-model="user.dataNascimento" required>
              </div>
              <div class="mb-3">
                <label for="endereco" class="form-label">Endereço</label>
                <input type="text" class="form-control" id="endereco" v-model="user.endereco" required>
              </div>
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" v-model="user.email" required>
              </div>
              <div class="mb-3">
                <label for="senha" class="form-label">Senha</label>
                <input type="password" class="form-control" id="senha" v-model="user.senha" required>
              </div>
              <div v-if="error" class="alert alert-danger">{{ error }}</div>
              <div v-if="success" class="alert alert-success">{{ success }}</div>
              <button type="submit" class="btn btn-primary">Registrar</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/api';

export default {
  name: 'AppRegister',
  data() {
    return {
      user: {
        nome: '',
        cpf: '',
        dataNascimento: '',
        endereco: '',
        email: '',
        senha: '',
      },
      error: null,
      success: null,
    };
  },
  methods: {
    async registerUser() {
      try {
        this.error = null;
        this.success = null;
        // Formata a data para o formato esperado pelo backend (YYYY-MM-DD)
        const formattedUser = {
          ...this.user,
          dataNascimento: this.user.dataNascimento ? new Date(this.user.dataNascimento).toISOString().split('T')[0] : '',
        };
        await api.post('/usuarios/registrar', formattedUser);
        this.success = 'Usuário registrado com sucesso! Você já pode fazer login.';
        // Limpar formulário
        Object.keys(this.user).forEach(key => this.user[key] = '');
      } catch (err) {
        this.error = 'Erro ao registrar usuário. Verifique os dados e tente novamente.';
        console.error('Erro de registro:', err.response ? err.response.data : err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos para a página de Registro */
</style>