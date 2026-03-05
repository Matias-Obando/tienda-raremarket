<template>
  <div class="container">
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
.container{
  /* esto crea el “margen” lateral en TODAS las pantallas */
  max-width: 1100px;
  margin: 0 auto;
  padding: 18px 16px;
}

.top{ margin-bottom: 12px; }
.sub{ margin:0; color:#666; font-size:14px; }

.grid{
  display:grid;
  gap:16px;

  /* por defecto (móvil) 1 columna */
  grid-template-columns: 1fr;
}

/* 2 columnas cuando ya cabe (móvil grande) */
@media (min-width: 520px){
  .grid{ grid-template-columns: repeat(2, minmax(0, 1fr)); }
}

/* 3 columnas */
@media (min-width: 820px){
  .grid{ grid-template-columns: repeat(3, minmax(0, 1fr)); }
}

/* 4 columnas */
@media (min-width: 1100px){
  .grid{ grid-template-columns: repeat(4, minmax(0, 1fr)); }
}
</style>