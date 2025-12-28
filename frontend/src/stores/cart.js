import { defineStore } from 'pinia';
import { storage } from '@/utils/storage';

const CART_STORAGE_KEY = 'cart_items';

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: storage.get(CART_STORAGE_KEY) || [],
    isOpen: false,
  }),
  getters: {
    itemCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    total: (state) => state.items.reduce((sum, item) => sum + (item.price * item.quantity), 0),
  },
  actions: {
    addItem(product) {
      // Defensive check to ensure product has required English keys
      if (!product.id || !product.name) {
          console.error("Attempted to add malformed product to cart:", product);
          return;
      }

      const existingItem = this.items.find(item => item.id === product.id);
      if (existingItem) {
        existingItem.quantity++;
      } else {
        this.items.push({ 
            id: product.id,
            name: product.name,
            price: product.price,
            imageUrl: product.imageUrl,
            quantity: 1 
        });
      }
      this.saveCart();
    },
    removeItem(productId) {
      this.items = this.items.filter(item => item.id !== productId);
      this.saveCart();
    },
    updateQuantity(productId, quantity) {
      const itemToUpdate = this.items.find(item => item.id === productId);
      if (itemToUpdate) {
        if (quantity > 0) {
          itemToUpdate.quantity = quantity;
        } else {
          this.removeItem(productId);
          return;
        }
      }
      this.saveCart();
    },
    clearCart() {
      this.items = [];
      this.saveCart();
    },
    saveCart() {
      storage.set(CART_STORAGE_KEY, this.items);
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
