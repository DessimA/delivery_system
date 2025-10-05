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
import { onMounted } from 'vue';
import AppNavbar from './components/AppNavbar.vue';
import NotificationDisplay from './components/layout/NotificationDisplay.vue';
import { useAuthStore } from './stores/auth';
import { useWebSocket } from './composables/useWebSocket';

const authStore = useAuthStore();
const { connect } = useWebSocket();

onMounted(() => {
  // Initialize auth state from localStorage on app load
  authStore.initializeAuth();

  // The watcher inside useWebSocket will automatically connect if/when authenticated.
  // We can trigger an initial connect attempt here if needed.
  if (authStore.isAuthenticated) {
      connect();
  }
});
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
  background-color: var(--color-dark);
  color: var(--color-text-light);
  padding: var(--spacing-md) 0;
  margin-top: auto;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 var(--spacing-md);
  }
}
</style>
