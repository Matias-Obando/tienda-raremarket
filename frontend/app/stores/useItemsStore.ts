import { defineStore } from 'pinia'
import type { Item, Categoria, Talla, Estado } from './items'
import { mockItems } from './items'
import {
  matchesCategorySelection,
  matchesSubcategorySelection,
  normalizeCategoryText,
  resolveCategoryLabel
} from '~/constants/categories'

type Filters = {
  query: string
  categoria?: Categoria | undefined
  subcategoria?: string | undefined
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

type UpdateItemRequest = CreateItemRequest

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
      const q = normalizeCategoryText(state.filters.query)
      return state.items.filter((it) => {
        if (q) {
          const inTitle = normalizeCategoryText(it.titulo).includes(q)
          const inDesc = normalizeCategoryText(it.descripcion).includes(q)
          if (!inTitle && !inDesc) return false
        }
        if (state.filters.categoria && !matchesCategorySelection(it.categoria, state.filters.categoria)) return false
        if (state.filters.subcategoria && !matchesSubcategorySelection(it.categoria, it.subcategoria, state.filters.subcategoria)) return false
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
        if (this.filters.categoria) params.categoria = resolveCategoryLabel(this.filters.categoria) ?? this.filters.categoria
        if (this.filters.subcategoria) params.subcategoria = this.filters.subcategoria
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
    async updateItem(itemId: string, payload: UpdateItemRequest) {
      const config = useRuntimeConfig()
      const { loadSessionUser } = useSessionUser()
      const user = loadSessionUser().value

      if (!user?.token) {
        throw new Error('Debes iniciar sesion para editar un articulo.')
      }

      const updated = await $fetch<Item>(`${config.public.API_BASE_URL}/items/${itemId}`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${user.token}`
        },
        body: payload
      })

      this.items = this.items.map((item) => (item.id === itemId ? updated : item))
      return updated
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
      this.filters = { query: '', categoria: undefined, subcategoria: undefined, talla: undefined, estado: undefined, minPrice: null, maxPrice: null }
    },
    // getById local solo si ya tienes los items cargados
    getById(id: string) {
      return this.items.find((i) => i.id === id)
    }
  }
})