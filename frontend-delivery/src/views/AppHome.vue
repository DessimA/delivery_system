<template>
  <div class="container mt-5">
    <div class="jumbotron text-center">
      <h1 class="display-4">Bem-vindo ao DeliveryApp!</h1>
      <p class="lead">Seu destino para os melhores produtos e entregas rápidas.</p>
      <hr class="my-4">
      <p>Explore nossos produtos ou faça login para gerenciar seus pedidos.</p>
      <router-link class="btn btn-primary btn-lg" to="/products" role="button">Ver Produtos</router-link>
      <router-link class="btn btn-success btn-lg ms-2" to="/login" role="button">Login</router-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AppHome',
};
</script>

<style scoped>
/* Estilos específicos para a página Home */
</style>