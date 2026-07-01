import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import { ref, watch } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useNotifications } from './useNotifications';

const WEBSOCKET_URL = import.meta.env.VITE_APP_WEBSOCKET_URL || 'http://localhost:8080/ws';

const stompClient = ref(null);
const isConnected = ref(false);
const activeSubscriptions = new Map();
const subscriptionQueue = [];

export function useWebSocket() {
  const authStore = useAuthStore();
  const { addNotification } = useNotifications();

  const connect = () => {
    if (isConnected.value || (stompClient.value && stompClient.value.connected)) return;

    console.log('Connecting to WebSocket...');
    const socket = new SockJS(WEBSOCKET_URL);
    stompClient.value = Stomp.over(socket);
    stompClient.value.debug = null; // Reduce console noise

    const headers = {};
    if (authStore.token) {
      headers.Authorization = `Bearer ${authStore.token}`;
    }

    stompClient.value.connect(
      headers,
      (frame) => {
        isConnected.value = true;
        console.log('Connected to WebSocket');
        
        // Process queued subscriptions
        while (subscriptionQueue.length > 0) {
          const { topic, callback } = subscriptionQueue.shift();
          subscribe(topic, callback);
        }
      },
      (error) => {
        console.error('WebSocket connection error:', error);
        isConnected.value = false;
        // Attempt to reconnect after 5 seconds
        setTimeout(connect, 5000);
      }
    );
  };

  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.disconnect(() => {
        isConnected.value = false;
        activeSubscriptions.clear();
        console.log('Disconnected from WebSocket');
      });
    }
  };

  const subscribe = (topic, callback) => {
    if (isConnected.value && stompClient.value?.connected) {
      const subscription = stompClient.value.subscribe(topic, callback);
      activeSubscriptions.set(topic, subscription);
      return subscription;
    } else {
      console.log(`Queueing subscription for topic: ${topic}`);
      subscriptionQueue.push({ topic, callback });
      return null;
    }
  };

  const unsubscribe = (topic) => {
    const subscription = activeSubscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      activeSubscriptions.delete(topic);
    }
    // Also remove from queue if it's there
    const queueIndex = subscriptionQueue.findIndex(s => s.topic === topic);
    if (queueIndex !== -1) subscriptionQueue.splice(queueIndex, 1);
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
  }, { immediate: true });

  return { connect, disconnect, subscribe, unsubscribe, isConnected };
}
