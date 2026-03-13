import { defineStore } from 'pinia'
import type { Item, Categoria, Talla, Estado } from './items'
import { mockItems } from './items'

type Filters = {
  query: string
  categoria?: Categoria | undefined
  talla?: Talla | undefined
  estado?: Estado | undefined
  minPrice?: number | null
  maxPrice?: number | null
}

export const useItemsStore = defineStore('items', {
  state: () => ({
    items: [] as Item[],
    loading: false,
    filters: {
      query: '',
      categoria: undefined,
      talla: undefined,
      estado: undefined,
      minPrice: null,
      maxPrice: null
    } as Filters
  }),
  getters: {
    total: (state) => state.items.length,
    filteredItems: (state) => {
      const q = state.filters.query?.trim().toLowerCase() ?? ''
      return state.items.filter((it) => {
        if (q) {
          const inTitle = it.titulo.toLowerCase().includes(q)
          const inDesc = (it.descripcion || '').toLowerCase().includes(q)
          if (!inTitle && !inDesc) return false
        }
        if (state.filters.categoria && it.categoria !== state.filters.categoria) return false
        if (state.filters.talla && it.talla !== state.filters.talla) return false
        if (state.filters.estado && it.estado !== state.filters.estado) return false
        if (state.filters.minPrice != null && it.precioEur < state.filters.minPrice) return false
        if (state.filters.maxPrice != null && it.precioEur > state.filters.maxPrice) return false
        return true
      })
    }
  },
  actions: {
    async fetchMock() {
      this.loading = true
      // pequeña latencia para simular petición
      await new Promise((r) => setTimeout(r, 120))
      this.items = mockItems
      this.loading = false
    },
    setFilters(payload: Partial<Filters>) {
      this.filters = { ...this.filters, ...payload }
    },
    clearFilters() {
      this.filters = { query: '', categoria: undefined, talla: undefined, estado: undefined, minPrice: null, maxPrice: null }
    },
    getById(id: string) {
      return this.items.find((i) => i.id === id)
    }
  }
})