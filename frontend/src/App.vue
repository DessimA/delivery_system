<template>
  <div id="app">
    <AppNavbar />
    <main class="app-main-content">
      <router-view />
    </main>

    <NotificationDisplay />

    <footer class="app-footer">
      <div class="container text-center">
        <span class="text-muted">&copy; 2025 Delivery System. All rights reserved.</span>
      </div>
    </footer>
  </div>
</template>

<script setup>
import AppNavbar from './components/AppNavbar.vue';
import NotificationDisplay from './components/layout/NotificationDisplay.vue';
</script>

<style lang="scss">
/* Estilos globais */
html, body {
  height: 100%;
  margin: 0;
  background-color: var(--color-surface);
}

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-main-content {
  flex-grow: 1;
  padding-top: var(--spacing-md); /* Adjust based on navbar height */
  padding-bottom: var(--spacing-md); /* Adjust based on footer height */
}

.app-footer {
  background: linear-gradient(135deg, var(--color-dark) 0%, #1a252f 100%);
  color: var(--color-text-light);
  padding: var(--spacing-xl) 0;
  margin-top: auto;
  border-top: 3px solid var(--color-primary);

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--spacing-md);
  }

  .text-muted {
    color: rgba(255, 255, 255, 0.8);
    font-size: var(--font-size-sm);
  }
}
</style>
