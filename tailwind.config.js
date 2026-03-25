module.exports = {
  content: [
    './app.vue',
    './app/**/*.vue',
    './components/**/*.vue',
    './layouts/**/*.vue',
    './pages/**/*.vue',
    './composables/**/*.{js,ts}',
    './plugins/**/*.{js,ts}',
    // si tienes .js/.ts en otras carpetas añádelas
  ],
  theme: {
    extend: {
      // aquí puedes mapear tus variables CSS a utilidades si quieres
      borderRadius: {
        'rm': '14px',
      }
    }
  },
  plugins: [],
}