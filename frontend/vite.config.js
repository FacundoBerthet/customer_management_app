import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Detect if we are building for GitHub Pages (repository site)
// Origin is https://<user>.github.io and assets served under /customer_management_app/
// We allow overriding via env VITE_BASE (local/dev unaffected)
const repoBase = '/customer_management_app/'
const isPages = process.env.PAGES === 'true' || process.env.GITHUB_PAGES === 'true'

export default defineConfig({
  base: process.env.VITE_BASE || (isPages ? repoBase : '/'),
  plugins: [react()],
})
