export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const currentBaseUrl = config.public.API_BASE_URL

  if (currentBaseUrl === 'http://localhost:8080/api') {
    config.public.API_BASE_URL = 'http://localhost:8081/api'
  }
})