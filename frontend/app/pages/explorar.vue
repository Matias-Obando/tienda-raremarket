<template>
  <div class="rm-page">
    <section
      v-if="showPromoHero"
      class="promo-hero"
      :style="{ '--promo-hero-image': `url(${promoHeroImage})` }"
    >
      <div class="rm-container promo-hero__inner">
        <div class="promo-hero__content">
          <p class="promo-kicker">Moda sostenible · Venta rápida</p>
          <h1 class="promo-title">¿Tienes ropa en tu armario que ya no usas?</h1>
          <p class="promo-text">
            Sube prendas que ya no uses y dales una segunda vida. Publicar es rápido y tus
            artículos aparecen junto al resto del catálogo.
          </p>

          <div class="promo-actions">
            <NuxtLink to="/vender" class="promo-btn promo-btn--primary">Vender ahora</NuxtLink>
            <a href="#catalogo" class="promo-btn promo-btn--ghost">Ver productos</a>
          </div>

          <div class="promo-stats" aria-hidden="true">
            <span>Publica en minutos</span>
            <span>Compra y vende</span>
            <span>Envía con facilidad</span>
          </div>
        </div>
      </div>
    </section>

    <div :class="['rm-container', 'explore-layout', { 'no-sidebar': showPromoHero }]">
      <ExploreFiltersSidebar v-if="!showPromoHero" :total-results="filteredItems.length" :category-counts="categoryCounts" />

      <main class="explore-content">
        <div v-if="!showPromoHero" class="explore-head">
          <div>
            <p class="explore-kicker">Marketplace</p>
            <h2 id="catalogo">Explorar artículos</h2>
            <p>Filtra por categoría, subcategoría, talla, estado y precio desde el panel lateral.</p>
          </div>

          <div v-if="!showPromoHero" class="explore-badge">{{ filteredItems.length }} resultados</div>
        </div>

        <div v-if="filteredItems.length === 0" class="empty">
          No hay productos con ese filtro/búsqueda.

          <div class="emptyActions">
            <NuxtLink to="/explorar" class="pill">Ver todos</NuxtLink>
          </div>
        </div>

        <div v-else class="grid">
          <ItemCard v-for="it in filteredItems" :key="it.id" :item="it" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import ItemCard from '~/components/ItemCard.vue'
import ExploreFiltersSidebar from '~/components/explore/ExploreFiltersSidebar.vue'
import { useItemsStore } from '~/stores/useItemsStore'
import {
  matchesCategorySelection,
  matchesSubcategorySelection,
  normalizeCategoryText,
  resolveCategoryLabel,
  resolveCategoryKey,
  composeCategoriaLabel,
  parseCategoriaLabel
} from '~/constants/categories'

const store = useItemsStore()
const route = useRoute()
const promoHeroImage = '/bg/bg.png'


onMounted(async () => {
  await store.fetchAll()
})

const cat = computed(() => {
  const v = route.query.cat
  return typeof v === 'string' && v.length ? v : null
})

const q = computed(() => {
  const v = route.query.q
  return typeof v === 'string' ? v : ''
})

const activeSize = computed(() => {
  const v = route.query.talla
  return typeof v === 'string' && v.length ? v : null
})

const activeState = computed(() => {
  const v = route.query.estado
  return typeof v === 'string' && v.length ? v : null
})

const minPrice = computed(() => {
  const v = route.query.minPrice
  return typeof v === 'string' && v.length ? Number(v) : null
})

const maxPrice = computed(() => {
  const v = route.query.maxPrice
  return typeof v === 'string' && v.length ? Number(v) : null
})

const sortValue = computed(() => {
  const v = route.query.sort
  return typeof v === 'string' && v.length ? v : ''
})

const activeCategoria = computed<string | null>(() => {
  if (!cat.value) return null
  return cat.value
})

const activeLabel = computed(() => activeCategoria.value)

const subcat = computed(() => {
  const v = route.query.subcat
  return typeof v === 'string' && v.length ? v : null
})

const showPromoHero = computed(() => !cat.value && !subcat.value && !q.value.trim())

const categoryCounts = computed(() => {
  return store.items.reduce((acc, item) => {
    const key = resolveCategoryKey(item.categoria) ?? resolveCategoryKey(parseCategoriaLabel(item.categoria).parent) ?? item.categoria
    acc[key] = (acc[key] ?? 0) + 1
    return acc
  }, {} as Record<string, number>)
})


const byCategoria = computed(() => {
  if (!activeCategoria.value) return store.items
  return store.items.filter((it) => matchesCategorySelection(it.categoria, activeCategoria.value))
})

const bySubcategoria = computed(() => {
  if (!subcat.value) return byCategoria.value

  return byCategoria.value.filter((it) => {
    if (matchesSubcategorySelection(it.categoria, it.subcategoria, subcat.value)) {
      return true
    }

    const normalizedSubcat = normalizeCategoryText(subcat.value)
    const haystack = normalizeCategoryText(`${it.titulo} ${it.descripcion ?? ''}`)
    return haystack.includes(normalizedSubcat)
  })
})

const filteredItems = computed(() => {
  const query = normalizeCategoryText(q.value)

  const base = query
    ? bySubcategoria.value.filter((it) => {
        const haystack = normalizeCategoryText(
          `${it.titulo} ${it.marca} ${it.categoria} ${it.subcategoria ?? ''} ${it.estado} ${it.talla} ${it.descripcion ?? ''}`,
        )
        return haystack.includes(query)
      })
    : bySubcategoria.value

  const withExtraFilters = base.filter((it) => {
    if (activeSize.value && it.talla !== activeSize.value) return false
    if (activeState.value && it.estado !== activeState.value) return false
    if (minPrice.value != null && !Number.isNaN(minPrice.value) && it.precioEur < minPrice.value) return false
    if (maxPrice.value != null && !Number.isNaN(maxPrice.value) && it.precioEur > maxPrice.value) return false
    return true
  })

  const sorted = [...withExtraFilters]
  if (sortValue.value === 'price_asc') {
    sorted.sort((a, b) => a.precioEur - b.precioEur)
  } else if (sortValue.value === 'price_desc') {
    sorted.sort((a, b) => b.precioEur - a.precioEur)
  }

  return sorted
})
</script>

<style scoped>
.rm-page {
  --card-max: 280px;
  --grid-gap: 28px;
  min-height: 100vh;
  background: var(--rm-page-bg);
}


.rm-container {
  box-sizing: border-box;
  max-width: 1360px;
  margin: 0 auto;
  padding-left: 40px;
  padding-right: 40px;
}

.explore-layout {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
  margin-top: 18px;
}

.explore-content {
  min-width: 0;
}

.explore-head {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.explore-kicker {
  margin: 0 0 6px;
  color: #0f766e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 11px;
  font-weight: 800;
}

.explore-head h2 {
  margin: 0;
  font-size: clamp(1.6rem, 2.3vw, 2.2rem);
  letter-spacing: -0.04em;
}

.explore-head p {
  margin: 8px 0 0;
  color: #64748b;
}

.explore-badge {
  padding: 10px 14px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #dbe3ec;
  color: #0f172a;
  font-weight: 700;
  white-space: nowrap;
}

.promo-hero {
  position: relative;
  overflow: hidden;
  min-height: 380px;
  width: 100vw;
  margin: -21px calc(50% - 50vw) 12px;
  border-radius: 0;
  background-image:
    linear-gradient(90deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.9) 34%, rgba(255, 255, 255, 0.1) 72%),
    var(--promo-hero-image);
  background-position: center, center;
  background-repeat: no-repeat, no-repeat;
  background-size: cover, cover;
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
}

.promo-hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 20%, rgba(255, 255, 255, 0.18), transparent 32%);
  pointer-events: none;
}

.promo-hero__content {
  position: relative;
  z-index: 1;
  max-width: 460px;
  margin: 0;
  padding: 28px 0;
}

.promo-hero__inner {
  min-height: 380px;
  display: flex;
  align-items: center;
}

.promo-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(13, 148, 136, 0.1);
  color: #1fb981;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.promo-title {
  margin: 0;
  font-size: clamp(2rem, 3.2vw, 3.35rem);
  line-height: 1.06;
  letter-spacing: -0.05em;
  color: #0f172a;
}

.promo-text {
  margin: 18px 0 0;
  max-width: 36ch;
  font-size: 16px;
  line-height: 1.65;
  color: #475569;
}

.promo-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 24px;
}

.promo-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 48px;
  padding: 0 18px;
  border-radius: 999px;
  font-weight: 700;
  text-decoration: none;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background-color 0.15s ease;
}

.promo-btn:hover {
  transform: translateY(-1px);
}

.promo-btn--primary {
  background: #1fb981;
  color: #fff;
  box-shadow: 0 12px 30px rgba(15, 118, 110, 0.25);
}

.promo-btn--primary:hover {
  background: #0e6b64;
}

.promo-btn--ghost {
  border: 1px solid rgba(15, 23, 42, 0.12);
  background: rgba(255, 255, 255, 0.72);
  color: #0f172a;
  backdrop-filter: blur(8px);
}

.promo-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 22px;
}

.promo-stats span {
  padding: 7px 11px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
  color: #334155;
  font-size: 12px;
  font-weight: 600;
  border: 1px solid rgba(148, 163, 184, 0.22);
}

/* Empuja hacia abajo el bloque de cabecera (Orden / Mostrando)
   Usamos la altura calculada de la category nav para evitar solapamientos.
  Añadimos 10px extra de espacio visual. */
.top {
  margin-top: 10px;
  margin-bottom: 12px;
}


@media (min-width: 900px) {
  .top { margin-top: 14px; }
}

@media (max-width: 900px) {
  .promo-hero {
    min-height: 320px;
    width: 100vw;
    margin: 0 calc(50% - 50vw) 12px;
    background-image:
      linear-gradient(180deg, rgba(255, 255, 255, 0.94) 0%, rgba(255, 255, 255, 0.9) 54%, rgba(255, 255, 255, 0.52) 100%),
      var(--promo-hero-image);
    background-position: center, center;
    background-repeat: no-repeat, no-repeat;
    background-size: cover, cover;
  }

  .promo-hero__content {
    max-width: 100%;
    padding: 28px 0;
  }

  .promo-hero__inner {
    min-height: 320px;
  }
}

@media (max-width: 640px) {
  .promo-hero {
    min-height: auto;
    width: 100vw;
  }

  .promo-hero__inner {
    min-height: auto;
  }

  .promo-title {
    font-size: 1.8rem;
  }

  .promo-text {
    font-size: 15px;
  }

  .promo-actions {
    gap: 10px;
  }

  .promo-btn {
    width: 100%;
  }
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

.explore-layout.no-sidebar {
  grid-template-columns: 1fr;
}

@media (max-width: 1200px) {
  .explore-layout {
    grid-template-columns: 1fr;
  }

  .explore-head {
    align-items: start;
  }
}

@media (max-width: 720px) {
  .explore-head {
    flex-direction: column;
  }

  .explore-badge {
    width: fit-content;
  }
}


.grid img {
  display: block;
  width: 100%;
  height: auto;
  object-fit: cover;
}


::v-deep(.rm-catnav) {
  max-width: 100%;
  box-sizing: border-box;
  padding-left: 12px;
  padding-right: 12px;
}
::v-deep(.rm-catnav__scroller) {
  justify-content: center;
}
</style>
