<template>
  <div id="app">
    <AppNavbar />
    <router-view />
    <div v-if="notification" class="alert alert-info fixed-bottom m-3" role="alert">
      {{ notification }}
    </div>
  </div>
</template>

<script>
import AppNavbar from './components/AppNavbar.vue';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export default {
  name: 'App',
  components: {
    AppNavbar,
  },
  data() {
    return {
      stompClient: null,
      notification: null,
      notificationTimeout: null,
    };
  },
  mounted() {
    this.connectWebSocket();
  },
  beforeUnmount() {
    this.disconnectWebSocket();
  },
  methods: {
    connectWebSocket() {
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        console.log('Connected: ' + frame);
        // Subscribe to general notifications
        this.stompClient.subscribe('/topic/general', message => {
          this.showNotification(message.body);
        });

        // Subscribe to user-specific notifications (e.g., for order updates)
        // This assumes the user object is available in localStorage
        const user = JSON.parse(localStorage.getItem('user'));
        if (user && user.codigo) {
          this.stompClient.subscribe('/topic/pedidos/' + user.codigo, message => {
            this.showNotification('Seu pedido: ' + message.body);
          });
        }
      });
    },
    disconnectWebSocket() {
      if (this.stompClient) {
        this.stompClient.disconnect();
        console.log("Disconnected");
      }
    },
    showNotification(message) {
      this.notification = message;
      if (this.notificationTimeout) {
        clearTimeout(this.notificationTimeout);
      }
      this.notificationTimeout = setTimeout(() => {
        this.notification = null;
      }, 5000); // Notification disappears after 5 seconds
    },
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

.fixed-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  z-index: 1050; /* Bootstrap's modal-backdrop z-index is 1040 */
}
</style>