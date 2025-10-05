import { defineStore } from 'pinia';

const CART_STORAGE_KEY = 'cartItems';

// Helper to safely get cart from localStorage
const getCartFromStorage = () => {
  const storedCart = localStorage.getItem(CART_STORAGE_KEY);
  if (!storedCart) return [];
  try {
    return JSON.parse(storedCart);
  } catch (e) {
    console.error("Failed to parse cart from localStorage, clearing cart.", e);
    localStorage.removeItem(CART_STORAGE_KEY);
    return [];
  }
};

// Helper to save cart to localStorage
const saveCartToStorage = (cartItems) => {
    try {
        localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cartItems));
    } catch (e) {
        console.error("Failed to save cart to localStorage", e);
    }
};

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: getCartFromStorage(),
    isOpen: false,
  }),
  getters: {
    itemCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    total: (state) => state.items.reduce((sum, item) => sum + (item.preco * item.quantity), 0),
  },
  actions: {
    addItem(product) {
      const existingItem = this.items.find(item => item.idProduto === product.idProduto);
      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.items.push({ 
            idProduto: product.idProduto,
            nomeProduto: product.nomeProduto,
            preco: product.preco,
            caminhoImagem: product.caminhoImagem,
            quantity: 1 
        });
      }
      saveCartToStorage(this.items);
    },
    removeItem(productId) {
      this.items = this.items.filter(item => item.idProduto !== productId);
      saveCartToStorage(this.items);
    },
    updateQuantity(productId, quantity) {
      const itemToUpdate = this.items.find(item => item.idProduto === productId);
      if (itemToUpdate) {
        if (quantity > 0) {
          itemToUpdate.quantity = quantity;
        } else {
          // If quantity is 0 or less, remove the item
          this.removeItem(productId);
          return; // removeItem already saves, so we exit here.
        }
      }
      saveCartToStorage(this.items);
    },
    clearCart() {
      this.items = [];
      saveCartToStorage(this.items);
    },
    toggleCart() {
      this.isOpen = !this.isOpen;
    },
    openCart() {
      this.isOpen = true;
    },
    closeCart() {
      this.isOpen = false;
    },
  },
});
