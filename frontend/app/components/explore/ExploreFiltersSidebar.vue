<template>
  <aside class="filters-panel">
    <div class="filters-panel__hero">
      <p class="filters-kicker">Filtrar catálogo</p>
      <h2>Refina tu búsqueda</h2>
      <p>
        Usa los filtros para acotar por categoría, estado, talla o precio sin perder el foco.
      </p>
    </div>

    <section class="filters-section">
      <div class="filters-section__head">
        <h3>Categoría</h3>
        <NuxtLink to="/explorar" class="filters-reset">Limpiar</NuxtLink>
      </div>

      <div class="select-stack">

        <label class="select-field">
          <span>Tipo</span>
          <select :value="activeSubcategory ?? ''" :disabled="!activeCategory" @change="updateSubcategory">
            <option value="">Todas</option>
            <option v-for="subcategory in activeSubcategories" :key="subcategory" :value="subcategory">
              {{ subcategory }}
            </option>
          </select>
        </label>
      </div>
    </section>

    <section class="filters-section">
      <h3>Talla</h3>
      <div class="chip-row">
        <NuxtLink
          :to="linkFor({ talla: null })"
          class="chip"
          :class="{ 'chip--active': !activeSize }"
        >
          Todas
        </NuxtLink>
        <NuxtLink
          v-for="size in sizeOptions"
          :key="size"
          :to="linkFor({ talla: size })"
          class="chip"
          :class="{ 'chip--active': activeSize === size }"
        >
          {{ size }}
        </NuxtLink>
      </div>
    </section>

    <section class="filters-section">
      <h3>Estado</h3>
      <div class="chip-row">
        <NuxtLink
          :to="linkFor({ estado: null })"
          class="chip"
          :class="{ 'chip--active': !activeState }"
        >
          Todos
        </NuxtLink>
        <NuxtLink
          v-for="state in stateOptions"
          :key="state"
          :to="linkFor({ estado: state })"
          class="chip"
          :class="{ 'chip--active': activeState === state }"
        >
          {{ state }}
        </NuxtLink>
      </div>
    </section>

    <section class="filters-section">
      <h3>Precio</h3>
      <div class="price-grid">
        <label>
          <span>Mínimo</span>
          <input :value="minPrice ?? ''" type="number" min="0" placeholder="0" @change="updatePrice('minPrice', $event)" />
        </label>
        <label>
          <span>Máximo</span>
          <input :value="maxPrice ?? ''" type="number" min="0" placeholder="200" @change="updatePrice('maxPrice', $event)" />
        </label>
      </div>
    </section>

    <section class="filters-section">
      <h3>Orden</h3>
      <select :value="sortValue" @change="updateSort">
        <option value="">Más recientes</option>
        <option value="price_asc">Precio: menor a mayor</option>
        <option value="price_desc">Precio: mayor a menor</option>
      </select>
    </section>

    <section class="filters-section filters-section--summary">
      <p class="summary-label">Resultados actuales</p>
      <div class="summary-value">{{ totalResults }}</div>
      <NuxtLink to="/explorar" class="summary-link">Ver catálogo limpio</NuxtLink>
    </section>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CATEGORY_TREE, getSubcategoriesByKey } from '~/constants/categories'

type QueryLike = Record<string, string | number | null | undefined>

const route = useRoute()

const props = defineProps<{
  totalResults: number
  categoryCounts?: Record<string, number>
}>()

const categoryOptions = CATEGORY_TREE
const sizeOptions = ['XS', 'S', 'M', 'L', 'XL']
const stateOptions = ['Nuevo', 'Como nuevo', 'Usado']

const activeCategory = computed(() => route.query.cat as string | undefined)
const activeSubcategory = computed(() => route.query.subcat as string | undefined)
const activeSize = computed(() => route.query.talla as string | undefined)
const activeState = computed(() => route.query.estado as string | undefined)
const minPrice = computed(() => route.query.minPrice as string | undefined)
const maxPrice = computed(() => route.query.maxPrice as string | undefined)
const sortValue = computed(() => route.query.sort as string | undefined)

const activeSubcategories = computed(() => getSubcategoriesByKey(activeCategory.value))

const categoryCounts = computed(() => props.categoryCounts ?? {})

function isCategoryActive(categoryKey: string) {
  return activeCategory.value === categoryKey
}

function linkFor(nextValues: QueryLike) {
  const next: Record<string, string> = { ...route.query } as Record<string, string>

  for (const [key, value] of Object.entries(nextValues)) {
    if (value === null || value === undefined || value === '') {
      delete next[key]
      continue
    }
    next[key] = String(value)
  }

  return { path: '/explorar', query: next }
}

function updateCategory(event: Event) {
  const target = event.target as HTMLSelectElement | null
  const next = { ...route.query }

  if (!target?.value) {
    delete next.cat
    delete next.subcat
    return navigateTo({ path: '/explorar', query: next })
  }

  next.cat = target.value
  delete next.subcat
  return navigateTo({ path: '/explorar', query: next })
}

function updateSubcategory(event: Event) {
  const target = event.target as HTMLSelectElement | null
  const next = { ...route.query }

  if (!target?.value) {
    delete next.subcat
  } else {
    next.subcat = target.value
  }

  return navigateTo({ path: '/explorar', query: next })
}

function updatePrice(field: 'minPrice' | 'maxPrice', event: Event) {
  const target = event.target as HTMLInputElement | null
  const value = target?.value?.trim()
  const next = { ...route.query }

  if (!value) {
    delete next[field]
    return navigateTo({ path: '/explorar', query: next })
  }

  next[field] = value
  return navigateTo({ path: '/explorar', query: next })
}

function updateSort(event: Event) {
  const target = event.target as HTMLSelectElement | null
  const next = { ...route.query }

  if (!target?.value) {
    delete next.sort
  } else {
    next.sort = target.value
  }

  return navigateTo({ path: '/explorar', query: next })
}
</script>

<style scoped>
.filters-panel {
  display: grid;
  gap: 18px;
  position: sticky;
  top: 88px;
  align-self: start;
  padding: 20px;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.96));
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.08);
}

.filters-panel__hero h2,
.filters-section h3 {
  margin: 0;
  color: #0f172a;
  letter-spacing: -0.03em;
}

.filters-panel__hero p {
  margin: 8px 0 0;
  color: #64748b;
  line-height: 1.5;
  font-size: 14px;
}

.filters-kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 11px;
  font-weight: 800;
  color: #0f766e;
}

.filters-section {
  display: grid;
  gap: 12px;
}

.filters-section__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.filters-reset,
.summary-link {
  color: #0f766e;
  font-size: 13px;
  font-weight: 700;
  text-decoration: none;
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  border-radius: 16px;
  border: 1px solid #dbe3ec;
  color: #334155;
  text-decoration: none;
  background: #fff;
  transition: border-color 0.12s ease, transform 0.12s ease, box-shadow 0.12s ease;
}

.chip:hover {
  transform: translateY(-1px);
  border-color: rgba(15, 118, 110, 0.35);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.06);
}

.chip--active {
  border-color: #0f766e;
  background: #ecfeff;
  color: #0f766e;
}

.select-stack {
  display: grid;
  gap: 12px;
}

.select-field {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #475569;
}

.select-field select {
  width: 100%;
  border: 1px solid #dbe3ec;
  border-radius: 14px;
  padding: 11px 12px;
  background: #fff;
  color: #0f172a;
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.price-grid label {
  display: grid;
  gap: 6px;
  font-size: 13px;
  color: #475569;
}

.price-grid input,
select {
  width: 100%;
  border: 1px solid #dbe3ec;
  border-radius: 14px;
  padding: 11px 12px;
  background: #fff;
  color: #0f172a;
}

.filters-section--summary {
  padding: 16px;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(15, 118, 110, 0.08), rgba(255, 255, 255, 0.92));
}

.summary-label {
  margin: 0;
  color: #475569;
  font-size: 13px;
}

.summary-value {
  font-size: 2rem;
  font-weight: 800;
  letter-spacing: -0.05em;
  color: #0f172a;
}

.summary-link {
  margin-top: 4px;
}

@media (max-width: 1200px) {
  .filters-panel {
    position: static;
  }
}
</style>