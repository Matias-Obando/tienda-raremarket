<template>
  <article class="sell-card" :class="{ 'is-small': small }">
    <div class="sell-card__media">
      <span v-if="item.estado" class="sell-card__badge">{{ item.estado }}</span>
      <div class="sell-card__imgwrap">
        <img :src="mainImgSrc" :alt="item.titulo || 'Imagen del producto'" loading="lazy" />
      </div>
      <div v-if="imagenes && imagenes.length > 1" class="sell-card__thumbs">
        <img v-for="(img, i) in imagenes" :key="i" :src="img" class="thumb" :class="{active: i === mainImgIndex}" @click="mainImgIndex = i" />
      </div>
    </div>
    <div class="sell-card__body">
      <div class="sell-card__row">
        <h3 class="sell-card__title">{{ item.titulo || 'Título del producto' }}</h3>
        <div class="sell-card__price">{{ item.precioEur ? item.precioEur + ' €' : '—' }}</div>
      </div>
      <div class="sell-card__meta">
        <span v-if="item.marca" class="meta-pill meta-pill--brand">{{ item.marca }}</span>
        <span v-if="item.talla" class="meta-pill meta-pill--size">Talla {{ item.talla }}</span>
      </div>
      <div class="sell-card__meta sell-card__meta--category">
        <span v-if="item.categoria" class="meta-pill meta-pill--category">{{ item.categoria }}</span>
        <span v-if="item.subcategoria" class="meta-pill meta-pill--subcategory">{{ item.subcategoria }}</span>
      </div>
      <p class="sell-card__desc">
        {{ item.descripcion ? truncate(item.descripcion, 140) : 'Descripción breve del producto...' }}
      </p>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps<{ item?: Record<string, any>, small?: boolean, imagenes?: string[] }>()
const item = props.item ?? {}
const imagenes = props.imagenes ?? (item.imagenes || [])
const mainImgIndex = ref(0)

const svg = `<svg xmlns='http://www.w3.org/2000/svg' width='800' height='600'><rect width='100%' height='100%' fill='%23f3f4f6'/><text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle' fill='%236b7280' font-family='Arial, Helvetica, sans-serif' font-size='28'>Imagen del producto</text></svg>`
const DEFAULT_PLACEHOLDER = `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`

const mainImgSrc = computed(() => {
  if (imagenes && imagenes.length > 0) return imagenes[mainImgIndex.value] || DEFAULT_PLACEHOLDER
  if (item && item.imagen) return String(item.imagen)
  return DEFAULT_PLACEHOLDER
})

watch(imagenes, () => { mainImgIndex.value = 0 })

function truncate(s: string, n = 140) {
  if (!s) return ''
  return s.length <= n ? s : s.slice(0, n - 1) + '…'
}
</script>

<style scoped>
:root {
  --card-radius: 12px;
  --media-height: 320px;
  --card-bg: #fff;
  --media-bg: #f3f4f6;
  --primary: var(--rm-primary, #10b981);
  --text: var(--rm-text, #111827);
  --muted: var(--rm-muted, #6b7280);
  --border: var(--rm-border, #e6e6e8);
}

.sell-card {
  width: 100%;
  max-width: 380px;
  background: var(--card-bg);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(16,24,40,0.06);
  overflow: hidden;
  border: 1px solid rgba(16,24,40,0.03);
  display: flex;
  flex-direction: column;
}


.sell-card__media {
  position: relative;
  height: var(--media-height);
  background: var(--media-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-direction: column;
}

.sell-card__imgwrap { width:100%; height:100%; }

.sell-card__imgwrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.sell-card__thumbs {
  display: flex;
  gap: 8px;
  margin: 8px 0 0 0;
  justify-content: center;
}
.sell-card__thumbs .thumb {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e6e6e8;
  background: #f3f4f6;
  cursor: pointer;
  opacity: 0.7;
  transition: border-color 0.15s, opacity 0.15s;
}
.sell-card__thumbs .thumb.active {
  border-color: #10b981;
  opacity: 1;
}

.sell-card__badge {
  position: absolute;
  left: 12px;
  top: 12px;
  background: rgba(16,24,40,0.9);
  color: #fff;
  padding: 6px 10px;
  border-radius: 999px;
  font-weight: 700;
  font-size: 13px;
  box-shadow: 0 6px 18px rgba(16,24,40,0.08);
}

.sell-card__body {
  padding: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sell-card__row {
  display:flex;
  align-items:flex-start;
  justify-content:space-between;
  gap:12px;
}

.sell-card__title {
  font-size: 16px;
  font-weight: 800;
  color: var(--text);
  margin: 0;
  line-height: 1.15;
}

.sell-card__price {
  font-weight: 900;
  font-size: 16px;
  color: var(--text);
}

.sell-card__meta {
  color: var(--muted);
  font-size: 13px;
  display:flex;
  gap:8px;
  align-items:center;
  flex-wrap:wrap;
}
.sell-card__meta--category {
  margin-top: -2px;
}

.meta-pill {
  display: inline-flex;
  align-items: center;
  min-height: 24px;
  padding: 0;
  border-radius: 0;
  background: transparent;
  color: #334155;
  border: 0;
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}
.meta-pill--brand,
.meta-pill--size,
.meta-pill--category,
.meta-pill--subcategory {
  color: #475569;
}

.sell-card__desc {
  margin: 0;
  color: var(--muted);
  font-size: 13px;
  line-height: 1.4;
  max-height: 3.6em;
  overflow: hidden;
}


@media (max-width: 880px) {
  :root { --media-height: 200px; }
  .sell-card { max-width: 100%; }
}
</style>
