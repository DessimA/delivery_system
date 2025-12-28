/**
 * Abstração para armazenamento de dados no frontend.
 * Atualmente usa sessionStorage para maior segurança contra XSS comparado ao localStorage.
 */

const STORAGE_PREFIX = 'delivery_app_';

export const storage = {
  get(key) {
    const value = sessionStorage.getItem(`${STORAGE_PREFIX}${key}`);
    try {
      return JSON.parse(value);
    } catch {
      return value;
    }
  },

  set(key, value) {
    const stringValue = typeof value === 'object' ? JSON.stringify(value) : value;
    sessionStorage.setItem(`${STORAGE_PREFIX}${key}`, stringValue);
  },

  remove(key) {
    sessionStorage.removeItem(`${STORAGE_PREFIX}${key}`);
  },

  clear() {
    sessionStorage.clear();
  }
};
