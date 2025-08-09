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
// import api from '@/api'; // Removido, pois não é usado diretamente aqui para o login HTTP Basic

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
        const credentials = btoa(`${this.email}:${this.password}`); // Codifica para Base64
        
        // A API Spring Security com HTTP Basic espera o cabeçalho Authorization
        // Para este exemplo, vamos apenas armazenar as credenciais codificadas como o "token"
        // e o interceptor do axios irá usá-las.
        localStorage.setItem('authToken', credentials);
        
        // Dispara um evento para que o Navbar possa reagir à mudança de status de login
        window.dispatchEvent(new Event('storage'));

        this.$router.push('/products'); // Redireciona para a página de produtos após o login
      } catch (err) {
        this.error = 'Erro ao fazer login. Verifique suas credenciais.';
        console.error('Erro de login:', err);
      }
    },
  },
};
</script>

<style scoped>
/* Estilos específicos para a página de Login */
</style>