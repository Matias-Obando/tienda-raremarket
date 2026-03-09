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
  /* en pantallas grandes usamos más ancho, pero sin volverse infinito */
  max-width: 1320px;
  margin: 0 auto;
  padding: 18px 16px;
}

.grid{
  display:grid;
  gap:16px;

  /* magia: mete tantas columnas como quepan */
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

/* un poquito más de aire en desktop grande */
@media (min-width: 1024px){
  .container{ padding: 24px 20px; }
  .grid{ gap:18px; }
}

/* si la pantalla es enorme, aprovecha más ancho */
@media (min-width: 1440px){
  .container{ max-width: 1500px; }
  .grid{ gap:20px; }
}
</style>