<template>
  <div class="rm-page">
    <div class="rm-container page">
    <div v-if="loading" class="notfound">Cargando producto...</div>

    <div v-else-if="!item" class="notfound">No se encontró el producto.</div>

    <div v-else class="product-grid">
      <div class="leftCol product-panel product-panel--media">
        <div class="media">
          <span
            class="badge"
            :class="{
              'badge-new': item.estado === 'Nuevo',
              'badge-like-new': item.estado === 'Como nuevo',
              'badge-used': item.estado === 'Usado'
            }"
            aria-hidden="true"
          >{{ item.estado }}</span>

          <button
            class="fav-btn"
            @click.prevent="toggleFavorite"
            :aria-pressed="isFav"
            :title="isFav ? 'Quitar de favoritos' : 'Añadir a favoritos'"
          >
            <svg v-if="isFav" class="icon fav-on" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 6 4 4 6.5 4c1.74 0 3.41.81 4.5 2.09C12.09 4.81 13.76 4 15.5 4 18 4 20 6 20 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            <svg v-else class="icon fav-off" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" aria-hidden="true">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
          </button>

          <img :src="currentImage" :alt="item.titulo" class="img" />
        </div>

        <div class="thumbs" role="list" aria-label="Miniaturas">
          <button
            v-for="(t, i) in thumbs"
            :key="i"
            class="thumb"
            :class="{ active: t === currentImage }"
            @click="selectImage(t)"
            @keyup.enter.space.prevent="selectImage(t)"
            :aria-pressed="t === currentImage"
            role="listitem"
            type="button"
            :title="`Ver imagen ${i + 1}`"
          >
            <img :src="t" :alt="`Miniatura ${i + 1} de ${item.titulo}`" />
          </button>
        </div>
      </div>

      <div class="rightCol product-panel product-panel--info">
        <h1 class="title">{{ item.titulo }}</h1>

        <div class="priceWrap">
          <div class="price">{{ item.precioEur }} €</div>
        </div>

        <div class="chips-real">
          <span class="chip-real">{{ item.categoria }}</span>
          <span class="chip-real">{{ item.marca }}</span>
          <span class="chip-real">Talla {{ item.talla }}</span>
          <span class="chip-real">{{ item.estado }}</span>
        </div>

        <p class="desc">{{ item.descripcion }}</p>

        <div class="actions">
          <button class="rm-btn rm-btn--primary" @click="comprarMock">Comprar</button>
          <button class="rm-btn rm-btn--secondary" @click="openContact">Enviar mensaje</button>
        </div>

        <div class="meta small">Publicado {{ item.creadoHace }}</div>

        <div class="seller-card" role="group" aria-label="Vendedor del articulo">
          <div class="seller-main">
            <div class="seller-avatar" aria-hidden="true">
              <img v-if="sellerAvatar" :src="sellerAvatar" :alt="`Avatar de ${sellerDisplayName}`" class="seller-avatar__img" />
              <span v-else>{{ sellerInitial }}</span>
            </div>

            <div class="seller-copy">
              <p class="seller-name">{{ sellerDisplayName }}</p>
              <p class="seller-reputation">
                <span class="seller-stars" aria-hidden="true">★★★★★</span>
                <span>{{ sellerReviews }}</span>
              </p>
            </div>
          </div>

          <button
            class="seller-open"
            type="button"
            :disabled="loadingSeller || !item?.sellerId"
            :title="`Ver perfil de ${sellerDisplayName}`"
            @click="goToSellerProfile"
          >
            <svg class="seller-open__icon" viewBox="0 0 20 20" fill="none" aria-hidden="true">
              <path d="M7.5 4.5L12.5 10L7.5 15.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>
        </div>
      </div>
    </div>

    <hr class="separator" />

    <section v-if="relatedItems.length" class="related">
      <h2 class="relatedTitle">{{ relatedTitle }}</h2>
      <div class="relatedGrid">
        <ItemCard v-for="r in relatedItems" :key="r.id" :item="r" />
      </div>
    </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import ItemCard from '~/components/ItemCard.vue'
import type { Item } from '~/stores/items'
import { useItemsStore } from '~/stores/useItemsStore'

const route = useRoute()
const router = useRouter()
const { sessionUser, loadSessionUser } = useSessionUser()
const store = useItemsStore()

const id = computed(() => String(route.params.id))
const item = ref<Item | null>(null)
const loading = ref(false)

type SellerPreview = {
  id: string
  username: string
  avatarUrl?: string
}

const seller = ref<SellerPreview | null>(null)
const loadingSeller = ref(false)

const sellerDisplayName = computed(() => seller.value?.username?.trim() || 'Vendedor Closely')
const sellerAvatar = computed(() => seller.value?.avatarUrl || '')
const sellerInitial = computed(() => sellerDisplayName.value.charAt(0).toUpperCase())
const sellerReviews = computed(() => {
  const seedBase = item.value?.sellerId ?? item.value?.id ?? '0'
  const seed = Array.from(seedBase).reduce((acc, ch) => acc + ch.charCodeAt(0), 0)
  return 35 + (seed % 170)
})

async function loadSellerInfo(sellerId?: string) {
  seller.value = null
  if (!sellerId) return

  loadingSeller.value = true
  try {
    const config = useRuntimeConfig()
    const users = await $fetch<SellerPreview[]>(`${config.public.API_BASE_URL}/users`)
    seller.value = users.find((u) => String(u.id) === String(sellerId)) ?? null
  } catch (e) {
    console.error('No se pudo cargar el vendedor del articulo:', e)
  } finally {
    loadingSeller.value = false
  }
}

async function loadItem() {
  loading.value = true
  try {
    item.value = await store.fetchById(id.value)

    if (store.items.length === 0) {
      await store.fetchAll()
    }

    await loadSellerInfo(item.value?.sellerId)
  } finally {
    loading.value = false
  }
}

const thumbs = computed<string[]>(() => {
  if (!item.value) return []
  if (Array.isArray((item.value as any).images) && (item.value as any).images.length > 0) {
    return (item.value as any).images
  }
  const base = item.value.imagen
  return [base, base] 
})

const currentImage = ref<string>('')

watch(item, (n) => {
  currentImage.value = thumbs.value[0] ?? ''
}, { immediate: true })

watch(id, async () => {
  await loadItem()
})

function selectImage(src: string) {
  currentImage.value = src
}

const hasSameCategoryRelated = computed(() => {
  if (!item.value) return false
  return store.items.some((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id))
})

const relatedTitle = computed(() => {
  if (!item.value) return 'Te puede interesar'
  return hasSameCategoryRelated.value ? `Más de ${item.value.categoria}` : 'Te puede interesar'
})

const relatedItems = computed(() => {
  if (!item.value) return []
  const sameCategory = store.items.filter((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id)).slice(0, 4)
  if (sameCategory.length) return sameCategory
  return store.items.filter((x) => String(x.id) !== String(item.value!.id)).slice(0, 4)
})

function goBack() {
  const from = route.query.from
  if (typeof from === 'string' && from.startsWith('/')) {
    navigateTo(from)
    return
  }
  if (window.history.length > 1) { router.back(); return }
  navigateTo('/explorar')
}

function comprarMock() {
  if (!item.value) return
  alert(`Compra simulada: ${item.value.titulo}`)
}
function openContact() {
  loadSessionUser()
  if (!item.value) return

  if (!sessionUser.value) {
    navigateTo({
      path: '/auth',
      query: {
        mode: 'login',
        redirect: `/chat?itemId=${encodeURIComponent(item.value.id)}&itemTitle=${encodeURIComponent(item.value.titulo)}`
      }
    })
    return
  }

  navigateTo({
    path: '/chat',
    query: {
      itemId: item.value.id,
      itemTitle: item.value.titulo
    }
  })
}

function goToSellerProfile() {
  if (!item.value?.sellerId) return
  navigateTo(`/perfil/${encodeURIComponent(item.value.sellerId)}`)
}

const LS_KEY = 'closely:favorites'
const isFav = ref(false)
function readFavorites(): string[] {
  try { const raw = localStorage.getItem(LS_KEY); return raw ? JSON.parse(raw) : [] } catch { return [] }
}
function writeFavorites(arr: string[]) {
  try {
    localStorage.setItem(LS_KEY, JSON.stringify(arr))
    window.dispatchEvent(new CustomEvent('closely:favs:updated', { detail: arr }))
  } catch {}
}
function toggleFavorite() {
  if (!item.value) return
  const favs = readFavorites()
  const idx = favs.indexOf(item.value.id)
  if (idx >= 0) { favs.splice(idx, 1); isFav.value = false } else { favs.push(item.value.id); isFav.value = true }
  writeFavorites(favs)
}
function syncFavs() {
  if (!item.value) { isFav.value = false; return }
  const favs = readFavorites()
  isFav.value = favs.includes(item.value.id)
}
onMounted(() => { syncFavs(); window.addEventListener('storage', syncFavs); window.addEventListener('closely:favs:updated', syncFavs) })
onBeforeUnmount(() => { window.removeEventListener('storage', syncFavs); window.removeEventListener('closely:favs:updated', syncFavs) })

onMounted(async () => {
  await loadItem()
})
</script>

<style scoped>

.rm-page {
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

.page { padding: 20px 0 60px; }

.product-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 18px;
}
@media (min-width: 900px) {
  .product-grid { grid-template-columns: 56% 44%; align-items: start; }
}

.product-panel {
  border: 1px solid #dbe4ee;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbfe 100%);
  box-shadow: 0 16px 36px rgba(15, 23, 42, 0.08);
}

.leftCol {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 12px;
}

.media {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: var(--rm-soft);
  height: 440px;
}
@media (min-width: 1200px) { .media { height: 470px; } }

.img { width: 100%; height: 100%; object-fit: cover; display: block; }

.badge {
  position: absolute;
  left: 16px;
  top: 16px;
  z-index: 20;
  font-size: 13px;
  font-weight: 700;
  color: white;
  padding: 8px 12px;
  border-radius: 999px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.12);
}
.badge-new { background: #16a34a; }
.badge-like-new { background: #4f46e5; }
.badge-used { background: #374151; }

.fav-btn {
  position: absolute;
  right: 18px;
  bottom: 18px;
  z-index: 30;
  width: 46px;
  height: 46px;
  border-radius: 999px;
  background: rgba(255,255,255,0.95);
  border: 1px solid rgba(0,0,0,0.04);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
  transition: transform .12s ease;
}
.fav-btn:hover { transform: translateY(-3px); }
.icon { width: 18px; height: 18px; display: block; }
.fav-on { color: #ef4444; }
.fav-off { color: #6b7280; }

.thumbs {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}
.thumb {
  flex: 1 1 0;
  height: 96px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--rm-border);
  padding: 0;
  background: transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }

.thumb.active {
  outline: 3px solid rgba(16,185,129,0.18);
  box-shadow: 0 8px 20px rgba(16,185,129,0.06);
  transform: translateY(-2px);
}

.rightCol {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.title {
  margin: 8px 0 12px;
  font-size: clamp(2rem, 3.2vw, 3rem);
  font-weight: 800;
  line-height: 1.03;
  letter-spacing: -0.03em;
  color: var(--rm-text);
}

@media (max-width: 1200px) { .title { font-size: clamp(1.9rem, 3vw, 2.6rem); } }
@media (max-width: 960px) { .title { font-size: 1.9rem; } }

.priceWrap { margin-bottom: 10px; }
.price { font-weight: 800; font-size: clamp(1.9rem, 2.2vw, 2.4rem); color: #0f172a; }
.chips-real { display:flex; gap:8px; margin-bottom: 16px; flex-wrap:wrap; }
.chip-real {
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid var(--rm-border);
  background: #f8fafc;
  color: var(--rm-text);
  font-size: 13px;
  font-weight: 600;
}

.desc {
  margin: 0 0 16px;
  color: #334155;
  font-size: 1rem;
  line-height: 1.55;
  max-width: 52ch;
}

.actions { display:flex; gap:10px; align-items:center; margin-bottom:12px; flex-wrap: wrap; }

.rm-btn {
  min-height: 44px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  font-size: 0.92rem;
  font-weight: 700;
  cursor: pointer;
}

.rm-btn--primary {
  background: #1fb981;
  color: #fff;
  box-shadow: 0 10px 24px rgba(15, 118, 110, 0.24);
}

.rm-btn--secondary {
  background: #ffffff;
  color: #1fb981;
  border-color: #99f6e4;
}

.small { font-size: 12px; color: #9aa0a6; margin-top:8px; }

.seller-card {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  border: 1px solid #dce3eb;
  border-radius: 14px;
  background: #ffffff;
  padding: 12px 14px;
}

.seller-main {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.seller-avatar {
  width: 52px;
  height: 52px;
  border-radius: 999px;
  background: #2f6f22;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1.55rem;
  font-weight: 700;
  overflow: hidden;
  flex-shrink: 0;
}

.seller-avatar__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-copy {
  min-width: 0;
}

.seller-name {
  margin: 0;
  font-size: 1.03rem;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.2;
}

.seller-reputation {
  margin: 4px 0 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #5b6472;
  font-size: 1.02rem;
}

.seller-stars {
  color: #f59e0b;
  letter-spacing: 0.02em;
  font-size: 0.95rem;
  line-height: 1;
}

.seller-open {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  border: 1px solid #d2dae4;
  background: #ffffff;
  color: #5b6472;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  flex-shrink: 0;
}

.seller-open__icon {
  width: 16px;
  height: 16px;
  display: block;
}

.seller-open:hover {
  border-color: #1fb981;
  color: #1fb981;
}

.seller-open:disabled {
  opacity: 0.6;
  cursor: default;
}

.separator { border: 0; border-top: 1px solid var(--rm-border); margin: 28px 0; }
.related { margin-top: 18px; }
.relatedTitle {
  font-weight: 800;
  font-size: 1.3rem;
  margin: 6px 0 12px;
  color: #0f172a;
}
.relatedGrid { display:grid; grid-template-columns: repeat(2,1fr); gap: 12px; }

@media (min-width: 900px) { .relatedGrid { grid-template-columns: repeat(4,1fr); } }

.notfound { padding: 16px; border: 1px dashed var(--rm-border); border-radius:8px; }

@media (max-width: 899px) {
  .media { height: 320px; }
  .thumb { height: 84px; }
  .rm-container {
    padding-left: 14px;
    padding-right: 14px;
  }
  .rightCol {
    padding: 16px;
  }
  .seller-card {
    margin-top: 14px;
  }
}
</style>

