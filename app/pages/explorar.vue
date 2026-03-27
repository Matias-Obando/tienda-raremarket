<template>
  <div class="rm-container rm-page">
    <header class="top">


      <div class="subRow">
        <label class="sort">
          <span class="sortLabel">Orden</span>
          <select v-model="sort" class="sortSelect">
            <option value="new">Últimos añadidos</option>
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
import { onMounted, computed } from 'vue'
import ItemCard from '~/components/ItemCard.vue'
import { useItemsStore } from '~/stores/useItemsStore'

const store = useItemsStore()
const route = useRoute()


onMounted(async () => {
  if (store.items.length === 0) await store.fetchAll()
})

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
  if (!activeCategoria.value) return store.items
  return store.items.filter((it) => it.categoria === activeCategoria.value)
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
.rm-page {
  --card-max: 280px;
  --grid-gap: 28px;
}


.rm-container {
  box-sizing: border-box;
  max-width: 1360px;
  margin: 0 auto;
  padding-left: 40px;
  padding-right: 40px;
}

/* Empuja hacia abajo el bloque de cabecera (Orden / Mostrando)
   Usamos la altura calculada de la category nav para evitar solapamientos.
  Añadimos 10px extra de espacio visual. */
.top {
  margin-top: calc(var(--catnav-height, 56px) + 10px);
  margin-bottom: 12px;
}


@media (min-width: 900px) {
  .top { margin-top: calc(var(--catnav-height, 56px) + 14px); }
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
  font-size: 13px; display:flex; 
  gap:10px; 
  flex-wrap:wrap; 
  align-items:center; 
}


.top > .subRow,
.top > .hint {
  padding-left: 24px !important;
}


.subRow { margin-top:10px; display:flex; gap:12px; align-items:center; justify-content:space-between; flex-wrap:wrap; }
.sort { display:inline-flex; align-items:center; gap:8px; color:var(--rm-muted); font-size:13px; }
.sortLabel { display:none; }
.sortSelect { border:1px solid var(--rm-border); background:#fff; color:var(--rm-text); border-radius:999px; padding:8px 10px; }

@media (min-width:520px){ .sortLabel{ display:inline; } }
.results { color:var(--rm-muted); font-size:13px; }
.pill { display:inline-flex; align-items:center; gap:6px; border:1px solid var(--rm-border); background:#fff; color:var(--rm-text); border-radius:999px; padding:6px 10px; font-size:12px; font-weight:700; text-decoration:none; line-height:1; }
.pill:hover { border-color: rgba(0,0,0,0.18); }
.empty { padding:14px; border:1px dashed var(--rm-border); border-radius:var(--rm-radius); background:#fff; color:var(--rm-text); }
.emptyActions { margin-top:10px; display:flex; gap:10px; flex-wrap:wrap; }


.grid {
  display: grid;
  gap: var(--grid-gap);
  justify-content: center;
  width: 100%;
  box-sizing: border-box;
  margin: 18px 0 0;
  grid-template-columns: repeat(4, minmax(0, var(--card-max)));
  justify-items: center;
}


.grid > * {
  width: 100%;
  max-width: var(--card-max);
  box-sizing: border-box;
}


@media (max-width: 1100px) {
  .grid { grid-template-columns: repeat(3, minmax(0, calc(var(--card-max) - 40px))); }
}
@media (max-width: 850px) {
  .grid { grid-template-columns: repeat(2, minmax(0, calc(var(--card-max) - 80px))); }
}
@media (max-width: 520px) {
  .grid { grid-template-columns: repeat(1, 1fr); }
}


.grid img {
  display: block;
  width: 100%;
  height: auto;
  object-fit: cover;
}


::v-deep(.rm-catnav),
::v-deep(.rm-catnav__list) {
  max-width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  white-space: nowrap;
  box-sizing: border-box;
  padding-left: 12px;
  padding-right: 12px;
}
::v-deep(.rm-catnav__list) { display:flex; gap:12px; align-items:center; }
::v-deep(.rm-catnav__item) { flex:0 0 auto; white-space:nowrap; }
</style>