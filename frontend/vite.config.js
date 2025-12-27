import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import { fileURLToPath } from 'url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'globalThis',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    host: true, // Escuta em todas as interfaces
    port: 5173,
    strictPort: true,
    watch: {
      usePolling: true, // Necessário para hot-reload no Docker
    },
    hmr: {
      clientPort: 5173,
    },
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    emptyOutDir: true,
  },
  test: {
    environment: 'jsdom',
  },
});
