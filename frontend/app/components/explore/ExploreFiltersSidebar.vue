<template>
  <aside :class="['filters-panel', { 'filters-panel--open': isOpen }]">
    <div class="filters-panel__header">
      <div class="filters-panel__hero">
        <p class="filters-kicker">Filtrar catálogo</p>
        <h2>Refina tu búsqueda</h2>
        <p>
          Usa los filtros para acotar por categoría, género, estado, talla o precio sin perder el foco.
        </p>
      </div>
      <button
        type="button"
        class="filters-close-btn"
        @click="$emit('close-filters')"
        aria-label="Cerrar filtros"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
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
      <h3>Género</h3>
      <select :value="activeGender ?? ''" @change="updateQuerySelect('genero', $event)">
        <option value="">Todos</option>
        <option v-for="gender in genderOptions" :key="gender" :value="gender">{{ gender }}</option>
      </select>
    </section>

    <section class="filters-section">
      <h3>Talla</h3>
      <select :value="activeSize ?? ''" @change="updateQuerySelect('talla', $event)">
        <option value="">Todas</option>
        <option v-for="size in sizeOptions" :key="size" :value="size">{{ size }}</option>
      </select>
    </section>

    <section class="filters-section">
      <h3>Estado</h3>
      <select :value="activeState ?? ''" @change="updateQuerySelect('estado', $event)">
        <option value="">Todos</option>
        <option v-for="state in stateOptions" :key="state" :value="state">{{ state }}</option>
      </select>
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


  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { getSubcategoriesByKey } from '~/constants/categories'

type QueryLike = Record<string, string | number | null | undefined>

const route = useRoute()

defineProps<{
  totalResults: number
  categoryCounts?: Record<string, number>
  isOpen?: boolean
}>()

defineEmits<{
  'toggle-filters': []
  'close-filters': []
}>()

const sizeOptions = ['XS', 'S', 'M', 'L', 'XL']
const stateOptions = ['Nuevo', 'Como nuevo', 'Usado']
const genderOptions = ['Hombre', 'Mujer', 'Unisex']

const activeCategory = computed(() => route.query.cat as string | undefined)
const activeSubcategory = computed(() => route.query.subcat as string | undefined)
const activeGender = computed(() => route.query.genero as string | undefined)
const activeSize = computed(() => route.query.talla as string | undefined)
const activeState = computed(() => route.query.estado as string | undefined)
const minPrice = computed(() => route.query.minPrice as string | undefined)
const maxPrice = computed(() => route.query.maxPrice as string | undefined)
const sortValue = computed(() => route.query.sort as string | undefined)

const activeSubcategories = computed(() => getSubcategoriesByKey(activeCategory.value))

function linkFor(nextValues: QueryLike) {
  const next: Record<string, string> = { ...(route.query as Record<string, string>) }

  for (const [key, value] of Object.entries(nextValues)) {
    if (value === null || value === undefined || value === '') {
      delete next[key]
      continue
    }
    next[key] = String(value)
  }

  return { path: '/explorar', query: next }
}

function updateQuerySelect(field: 'genero' | 'talla' | 'estado', event: Event) {
  const target = event.target as HTMLSelectElement | null
  const next = { ...route.query }

  if (!target?.value) {
    delete next[field]
  } else {
    next[field] = target.value
  }

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

.filters-section__head,
.filters-section__head-with-toggle {
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

.section-toggle-btn {
  display: none;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #0f172a;
  transition: transform 0.2s ease, color 0.2s ease;
  flex-shrink: 0;
}

.section-toggle-btn:hover {
  color: #0f766e;
}

.section-toggle-btn svg {
  width: 18px;
  height: 18px;
  transition: transform 0.2s ease;
}

.section-toggle-btn[aria-expanded="false"] svg {
  transform: rotate(-90deg);
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.section-fade-enter-active,
.section-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.section-fade-enter-from,
.section-fade-leave-to {
  opacity: 0;
  transform: translateY(-6px);
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

.select-field select,
.price-grid input,
.filters-section select {
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

.filters-panel__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.filters-close-btn {
  display: none;
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  cursor: pointer;
  color: #0f172a;
  transition: color 0.2s ease;
}

.filters-close-btn:hover {
  color: #0f766e;
}

.filters-close-btn svg {
  width: 100%;
  height: 100%;
  stroke-width: 2.5;
}

@media (max-width: 920px) {
  .filters-panel {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    width: 100%;
    max-width: 100%;
    height: 100vh;
    border-radius: 0;
    border: none;
    background: #fff;
    box-shadow: none;
    z-index: 50;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
    padding: 16px;
    overflow-y: auto;
    gap: 16px;
  }

  .filters-panel--open {
    transform: translateX(0);
    box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
  }

  .filters-close-btn,
  .section-toggle-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .filters-panel__hero {
    padding-right: 0;
  }

  .filters-panel__hero h2 {
    font-size: 1.4rem;
  }

  .filters-panel__hero p {
    font-size: 13px;
  }
}

@media (max-width: 640px) {
  .filters-panel {
    padding: 12px;
    gap: 12px;
  }

  .filters-panel__header {
    position: sticky;
    top: 0;
    background: #fff;
    z-index: 10;
    padding-bottom: 12px;
    border-bottom: 1px solid #e2e8f0;
    margin: -12px -12px 0 -12px;
    padding: 12px;
  }

  .filters-panel__hero {
    margin-bottom: 0;
  }

  .filters-panel__hero p {
    display: none;
  }
}
</style>
