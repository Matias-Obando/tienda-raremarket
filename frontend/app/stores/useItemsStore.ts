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

type CreateItemRequest = {
  titulo: string
  descripcion: string
  precioEur: number
  categoria: string
  subcategoria?: string
  marca: string
  talla: string
  estado: string
  imagen: string
  images: string[]
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
        const config = useRuntimeConfig()
        const params: any = {}
        if (this.filters.query) params.query = this.filters.query
        if (this.filters.categoria) params.categoria = this.filters.categoria
        if (this.filters.talla) params.talla = this.filters.talla
        if (this.filters.estado) params.estado = this.filters.estado
        if (this.filters.minPrice != null) params.minPrice = this.filters.minPrice
        if (this.filters.maxPrice != null) params.maxPrice = this.filters.maxPrice
        this.items = await $fetch<Item[]>(`${config.public.API_BASE_URL}/items`, { params })
      } catch (e) {
        console.error('Error cargando items desde backend:', e)
        this.items = []
      } finally {
        this.loading = false
      }
    },

    async fetchById(id: string) {
      this.loading = true
      try {
        const config = useRuntimeConfig()
        return await $fetch<Item>(`${config.public.API_BASE_URL}/items/${id}`)
      } catch (e) {
        console.error('Error cargando item por id:', e)
        return null
      } finally {
        this.loading = false
      }
    },
    async createItem(payload: CreateItemRequest) {
      const config = useRuntimeConfig()
      const { loadSessionUser } = useSessionUser()
      const user = loadSessionUser().value

      if (!user?.token) {
        throw new Error('Debes iniciar sesion para publicar un articulo.')
      }

      const created = await $fetch<Item>(`${config.public.API_BASE_URL}/items`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.token}`
        },
        body: payload
      })

      this.items = [created, ...this.items]
      return created
    },
    async uploadImages(files: File[]) {
      const config = useRuntimeConfig()
      const { loadSessionUser } = useSessionUser()
      const user = loadSessionUser().value

      if (!user?.token) {
        throw new Error('Debes iniciar sesion para subir imagenes.')
      }
      if (!files.length) {
        throw new Error('Selecciona al menos una imagen.')
      }

      const formData = new FormData()
      files.forEach((file) => {
        formData.append('files', file)
      })

      const response = await $fetch<{ urls: string[] }>(`${config.public.API_BASE_URL}/items/images`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.token}`
        },
        body: formData
      })

      return response.urls ?? []
    },
    async cleanupUploadedImages(urls: string[]) {
      const config = useRuntimeConfig()
      const { loadSessionUser } = useSessionUser()
      const user = loadSessionUser().value

      if (!user?.token || !urls.length) {
        return 0
      }

      const response = await $fetch<{ deleted: number }>(`${config.public.API_BASE_URL}/items/images/cleanup`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${user.token}`
        },
        body: { urls }
      })

      return response.deleted ?? 0
    },
    async deleteItem(itemId: string) {
      const config = useRuntimeConfig()
      const { loadSessionUser } = useSessionUser()
      const user = loadSessionUser().value

      if (!user?.token) {
        throw new Error('Debes iniciar sesion para eliminar articulos.')
      }

      await $fetch(`${config.public.API_BASE_URL}/items/${itemId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${user.token}`
        }
      })

      this.items = this.items.filter((item) => item.id !== itemId)
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