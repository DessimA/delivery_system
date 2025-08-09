<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">Login</div>
          <div class="card-body">
            <form @submit.prevent="login">
              <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" v-model="email" required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Senha</label>
                <input type="password" class="form-control" id="password" v-model="password" required>
              </div>
              <div v-if="error" class="alert alert-danger">{{ error }}</div>
              <button type="submit" class="btn btn-primary">Entrar</button>
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
  name: 'AppLogin',
  data() {
    return {
      email: '',
      password: '',
      error: null,
    };
  },
  methods: {
    async login() {
      try {
        this.error = null;
        const response = await api.post('/auth/login', {
          email: this.email,
          senha: this.password, // O backend espera 'senha'
        });

        const token = response.data.accessToken;
        if (token) {
          localStorage.setItem('authToken', token);

          // Após salvar o token, busca os dados do usuário
          const userResponse = await api.get('/usuarios/me');
          localStorage.setItem('user', JSON.stringify(userResponse.data));
          
          // Dispara um evento para que o Navbar possa reagir à mudança de status de login
          window.dispatchEvent(new Event('storage'));

          this.$router.push('/products'); // Redireciona para a página de produtos após o login
        } else {
            this.error = 'Token não recebido do servidor.';
        }

      } catch (err) {
        this.error = 'Erro ao fazer login. Verifique suas credenciais.';
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        console.error('Erro de login:', err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos para a página de Login */
</style>