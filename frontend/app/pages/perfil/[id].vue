<template>
  <div class="seller-profile-page">
    <div class="seller-profile-shell">
      <a class="back-link" href="#" @click.prevent="goBack">← Volver al producto</a>

      <section class="seller-hero card">
        <div class="seller-avatar" aria-hidden="true">
          <img v-if="sellerAvatar" :src="sellerAvatar" :alt="`Avatar de ${sellerName}`" class="seller-avatar__img" />
          <span v-else>{{ sellerInitial }}</span>
        </div>

        <div class="seller-meta">
          <p class="seller-kicker">Perfil del vendedor</p>
          <h1 class="seller-name">{{ sellerName }}</h1>
          <p class="seller-stats">{{ publishedItems.length }} artículos publicados</p>
        </div>
      </section>

      <section class="card seller-items">
        <div class="seller-items__head">
          <h2>Artículos de {{ sellerName }}</h2>
        </div>

        <p v-if="loading" class="state">Cargando artículos...</p>
        <p v-else-if="!publishedItems.length" class="state">Este vendedor todavía no tiene artículos publicados.</p>

        <div v-else class="grid">
          <ItemCard v-for="it in publishedItems" :key="it.id" :item="it" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import ItemCard from '~/components/ItemCard.vue'
import type { Item } from '~/stores/items'

type SellerPreview = {
  id: string
  username: string
  avatarUrl?: string
}

const route = useRoute()
const router = useRouter()
const store = useItemsStore()
const seller = ref<SellerPreview | null>(null)
const loading = ref(false)

const sellerId = computed(() => String(route.params.id || ''))
const sellerName = computed(() => seller.value?.username?.trim() || 'Vendedor Closely')
const sellerAvatar = computed(() => seller.value?.avatarUrl || '')
const sellerInitial = computed(() => sellerName.value.charAt(0).toUpperCase())

const publishedItems = computed<Item[]>(() => {
  const id = sellerId.value
  if (!id) return []
  return store.items.filter((item) => String(item.sellerId ?? '') === id)
})

async function loadSeller() {
  if (!sellerId.value) return

  loading.value = true
  try {
    if (!store.items.length) {
      await store.fetchAll()
    }

    const config = useRuntimeConfig()
    const users = await $fetch<SellerPreview[]>(`${config.public.API_BASE_URL}/users`)
    seller.value = users.find((user) => String(user.id) === sellerId.value) ?? null
  } finally {
    loading.value = false
  }
}

function goBack() {
  const from = route.query.from
  if (typeof from === 'string' && from.startsWith('/')) {
    navigateTo(from)
    return
  }
  if (window.history.length > 1) {
    router.back()
    return
  }
  navigateTo('/explorar')
}

onMounted(async () => {
  await loadSeller()
})
</script>

<style scoped>
.seller-profile-page {
  min-height: 100vh;
  background: var(--rm-page-bg);
  padding: 24px 16px 56px;
}

.seller-profile-shell {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 16px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #334155;
  text-decoration: none;
  font-weight: 600;
}

.card {
  border: 1px solid #dbe4ee;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.07);
}

.seller-hero {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
}

.seller-avatar {
  width: 64px;
  height: 64px;
  border-radius: 999px;
  background: #2f6f22;
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  font-weight: 700;
  overflow: hidden;
}

.seller-avatar__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #1fb981;
  font-size: 12px;
  font-weight: 700;
}

.seller-name {
  margin: 4px 0 0;
  font-size: clamp(1.4rem, 2.5vw, 2rem);
  line-height: 1.1;
  color: #0f172a;
}

.seller-stats {
  margin: 6px 0 0;
  color: #64748b;
}

.seller-items {
  padding: 18px;
}

.seller-items__head h2 {
  margin: 0 0 10px;
  font-size: 1.15rem;
  color: #0f172a;
}

.state {
  margin: 8px 0;
  color: #64748b;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

@media (min-width: 900px) {
  .grid {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .seller-profile-page {
    padding-left: 12px;
    padding-right: 12px;
  }
  .seller-hero {
    padding: 14px;
  }
  .seller-avatar {
    width: 56px;
    height: 56px;
    font-size: 1.5rem;
  }
}
</style>

