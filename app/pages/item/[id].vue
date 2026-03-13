<template>
  <div class="rm-container page">
    <a class="back" href="#" @click.prevent="goBack">← Volver</a>

    <div v-if="!item" class="notfound">No se encontró el producto.</div>

    <div v-else class="product-grid">
      <div class="leftCol">
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

      <div class="rightCol">
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
          <button class="rm-btn rm-btn--primary" @click="openContact">Enviar mensaje</button>
        </div>

        <div class="meta small">Publicado {{ item.creadoHace }}</div>
      </div>
    </div>

    <hr class="separator" />

    <section v-if="relatedItems.length" class="related">
      <h2 class="relatedTitle">Te puede interesar</h2>
      <div class="relatedGrid">
        <ItemCard v-for="r in relatedItems" :key="r.id" :item="r" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import ItemCard from '~/components/ItemCard.vue'
import { mockItems } from '~/stores/items'

const route = useRoute()
const router = useRouter()

const id = computed(() => String(route.params.id))
const item = computed(() => mockItems.find((x) => String(x.id) === id.value) ?? null)

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

function selectImage(src: string) {
  currentImage.value = src
}

const hasSameCategoryRelated = computed(() => {
  if (!item.value) return false
  return mockItems.some((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id))
})

const relatedTitle = computed(() => {
  if (!item.value) return 'Te puede interesar'
  return hasSameCategoryRelated.value ? `Más de ${item.value.categoria}` : 'Te puede interesar'
})

const relatedItems = computed(() => {
  if (!item.value) return []
  const sameCategory = mockItems.filter((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id)).slice(0, 4)
  if (sameCategory.length) return sameCategory
  return mockItems.filter((x) => String(x.id) !== String(item.value!.id)).slice(0, 4)
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
  if (!item.value) return alert(`Compra simulada: ${item.value.titulo}`)
}
const showContact = ref(false)
function openContact() { showContact.value = !showContact.value }

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
</script>

<style scoped>

.page { padding: 18px 12px 60px; }

.product-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 28px;
}
@media (min-width: 900px) {
  .product-grid { grid-template-columns: 62% 38%; align-items: start; }
}

.leftCol { display: flex; flex-direction: column; gap: 14px; }

.media {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: var(--rm-soft);
  height: 520px;
}
@media (min-width: 1200px) { .media { height: 560px; } }

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
  height: 140px;
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
  transform: translateY(-4px);
}

.rightCol { padding-top: 6px; }

.title {
  margin: 0 0 10px;
  font-size: 34px;
  font-weight: 800;
  line-height: 1.06;
  color: var(--rm-text);
}

@media (min-width: 1200px) { .title { font-size: 40px; } }

.priceWrap { margin-bottom: 12px; }
.price { font-weight: 900; font-size: 26px; color: var(--rm-text); }
.chips-real { display:flex; gap:8px; margin-bottom: 14px; flex-wrap:wrap; }
.chip-real {
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid var(--rm-border);
  background: #fff;
  color: var(--rm-text);
  font-size: 13px;
  font-weight: 600;
}

.desc { margin: 0 0 18px; color: #374151; }

.actions { display:flex; gap:14px; align-items:center; margin-bottom:12px; }

.small { font-size: 12px; color: #9aa0a6; margin-top:8px; }
.separator { border: 0; border-top: 1px solid var(--rm-border); margin: 28px 0; }
.related { margin-top: 18px; }
.relatedTitle { font-weight: 700; margin: 6px 0 12px; }
.relatedGrid { display:grid; grid-template-columns: repeat(2,1fr); gap: 12px; }

@media (min-width: 900px) { .relatedGrid { grid-template-columns: repeat(4,1fr); } }

.notfound { padding: 16px; border: 1px dashed var(--rm-border); border-radius:8px; }

@media (max-width: 899px) {
  .media { height: 360px; }
  .thumb { height: 96px; }
}
</style>