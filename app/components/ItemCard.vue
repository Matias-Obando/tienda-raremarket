<template>
  <NuxtLink :to="`/item/${item.id}`" class="card">
    <div class="imgWrap">
      <img :src="item.imagen" :alt="item.titulo" loading="lazy" />
    </div>

    <div class="body">
      <div class="price">{{ item.precioEur.toFixed(2) }} €</div>
      <div class="title">{{ item.titulo }}</div>
      <div class="meta">{{ item.marca }} · {{ item.talla }} · {{ item.estado }}</div>
    </div>
  </NuxtLink>
</template>

<script setup lang="ts">
import type { Item } from '../mock/items'

defineProps<{
  item: Item
}>()
</script>

<style scoped>
.card {
  display: block;
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  background: #fff;
}
.imgWrap {
  aspect-ratio: 1 / 1;
  width: 100%;
  background: #f6f6f6;
}
.imgWrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.body {
  padding: 10px;
}
.price {
  font-weight: 700;
}
.title {
  margin-top: 4px;
  font-size: 14px;
  line-height: 1.2;
}
.meta {
  margin-top: 6px;
  font-size: 12px;
  color: #666;
}
</style>