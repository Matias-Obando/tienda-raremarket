import { defineStore } from 'pinia'
import type { Item, Categoria, Talla, Estado } from './items'
import { mockItems } from './items'
import axios from 'axios'

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
    async fetchAll() {
      this.loading = true
      try {
        const params: any = {}
        if (this.filters.query) params.query = this.filters.query
        if (this.filters.categoria) params.categoria = this.filters.categoria
        if (this.filters.talla) params.talla = this.filters.talla
        if (this.filters.estado) params.estado = this.filters.estado
        if (this.filters.minPrice != null) params.minPrice = this.filters.minPrice
        if (this.filters.maxPrice != null) params.maxPrice = this.filters.maxPrice
        const res = await axios.get('/api/items', { params })
        this.items = res.data
      } catch (e) {
        // Puedes agregar manejo de error aquí
        this.items = []
      } finally {
        this.loading = false
      }
    },

    async fetchById(id: string) {
      this.loading = true
      try {
        const res = await axios.get(`/api/items/${id}`)
        return res.data
      } catch (e) {
        return null
      } finally {
        this.loading = false
      }
    },
    setFilters(payload: Partial<Filters>) {
      this.filters = { ...this.filters, ...payload }
    },
    clearFilters() {
      this.filters = { query: '', categoria: undefined, talla: undefined, estado: undefined, minPrice: null, maxPrice: null }
    },
    // getById local solo si ya tienes los items cargados
    getById(id: string) {
      return this.items.find((i) => i.id === id)
    }
  }
})