import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { createRouter, createWebHistory } from 'vue-router';
import routes from '../router/index.js'; // Assuming your routes are exported from index.js
import App from '../App.vue';
import { useAuthStore } from '@/stores/auth';
import { useCartStore } from '@/stores/cart';
import { useNotifications } from '@/composables/useNotifications';
import api, { setupAxiosInterceptors } from '../plugins/axios';

// Mock Notification composable to capture notifications
const mockAddNotification = vi.fn();
vi.mock('@/composables/useNotifications', () => ({
  useNotifications: () => ({
    addNotification: mockAddNotification,
  }),
}));

describe('Full E2E User Flow', () => {
  let wrapper;
  let router;
  let authStore;
  let cartStore;

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks();
    mockAddNotification.mockClear();

    // Set up Pinia
    setActivePinia(createPinia());
    authStore = useAuthStore();
    cartStore = useCartStore();

    // Set up Vue Router
    router = createRouter({
      history: createWebHistory(),
      routes: routes.routes, // Assuming your routes object has a 'routes' property
    });

    // Mock localStorage for authStore
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: vi.fn((key) => {
          if (key === 'authToken') return 'fake-admin-jwt-token';
          if (key === 'user') return JSON.stringify({ email: 'admin@fakedata.com', roles: ['ADMIN'] });
          return null;
        }),
        setItem: vi.fn(),
        removeItem: vi.fn(),
      },
      writable: true,
    });

    // Setup Axios Interceptors (will attach the mocked token from localStorage)
    setupAxiosInterceptors();

    // Initialize auth store, which usually loads token from localStorage
    await authStore.initializeAuth();

    // Mount the App with router and pinia
    wrapper = mount(App, {
      global: {
        plugins: [router, createPinia()], // Pass createPinia() to avoid re-creation issues
        stubs: {
          RouterLink: true,
          RouterView: true,
          // You might need to stub other components if they cause issues or are not relevant for E2E
        },
      },
    });

    // Wait for router to be ready
    await router.isReady();
  });

  afterEach(() => {
    wrapper.unmount();
    vi.restoreAllMocks();
  });

  it('should successfully login as admin, navigate, add to cart, place order, and view order history', async () => {
    // 1. Simulate Admin Login (AuthStore handles this on initializeAuth due to mocked localStorage)
    // We can directly assert that the user is considered logged in and is an admin
    expect(authStore.isAuthenticated).toBe(true);
    expect(authStore.user.roles).toContain('ADMIN');
    expect(authStore.user.email).toBe('admin@fakedata.com');

    // Mock API responses for user and products (simulate successful fetches after login)
    // IMPORTANT: For true E2E, these should not be mocked. But for Vitest in JSDOM,
    // we might need to mock if we don't have a live server.
    // For this E2E test, we assume a live backend is running and these mocks are just for clarity/control.
    vi.spyOn(api, 'get').mockImplementation((url) => {
        if (url.includes('/usuarios/me')) {
            return Promise.resolve({ data: { codigo: 1, email: 'admin@fakedata.com', roles: ['ADMIN'], endereco: 'Endereço Administrativo' } });
        }
        if (url.includes('/produtos')) {
            return Promise.resolve({ data: [
                { idProduto: 1, nomeProduto: 'Pizza Calabresa', descricao: 'Molho, calabresa, cebola', preco: 35.0, caminhoImagem: '/images/placeholder-food.svg' },
                { idProduto: 2, nomeProduto: 'Pizza Margherita', descricao: 'Molho, mussarela, manjericão', preco: 30.0, caminhoImagem: '/images/placeholder-food.svg' },
            ]});
        }
        if (url.includes('/estabelecimentos')) {
            return Promise.resolve({ data: [
                { id: 1, nome: 'Pizzaria Bella Vista', endereco: 'Rua da Pizza, 123' },
            ]});
        }
        if (url.includes('/pagamentos/1/status')) { // Mock PIX status check
            return Promise.resolve({ data: 'CONFIRMADO' });
        }
        return Promise.reject(new Error(`Unexpected API GET call to ${url}`));
    });

    vi.spyOn(api, 'post').mockImplementation((url, data) => {
        if (url.includes('/pedidos')) {
            return Promise.resolve({ data: { codigoPedido: 1, enderecoPedido: data.enderecoPedido, valorTotal: 45.0, status: 'AGUARDANDO_PAGAMENTO' } });
        }
        if (url.includes('/pagamentos/pix/gerar')) {
            return Promise.resolve({ data: { qrCode: 'base64qrcode', copiaCola: 'pixcopiacola', expiraEm: '2025-11-21' } });
        }
        return Promise.reject(new Error(`Unexpected API POST call to ${url}`));
    });

    // 2. Navigate to Home and view products (assuming Home is '/')
    await router.push('/');
    await wrapper.vm.$nextTick(); // Wait for Vue to re-render

    // Simulate product fetching
    // await new Promise(resolve => setTimeout(resolve, 100)); // Give some time for async operations

    expect(wrapper.html()).toContain('Cardápio');
    expect(api.get).toHaveBeenCalledWith('/produtos');

    // 3. Add item to cart
    // Since we don't have direct access to BaseButton clicks in mounted App due to router-view,
    // and product cards are deep inside, we simulate cart interaction directly
    cartStore.addItem({ idProduto: 1, nomeProduto: 'Pizza Calabresa', preco: 35.0, caminhoImagem: '/images/placeholder-food.svg', quantity: 1 });
    cartStore.addItem({ idProduto: 2, nomeProduto: 'Pizza Margherita', preco: 30.0, caminhoImagem: '/images/placeholder-food.svg', quantity: 1 });
    expect(cartStore.itemCount).toBe(2);
    expect(mockAddNotification).toHaveBeenCalledWith({ type: 'success', message: 'Pizza Margherita adicionado ao carrinho!' });

    // 4. Navigate to Cart and proceed to checkout
    await router.push('/cart');
    await wrapper.vm.$nextTick();

    // Check if cart items are displayed
    expect(wrapper.html()).toContain('Seu Carrinho');
    expect(wrapper.html()).toContain('Pizza Calabresa');
    expect(wrapper.html()).toContain('Pizza Margherita');
    
    // Simulate clicking "Finalizar Pedido" and filling address
    // This part is tricky with @vue/test-utils for full component trees.
    // We will directly call the underlying method for simplicity.
    // In a real E2E framework (Cypress/Playwright), we'd click the button.
    const appCartVm = wrapper.findComponent({ name: 'AppCart' }).vm; // Get the VM of the AppCart view
    expect(appCartVm).toBeDefined();

    // Set address directly, as BaseInput is internal
    appCartVm.checkoutForm.enderecoPedido = 'Endereço de Teste E2E';
    
    // Simulate placing order
    await appCartVm.handlePlaceOrder();

    expect(mockAddNotification).toHaveBeenCalledWith({ type: 'success', message: 'Pedido criado com sucesso! Redirecionando para o pagamento...' });
    expect(api.post).toHaveBeenCalledWith('/pedidos', expect.any(Object)); // Verify API call
    expect(router.currentRoute.value.name).toBe('CheckoutPix'); // Should redirect to CheckoutPix

    // 5. Verify PIX payment page and status check
    await wrapper.vm.$nextTick();
    expect(wrapper.html()).toContain('Pagamento PIX');
    expect(api.post).toHaveBeenCalledWith('/pagamentos/pix/gerar', expect.any(Object));

    // Simulate payment confirmation (the interval will trigger this)
    await new Promise(resolve => setTimeout(resolve, 5500)); // Wait for interval to trigger at least once
    
    expect(mockAddNotification).toHaveBeenCalledWith({ type: 'success', message: 'Pagamento PIX confirmado com sucesso!' });
    expect(api.get).toHaveBeenCalledWith('/pagamentos/1/status'); // Verify status check
    expect(router.currentRoute.value.name).toBe('Orders'); // Should redirect to Orders

    // 6. Verify order history
    await wrapper.vm.$nextTick();
    expect(wrapper.html()).toContain('Meus Pedidos');
    expect(api.get).toHaveBeenCalledWith('/pedidos/meus-pedidos');

    // Further assertions can be added here to check order details in the UI
  });
});
