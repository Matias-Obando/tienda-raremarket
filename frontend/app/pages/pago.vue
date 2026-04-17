<template>
  <div class="rm-container page">
    <a class="back" href="#" @click.prevent="goBack">← Volver</a>
    <h1>Pago del producto</h1>
    <div v-if="loading">Cargando producto...</div>
    <div v-else-if="!item">No se encontró el producto.</div>
    <div v-else>
      <div class="product-summary">
        <img :src="item.imagen" :alt="item.titulo" class="img" />
        <div>
          <h2>{{ item.titulo }}</h2>
          <div class="price">{{ item.precioEur }} €</div>
          <div class="desc">{{ item.descripcion }}</div>
        </div>
      </div>
      <hr />
      <PaymentForm :amount="item.precioEur" :item-id="item.id" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PaymentForm from '~/components/PaymentForm.vue'
import { useItemsStore } from '~/stores/useItemsStore'

const route = useRoute()
const router = useRouter()
const store = useItemsStore()

const itemId = route.query.itemId as string
const item = ref<any>(null)
const loading = ref(false)

async function loadItem() {
  loading.value = true
  try {
    item.value = await store.fetchById(itemId)
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadItem()
})
</script>

<style scoped>
.page { padding: 18px 12px 60px; }
.product-summary { display: flex; gap: 18px; align-items: flex-start; margin-bottom: 18px; }
.img { width: 120px; height: 120px; object-fit: cover; border-radius: 8px; }
.price { font-weight: 900; font-size: 22px; color: var(--rm-text); margin-bottom: 6px; }
.desc { color: #374151; font-size: 15px; }
</style>
