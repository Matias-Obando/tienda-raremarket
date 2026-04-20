<template>
  <div class="favorites-page">
    <section class="favorites-shell">
      <article class="favorites-card favorites-card--header">
        <div>
          <p class="eyebrow">Favoritos</p>
          <h1>Productos guardados</h1>
          <p class="section-note">Los artículos que marcaste con corazón aparecen aquí.</p>
        </div>

        <NuxtLink to="/explorar" class="favorites-link favorites-link--soft">Seguir explorando</NuxtLink>
      </article>

      <article class="favorites-card">
        <div v-if="loading" class="empty">Cargando favoritos...</div>
        <div v-else-if="!favoriteItems.length" class="empty empty--wide">
          Aún no tienes favoritos. Guarda artículos desde explorar para verlos aquí.
        </div>

        <div v-else class="favorites-grid">
          <ItemCard
            v-for="item in favoriteItems"
            :key="item.id"
            :item="item"
            :show-badge="true"
            :show-fav="true"
          />
        </div>
      </article>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Item } from '~/stores/items'

const store = useItemsStore()
const loading = ref(false)
const favoriteItems = ref<Item[]>([])
const LS_FAVORITES_KEY = 'closely:favorites'

function readFavoriteIds() {
  if (!process.client) {
    return [] as string[]
  }

  try {
    const raw = localStorage.getItem(LS_FAVORITES_KEY)
    const parsed = raw ? JSON.parse(raw) : []
    return Array.isArray(parsed) ? parsed.map((id) => String(id)) : []
  } catch {
    return []
  }
}

async function loadFavorites() {
  loading.value = true
  try {
    if (!store.items.length) {
      await store.fetchAll()
    }

    const ids = readFavoriteIds()
    favoriteItems.value = ids
      .map((id) => store.items.find((item) => item.id === id))
      .filter((item): item is Item => Boolean(item))
  } finally {
    loading.value = false
  }
}

function syncFavorites() {
  loadFavorites()
}

onMounted(async () => {
  await loadFavorites()
  window.addEventListener('closely:favs:updated', syncFavorites)
  window.addEventListener('storage', syncFavorites)
})

onBeforeUnmount(() => {
  window.removeEventListener('closely:favs:updated', syncFavorites)
  window.removeEventListener('storage', syncFavorites)
})
</script>

<style scoped>
.favorites-page {
  padding: 24px 16px 56px;
  background:
    radial-gradient(circle at 15% 8%, rgba(15, 118, 110, 0.1), transparent 28%),
    radial-gradient(circle at 88% 2%, rgba(15, 23, 42, 0.08), transparent 24%),
    linear-gradient(180deg, #f8fafc 0%, #eef2f6 100%);
}

.favorites-shell {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 18px;
}

.favorites-card {
  width: 100%;
  background: #fff;
  border: 1px solid #e6ebf1;
  border-radius: 26px;
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.08);
  padding: 30px;
}

.favorites-card--header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0f766e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

h1,
.hero-note {
  margin: 0 0 10px;
}

h1 {
  font-size: clamp(1.85rem, 2.5vw, 2.35rem);
  line-height: 1.08;
  letter-spacing: -0.04em;
}

.hero-note {
  max-width: 56ch;
  color: #475569;
  font-size: 0.94rem;
}

.section-note {
  color: #64748b;
  font-size: 13px;
}

.favorites-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  min-height: 44px;
  padding: 0 16px;
  background: #0f766e;
  color: #fff;
  border: 1px solid transparent;
  text-decoration: none;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.favorites-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(15, 118, 110, 0.2);
}

.favorites-link--soft {
  background: #fff;
  color: #0f766e;
  border-color: #99f6e4;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 16px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.stat-label {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.stat-value {
  margin: 6px 0 0;
  font-size: 27px;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.section-head h2 {
  margin: 0;
  font-size: 22px;
  line-height: 1.12;
  letter-spacing: -0.03em;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.empty {
  border: 1px dashed #cbd5e1;
  border-radius: 14px;
  background: #f8fafc;
  color: #64748b;
  padding: 16px;
}

.empty--wide {
  min-height: 120px;
  display: flex;
  align-items: center;
}

@media (max-width: 960px) {
  .favorites-card--header {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-row,
  .favorites-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .favorites-page {
    padding-left: 10px;
    padding-right: 10px;
    padding-top: 16px;
    padding-bottom: 36px;
  }

  .favorites-card {
    padding: 18px;
    border-radius: 16px;
  }

  h1 {
    font-size: 1.75rem;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .stat-card {
    padding: 14px;
  }

  .stat-value {
    font-size: 24px;
  }

  .stats-row,
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>