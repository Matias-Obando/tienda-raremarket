import { ref } from 'vue'

export function useShippings() {
  const shippings = ref([])
  const loading = ref(false)

  async function fetchShippings() {
    loading.value = true
    shippings.value = await $fetch('/api/shippings')
    loading.value = false
  }

  async function createShipping(shipping: any) {
    return await $fetch('/api/shippings', { method: 'POST', body: shipping })
  }

  return { shippings, loading, fetchShippings, createShipping }
}
