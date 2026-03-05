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
defineProps<{ item: Item }>()
</script>

<style scoped>
.card{
  display:block;
  background:#fff;
  border:1px solid #eee;
  border-radius:12px;
  overflow:hidden;
  text-decoration:none;
  color:inherit;
  min-width:0;
  transition: transform .08s ease, box-shadow .08s ease;
}
.card:hover{
  transform: translateY(-1px);
  box-shadow: 0 6px 18px rgba(0,0,0,.06);
}

.imgWrap{
  width:100%;
  aspect-ratio: 1 / 1;
  background:#f6f6f6;
}
.imgWrap img{
  width:100%;
  height:100%;
  object-fit:cover;
  display:block;
}

.body{ padding:10px; min-width:0; }
.price{ font-weight:900; font-size:14px; }
.title{
  margin-top:4px;
  font-size:13px;
  line-height:1.2;
  overflow:hidden;
  text-overflow:ellipsis;
  white-space:nowrap;
}
.meta{
  margin-top:6px;
  font-size:12px;
  color:#777;
  overflow:hidden;
  text-overflow:ellipsis;
  white-space:nowrap;
}
</style>