<template>
  <NuxtLink :to="itemHref" class="card">
    <div class="media">
      <img :src="item.imagen" :alt="item.titulo" loading="lazy" />
    </div>

    <div class="body">
      <div class="price">{{ item.precioEur.toFixed(2) }} €</div>
      <div class="title">{{ item.titulo }}</div>
      <div class="meta">
        {{ item.marca }} · {{ item.talla }} · {{ item.estado }}
      </div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import type { Item } from '~/mock/items'

const props = defineProps<{
  item: Item
}>()

const route = useRoute()

const itemHref = computed(() => {
  const from = encodeURIComponent(route.fullPath)
  return `/item/${props.item.id}?from=${from}`
})
</script>

<style scoped>
.card {
  display: block;
  background: #fff;
  border: 1px solid var(--rm-border);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: var(--rm-shadow);
}

.media {
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

.body {
  padding: 12px;
}

.price {
  font-weight: 900;
  margin-bottom: 6px;
}

.title {
  font-weight: 700;
  margin-bottom: 6px;
  line-height: 1.2;
}

.meta {
  color: var(--rm-muted);
  font-size: 13px;
}
</style>