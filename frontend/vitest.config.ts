/// <reference types="vitest" />
import { defineConfig } from 'vitest/config'
import react from '@vitejs/plugin-react'

// Eigenständige Vitest-Konfiguration: Vitest 2.x bringt sein eigenes Vite 5
// mit, deshalb hier die Test-Optionen + `esbuild.jsx` getrennt von
// vite.config.ts (Vite 8) führen — sonst kollidieren die Typen.
export default defineConfig({
  plugins: [react({ jsxRuntime: 'automatic' })],
  esbuild: {
    jsx: 'automatic',
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/test/setup.ts'],
    css: false,
  },
})
