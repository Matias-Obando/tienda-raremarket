<template>
  <div class="wrap">
    <header class="top">
      <h1>Explorar</h1>
      <p class="sub">Ropa de segunda mano (mock). Precios en €.</p>
    </header>

    <div class="grid">
      <ItemCard v-for="it in items" :key="it.id" :item="it" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { mockItems } from '../mock/items'

const items = mockItems
</script>

<style scoped>
.wrap {
  max-width: 1100px;
  margin: 0 auto;
  padding: 16px;
}
.top {
  margin-bottom: 12px;
}
.sub {
  margin: 0;
  color: #666;
  font-size: 14px;
}
.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}
@media (min-width: 640px) {
  .grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}
@media (min-width: 960px) {
  .grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}
</style>