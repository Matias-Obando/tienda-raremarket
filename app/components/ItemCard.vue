<template>
  <article class="card">
    <NuxtLink :to="`/item/${item.id}`" class="card-link">
      <div class="media">
        <!-- Badge estado (posición absoluta dentro de la media) -->
        <span
          class="badge"
          :class="{
            'badge-new': item.estado === 'Nuevo',
            'badge-like-new': item.estado === 'Como nuevo',
            'badge-used': item.estado === 'Usado'
          }"
          aria-hidden="true"
        >
          {{ item.estado }}
        </span>

        <!-- Botón favorito -->
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

        <img :src="item.imagen" :alt="item.titulo" loading="lazy" />
      </div>

      <div class="body">
        <div class="row">
          <h3 class="title">{{ item.titulo }}</h3>
          <div class="price">{{ item.precioEur }} €</div>
        </div>

        <div class="meta">
          <span class="brand">{{ item.marca }}</span>
          <span class="dot">·</span>
          <span class="size">{{ item.talla }}</span>
          <span class="dot">·</span>
          <span class="cat">{{ item.categoria }}</span>
        </div>

        <div class="created">{{ item.creadoHace }}</div>
      </div>
    </NuxtLink>
  </article>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import type { Item } from '~/mock/items'

const props = defineProps<{ item: Item }>()
const item = props.item

const LS_KEY = 'closely:favorites'
const isFav = ref(false)

function readFavorites(): string[] {
  try {
    const raw = localStorage.getItem(LS_KEY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}
function writeFavorites(arr: string[]) {
  try {
    localStorage.setItem(LS_KEY, JSON.stringify(arr))
    window.dispatchEvent(new CustomEvent('closely:favs:updated', { detail: arr }))
  } catch {}
}
function toggleFavorite() {
  const favs = readFavorites()
  const idx = favs.indexOf(item.id)
  if (idx >= 0) {
    favs.splice(idx, 1)
    isFav.value = false
  } else {
    favs.push(item.id)
    isFav.value = true
  }
  writeFavorites(favs)
}
function syncFavs() {
  const favs = readFavorites()
  isFav.value = favs.includes(item.id)
}

onMounted(() => {
  syncFavs()
  window.addEventListener('closely:favs:updated', syncFavs)
  window.addEventListener('storage', syncFavs)
})
onBeforeUnmount(() => {
  window.removeEventListener('closely:favs:updated', syncFavs)
  window.removeEventListener('storage', syncFavs)
})
</script>

<style scoped>
.card {
  display: block;
  background: #fff;
  border: 1px solid var(--rm-border);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--rm-shadow);
}

/* Link dentro de la card para que todo sea clicable excepto el botón favorito */
.card-link { display: block; color: inherit; text-decoration: none; }

/* Imagen con aspect ratio y posición relativa para badges */
.media {
  position: relative;
  aspect-ratio: 4 / 3;
  background: var(--rm-soft);
  overflow: hidden;
}
.media img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* Badge estado: discreto y dentro de la imagen */
.badge {
  position: absolute;
  left: 10px;
  top: 10px;
  z-index: 20;
  font-size: 12px;
  font-weight: 700;
  color: white;
  padding: 6px 8px;
  border-radius: 999px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.12);
}
.badge-new { background: #16a34a; }       /* verde */
.badge-like-new { background: #4f46e5; }  /* índigo */
.badge-used { background: #374151; }      /* gris oscuro */

/* Botón favorito: pequeño círculo en la esquina superior derecha */
.fav-btn {
  position: absolute;
  right: 8px;
  top: 8px;
  z-index: 25;
  width: 36px;
  height: 36px;
  border-radius: 999px;
  background: rgba(255,255,255,0.9);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  cursor: pointer;
  transition: transform .12s ease, background .12s ease;
}
.fav-btn:hover { transform: scale(1.03); background: #fff; }
.icon { width: 18px; height: 18px; display: block; }
.fav-on { color: #ef4444; }
.fav-off { color: #6b7280; }

/* Body */
.body {
  padding: 12px;
}
.row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}
.title {
  font-weight: 700;
  font-size: 15px;
  margin: 0;
  line-height: 1.15;
}
.price {
  font-weight: 900;
  font-size: 14px;
  color: var(--rm-text);
}
.meta {
  margin-top: 8px;
  color: var(--rm-muted);
  font-size: 13px;
  display: flex;
  gap: 6px;
  align-items: center;
}
.dot { opacity: .6; }
.created {
  margin-top: 8px;
  color: #9ca3af;
  font-size: 12px;
}
</style>