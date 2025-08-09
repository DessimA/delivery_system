<template>
  <div id="app">
    <AppNavbar />
    <router-view />
  </div>
</template>

<script>
import AppNavbar from './components/AppNavbar.vue';

export default {
  name: 'App',
  components: {
    AppNavbar,
  },
};
</script>

<style>
/* Estilos globais */
body {
  font-family: 'Arial', sans-serif;
  background-color: #f8f9fa;
}

.container {
  margin-top: 20px;
}
</style>