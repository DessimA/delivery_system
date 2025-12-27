import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { ref, watch } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useNotifications } from './useNotifications';

const WEBSOCKET_URL = import.meta.env.VITE_APP_WEBSOCKET_URL || 'ws://localhost:8080/ws';

const stompClient = ref(null);
const isConnected = ref(false);

export function useWebSocket() {
  const authStore = useAuthStore();
  const { addNotification } = useNotifications();

  const connect = () => {
    if (isConnected.value) return;

    const socket = new SockJS(WEBSOCKET_URL);
    stompClient.value = Stomp.over(socket);

    const headers = {};
    if (authStore.token) {
      headers.Authorization = `Bearer ${authStore.token}`;
    }

    stompClient.value.connect(
      headers,
      (frame) => {
        isConnected.value = true;
        console.log('Connected to WebSocket: ' + frame);
        
        // Inscrições gerais
        stompClient.value.subscribe('/topic/general', (message) => {
          addNotification({ type: 'info', message: message.body });
        });

        // Inscrições específicas do usuário (se logado)
        if (authStore.isAuthenticated && authStore.user?.codigo) {
          subscribeToUserTopics(authStore.user.codigo);
        }
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        addNotification({ type: 'error', message: 'Erro na conexão com o servidor de notificações.' });
        isConnected.value = false;
      }
    );
  };

  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.disconnect(() => {
        isConnected.value = false;
        console.log('Disconnected from WebSocket');
      });
    }
  };

  const subscribeToUserTopics = (userId) => {
    if (!stompClient.value || !isConnected.value) return;

    // Exemplo: Inscrição para atualizações de pedidos do usuário
    stompClient.value.subscribe(`/topic/pedidos/${userId}`, (message) => {
      addNotification({ type: 'info', message: `Atualização do seu pedido: ${message.body}` });
    });

    console.log(`Subscribed to user-specific topics for user ${userId}`);
  };

  // Observa o estado de autenticação para conectar/desconectar
  watch(() => authStore.isAuthenticated, (newIsAuthenticated) => {
    if (newIsAuthenticated) {
      if (!isConnected.value) {
        connect();
      }
    } else {
      disconnect();
    }
  });

  return { connect, disconnect };
}
