<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
      <router-link class="navbar-brand" to="/">DeliveryApp</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link class="nav-link" to="/products">Produtos</router-link>
          </li>
          <li class="nav-item">
            <router-link class="nav-link" to="/cart">Carrinho</router-link>
          </li>
          <li class="nav-item" v-if="isRestaurantOwner">
            <router-link class="nav-link" to="/restaurant/dashboard">Meu Restaurante</router-link>
          </li>
          <li class="nav-item" v-if="isDeliveryUser">
            <router-link class="nav-link" to="/delivery/dashboard">Minhas Entregas</router-link>
          </li>
        </ul>
        <ul class="navbar-nav">
          <li class="nav-item" v-if="!isLoggedIn">
            <router-link class="nav-link" to="/login">Login</router-link>
          </li>
          <li class="nav-item" v-if="!isLoggedIn">
            <router-link class="nav-link" to="/register">Registrar</router-link>
          </li>
          <li class="nav-item dropdown" v-if="isLoggedIn">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              {{ userEmail }}
            </a>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
              <li><a class="dropdown-item" href="#" @click.prevent="logout">Logout</a></li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'AppNavbar',
  data() {
    return {
      isLoggedIn: false,
      user: null,
    };
  },
  computed: {
    isRestaurantOwner() {
      return this.user && this.user.roles && this.user.roles.includes('RESTAURANT');
    },
    isDeliveryUser() {
      return this.user && this.user.roles && this.user.roles.includes('DELIVERY');
    },
    userEmail() {
        return this.user ? this.user.email : '';
    }
  },
  created() {
    this.checkLoginStatus();
    window.addEventListener('storage', this.checkLoginStatus);
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.checkLoginStatus);
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('authToken');
      this.isLoggedIn = !!token;
      if (this.isLoggedIn) {
        this.user = JSON.parse(localStorage.getItem('user'));
      } else {
        this.user = null;
      }
    },
    logout() {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      this.isLoggedIn = false;
      this.user = null;
      window.dispatchEvent(new Event('storage')); // Notifica outros componentes
      this.$router.push('/login');
    },
  },
};
</script>

<style scoped>
/* Adicione estilos específicos para o Navbar aqui, se necessário */
</style>