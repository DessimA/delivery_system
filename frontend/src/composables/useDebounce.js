import { ref, onUnmounted } from 'vue';

export function useDebounce(fn, delay) {
  const timeoutId = ref(null);

  onUnmounted(() => {
    clearTimeout(timeoutId.value);
  });

  return (...args) => {
    clearTimeout(timeoutId.value);
    timeoutId.value = setTimeout(() => {
      fn(...args);
    }, delay);
  };
}
