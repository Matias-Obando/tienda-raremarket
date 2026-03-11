<template>
  <div class="rm-container rm-page">
    <header class="top">
      <h1 class="title">
        Explorar
        <span v-if="activeLabel" class="titleTag">· {{ activeLabel }}</span>
      </h1>

      <div class="subRow">
        <label class="sort">
          <span class="sortLabel">Orden</span>
          <select v-model="sort" class="sortSelect">
            <option value="new">Recién llegados</option>
            <option value="price_asc">Precio: menor a mayor</option>
            <option value="price_desc">Precio: mayor a menor</option>
          </select>
        </label>

        <div class="results">{{ resultsText }}</div>
      </div>

      <p v-if="activeLabel || q" class="hint">
        <span v-if="q">Búsqueda: <strong>{{ q }}</strong></span>

        
        <button v-if="q" class="pill" type="button" @click="clearSearch">
          Quitar búsqueda
        </button>

        <NuxtLink v-if="activeLabel" :to="clearCatHref" class="pill">
          Quitar categoría
        </NuxtLink>
      </p>

      <p v-else class="hint">
        Mostrando: <strong>Todos</strong>
      </p>
    </header>

    <div v-if="filteredItems.length === 0" class="empty">
      No hay productos con ese filtro/búsqueda.

      <div class="emptyActions">
        <NuxtLink v-if="q && activeLabel" :to="clearSearchAndCatHref" class="pill">
          Quitar búsqueda y categoría
        </NuxtLink>

        <NuxtLink v-else-if="q" :to="clearSearchHref" class="pill">
          Quitar búsqueda
        </NuxtLink>

        <NuxtLink v-else-if="activeLabel" :to="clearCatHref" class="pill">
          Quitar categoría
        </NuxtLink>

        <NuxtLink v-else to="/explorar" class="pill">
          Ver todos
        </NuxtLink>
      </div>
    </div>

    <div v-else class="grid">
      <ItemCard v-for="it in filteredItems" :key="it.id" :item="it" />
    </div>
  </div>
</template>

<script setup lang="ts">
import ItemCard from '~/components/ItemCard.vue'
import { mockItems } from '~/mock/items'
import type { Categoria } from '~/mock/items'

const route = useRoute()

const cat = computed(() => {
  const v = route.query.cat
  return typeof v === 'string' && v.length ? v : null
})

const q = computed(() => {
  const v = route.query.q
  return typeof v === 'string' ? v : ''
})

const clearCatHref = computed(() => {
  const next: Record<string, any> = { ...route.query }
  delete next.cat
  return { path: '/explorar', query: next }
})

const clearSearchHref = computed(() => {
  const next: Record<string, any> = { ...route.query }
  delete next.q
  return { path: '/explorar', query: next }
})

const clearSearchAndCatHref = computed(() => {
  const next: Record<string, any> = { ...route.query }
  delete next.q
  delete next.cat
  return { path: '/explorar', query: next }
})

function clearSearch() {
  navigateTo(clearSearchHref.value)
}

const sort = computed<'new' | 'price_asc' | 'price_desc'>({
  get() {
    const v = route.query.sort
    if (v === 'new' || v === 'price_asc' || v === 'price_desc') return v
    return 'new'
  },
  set(v) {
    const next = { ...route.query }
    if (v === 'new') delete next.sort
    else next.sort = v
    navigateTo({ path: '/explorar', query: next })
  },
})

const mapCatToCategoria: Record<string, Categoria> = {
  shirts: 'Camisetas',
  pants: 'Pantalones',
  sneakers: 'Zapatillas',
  acc: 'Accesorios',
}

const activeCategoria = computed<Categoria | null>(() => {
  if (!cat.value) return null
  return mapCatToCategoria[cat.value] ?? null
})

const activeLabel = computed(() => activeCategoria.value)

const byCategoria = computed(() => {
  if (!activeCategoria.value) return mockItems
  return mockItems.filter((it) => it.categoria === activeCategoria.value)
})

function norm(s: string) {
  return s.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '')
}

const filteredItems = computed(() => {
  const query = norm(q.value.trim())

  const base = query
    ? byCategoria.value.filter((it) => {
        const haystack = norm(
          `${it.titulo} ${it.marca} ${it.categoria} ${it.estado} ${it.talla} ${it.descripcion ?? ''}`,
        )
        return haystack.includes(query)
      })
    : byCategoria.value

  const arr = [...base]

  if (sort.value === 'price_asc') arr.sort((a, b) => a.precioEur - b.precioEur)
  else if (sort.value === 'price_desc') arr.sort((a, b) => b.precioEur - a.precioEur)

  return arr
})

const resultsText = computed(() => {
  const n = filteredItems.value.length
  return n === 1 ? '1 producto' : `${n} productos`
})
</script>

<style scoped>
.top {
  margin-bottom: 12px;
}

.title {
  margin: 6px 0 10px;
  letter-spacing: -0.03em;
}

.titleTag {
  font-weight: 700;
  color: var(--rm-muted);
  font-size: 0.65em;
}

.hint {
  margin: 10px 0 0;
  color: var(--rm-muted);
  font-size: 13px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.subRow {
  margin-top: 10px;
  display: flex;
  gap: 12px;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}

.sort {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--rm-muted);
  font-size: 13px;
}

.sortLabel {
  display: none;
}

.sortSelect {
  border: 1px solid var(--rm-border);
  background: #fff;
  color: var(--rm-text);
  border-radius: 999px;
  padding: 8px 10px;
}

@media (min-width: 520px) {
  .sortLabel {
    display: inline;
  }
}

.results {
  color: var(--rm-muted);
  font-size: 13px;
}

.pill {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 1px solid var(--rm-border);
  background: #fff;
  color: var(--rm-text);
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 700;
  text-decoration: none;
  line-height: 1;
}

.pill:hover {
  border-color: rgba(0, 0, 0, 0.18);
}

.empty {
  padding: 14px;
  border: 1px dashed var(--rm-border);
  border-radius: var(--rm-radius);
  background: #fff;
  color: var(--rm-text);
}

.emptyActions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.grid {
  display: grid;
  gap: 16px;
  grid-template-columns: 1fr;
  margin-top: 14px;
}

@media (min-width: 520px) {
  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (min-width: 820px) {
  .grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (min-width: 1100px) {
  .grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}
</style>