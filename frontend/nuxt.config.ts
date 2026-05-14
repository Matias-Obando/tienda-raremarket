export default defineNuxtConfig({
  // fecha recomendada por Nuxt / Nitro (evita el WARN de compatibilidad)
  compatibilityDate: '2026-03-20',

  // Definir la carpeta raíz de la aplicación
  srcDir: 'app',

  // CSS global
  css: ['~/recursos/css/main.css'],

  dir: {
    pages: 'paginas',
    assets: 'recursos'
  },

  // componentes automáticos
  components: {
    dirs: ['~/componentes']
  },

  imports: {
    dirs: ['~/utilidades']
  },

  pinia: {
    storesDirs: ['./tiendas/**']
  },

  // devtools
  devtools: { enabled: false },

  // módulos
  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss',
    '@nuxtjs/color-mode'
  ],



  // PostCSS config (Nuxt usará estos plugins en la compilación)
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },

  runtimeConfig: {
    public: {
      API_BASE_URL: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8081/api',
      SUPABASE_URL: process.env.NUXT_PUBLIC_SUPABASE_URL || '',
      SUPABASE_ANON_KEY: process.env.NUXT_PUBLIC_SUPABASE_ANON_KEY || ''
    }
  },

  vite: {
    optimizeDeps: {
      include: [
        '@supabase/supabase-js',
      ]
    }
  }

  
})