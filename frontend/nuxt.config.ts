export default defineNuxtConfig({
  
  compatibilityDate: '2026-03-20',

  srcDir: 'app',

  
  css: ['~/recursos/css/main.css'],

  dir: {
    pages: 'paginas',
    assets: 'recursos'
  },


  components: {
    dirs: ['~/componentes']
  },

  imports: {
    dirs: ['~/utilidades']
  },

  pinia: {
    storesDirs: ['./tiendas/**']
  },

  
  devtools: { enabled: false },

  
  modules: [
    '@pinia/nuxt',
    '@nuxtjs/tailwindcss',
    '@nuxtjs/color-mode'
  ],




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