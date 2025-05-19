import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig(({ mode }) => {
  const proxy = mode === 'development' ? {
    '/auth': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      secure: false,
      rewrite: (path) => path.replace('/auth', '')
    },
    '/books': {
      target: 'http://localhost:8081',
      changeOrigin: true,
      secure: false,
      rewrite: (path) => path.replace('/books', '')
    }
  } : {};

  return {
    plugins: [react()],
    server: {
      port: 3000,
      proxy
    }
  };
});