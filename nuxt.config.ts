export default defineNuxtConfig({
  // fecha recomendada por Nuxt / Nitro (evita el WARN de compatibilidad)
  compatibilityDate: '2026-03-20',

  // CSS global
  css: ['~/assets/css/main.css'],

  // componentes automáticos
  components: true,

  // devtools
  devtools: { enabled: false },

  // módulos
  modules: ['@pinia/nuxt'],

  // PostCSS config (Nuxt usará estos plugins en la compilación)
  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
})