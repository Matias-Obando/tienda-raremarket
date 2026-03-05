<template>
  <div class="wrap">
    <NuxtLink to="/explorar" class="back">← Volver</NuxtLink>

    <div v-if="!item" class="notfound">
      No se encontró el producto.
    </div>

    <div v-else class="layout">
      <div class="img">
        <img :src="item.imagen" :alt="item.titulo" />
      </div>

      <div class="info">
        <h1>{{ item.titulo }}</h1>
        <div class="price">{{ item.precioEur.toFixed(2) }} €</div>

        <div class="chips">
          <span class="chip">{{ item.categoria }}</span>
          <span class="chip">{{ item.marca }}</span>
          <span class="chip">Talla {{ item.talla }}</span>
          <span class="chip">{{ item.estado }}</span>
        </div>

        <p class="desc">{{ item.descripcion }}</p>

        <button class="btn" type="button" @click="reservarMock">
          Reservar (mock)
        </button>

        <p class="small">Publicado {{ item.creadoHace }} · Solo envío (por ahora)</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { mockItems } from '../../mock/items'

const route = useRoute()
const id = String(route.params.id)

const item = computed(() => mockItems.find((x) => x.id === id) ?? null)

function reservarMock() {
  alert('Reserva simulada. Luego lo conectamos con Supabase.')
}
</script>

<style scoped>
.wrap {
  max-width: 1100px;
  margin: 0 auto;
  padding: 16px;
}
.back {
  display: inline-block;
  margin-bottom: 12px;
  text-decoration: none;
  color: inherit;
}
.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 16px;
}
@media (min-width: 900px) {
  .layout {
    grid-template-columns: 1fr 1fr;
    align-items: start;
  }
}
.img {
  border: 1px solid #eee;
  border-radius: 12px;
  overflow: hidden;
  background: #f6f6f6;
}
.img img {
  width: 100%;
  height: auto;
  display: block;
}
.price {
  font-weight: 800;
  font-size: 22px;
  margin: 8px 0;
}
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0;
}
.chip {
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid #eee;
  background: #fafafa;
}
.desc {
  color: #333;
}
.btn {
  margin-top: 10px;
  background: #111;
  color: #fff;
  border: 0;
  padding: 10px 14px;
  border-radius: 10px;
  cursor: pointer;
}
.small {
  margin-top: 10px;
  color: #666;
  font-size: 12px;
}
.notfound {
  padding: 20px;
  border: 1px dashed #ccc;
  border-radius: 12px;
}
</style>