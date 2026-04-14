<template>
  <div class="profile-page">
    <section v-if="sessionReady && sessionUser" class="profile-grid">
      <article class="profile-card profile-card--hero">
        <div class="hero-left">
          <span class="avatar">{{ userInitial }}</span>
          <div>
            <p class="eyebrow">Cuenta</p>
            <h1>Mi perfil</h1>
            <p class="profile-name">{{ sessionUser.username }}</p>
            <p class="profile-email">{{ sessionUser.email }}</p>
          </div>
        </div>

        <div class="hero-actions">
          <NuxtLink to="/chat" class="profile-link">Ir a mensajes</NuxtLink>
          <NuxtLink to="/explorar" class="profile-link profile-link--soft">Explorar artículos</NuxtLink>
          <button type="button" class="profile-link profile-link--danger" @click="signOut">Cerrar sesión</button>
        </div>
      </article>

      <article class="profile-card stats-row">
        <div class="stat-card">
          <p class="stat-label">Favoritos</p>
          <p class="stat-value">{{ favoriteItems.length }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Conversaciones</p>
          <p class="stat-value">{{ conversationsCount }}</p>
        </div>
        <div class="stat-card">
          <p class="stat-label">Sesión</p>
          <p class="stat-value">Activa</p>
        </div>
      </article>

      <article class="profile-card">
        <div class="section-head">
          <h2>Accesos rápidos</h2>
        </div>

        <div class="quick-links">
          <NuxtLink to="/vender" class="quick-link">Publicar artículo</NuxtLink>
          <NuxtLink to="/chat" class="quick-link">Responder mensajes</NuxtLink>
          <NuxtLink to="/explorar" class="quick-link">Buscar gangas</NuxtLink>
        </div>
      </article>

      <article class="profile-card">
        <div class="section-head">
          <h2>Favoritos recientes</h2>
          <span class="section-note">Los últimos que guardaste</span>
        </div>

        <div v-if="loadingFavorites" class="empty">Cargando favoritos...</div>
        <div v-else-if="!favoriteItems.length" class="empty">
          Aún no tienes favoritos. Guarda artículos desde explorar para verlos aquí.
        </div>

        <div v-else class="favorites-grid">
          <NuxtLink
            v-for="item in favoriteItems"
            :key="item.id"
            :to="`/item/${item.id}`"
            class="fav-card"
          >
            <img :src="item.imagen" :alt="item.titulo" class="fav-image" />
            <div class="fav-body">
              <p class="fav-title">{{ item.titulo }}</p>
              <p class="fav-meta">{{ item.marca }} · {{ item.talla }} · {{ item.estado }}</p>
              <p class="fav-price">{{ item.precioEur }} €</p>
            </div>
          </NuxtLink>
        </div>
      </article>
    </section>

    <section v-else-if="sessionReady" class="profile-card profile-card--guest">
      <h1>Mi perfil</h1>
      <p class="profile-email">Todavía no has iniciado sesión.</p>
      <NuxtLink :to="{ path: '/auth', query: { mode: 'login', redirect: '/perfil' } }" class="profile-link">
        Iniciar sesión
      </NuxtLink>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Item } from '~/stores/items'

const { sessionUser, loadSessionUser, clearSessionUser, storageEventName } = useSessionUser()
const store = useItemsStore()
const uiMessages = useUiMessages()
const sessionReady = ref(false)
const loadingFavorites = ref(false)
const conversationsCount = ref(0)
const favoriteItems = ref<Item[]>([])
const LS_FAVORITES_KEY = 'closely:favorites'

const userInitial = computed(() => sessionUser.value?.username?.charAt(0).toUpperCase() || 'U')

function readFavoriteIds() {
  if (!process.client) {
    return [] as string[]
  }

  try {
    const raw = localStorage.getItem(LS_FAVORITES_KEY)
    const parsed = raw ? JSON.parse(raw) : []
    return Array.isArray(parsed) ? parsed.map((id) => String(id)) : []
  } catch {
    return []
  }
}

async function loadConversationsCount() {
  if (!sessionUser.value) {
    conversationsCount.value = 0
    return
  }

  try {
    const config = useRuntimeConfig()
    const list = await $fetch<Array<{ id: string }>>(`${config.public.API_BASE_URL}/chat/conversations`, {
      params: { userId: sessionUser.value.id }
    })
    conversationsCount.value = list.length
  } catch {
    conversationsCount.value = 0
  }
}

async function loadFavoriteItems() {
  loadingFavorites.value = true
  try {
    if (!store.items.length) {
      await store.fetchAll()
    }

    const ids = readFavoriteIds()
    favoriteItems.value = ids
      .map((id) => store.items.find((item) => item.id === id))
      .filter((item): item is Item => Boolean(item))
      .slice(0, 6)
  } finally {
    loadingFavorites.value = false
  }
}

async function refreshProfileData() {
  await Promise.all([
    loadFavoriteItems(),
    loadConversationsCount()
  ])
}

function signOut() {
  clearSessionUser()
  conversationsCount.value = 0
  favoriteItems.value = []
  uiMessages.info('Sesion cerrada correctamente.')
  navigateTo('/')
}

function syncSession() {
  loadSessionUser()
  if (sessionUser.value) {
    refreshProfileData()
  }
}

onMounted(async () => {
  loadSessionUser()
  sessionReady.value = true

  if (sessionUser.value) {
    await refreshProfileData()
  }

  window.addEventListener(storageEventName, syncSession)
  window.addEventListener('storage', syncSession)
  window.addEventListener('closely:favs:updated', loadFavoriteItems)
})

onBeforeUnmount(() => {
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
  window.removeEventListener('closely:favs:updated', loadFavoriteItems)
})
</script>

<style scoped>
.profile-page {
  padding: 18px 16px 48px;
}

.profile-card {
  width: 100%;
  background: #fff;
  border: 1px solid var(--rm-border);
  border-radius: 24px;
  box-shadow: 0 14px 30px rgba(15, 23, 42, 0.06);
  padding: 28px;
}

.profile-grid {
  max-width: 1100px;
  margin: 0 auto;
  display: grid;
  gap: 16px;
}

.profile-card--hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  background: linear-gradient(120deg, #ffffff 0%, #f3fbf8 100%);
}

.hero-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 70px;
  height: 70px;
  border-radius: 999px;
  background: #dff8f2;
  color: #0f766e;
  border: 1px solid #99f6e4;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 800;
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0f766e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

.profile-card h1,
.profile-name,
.profile-email {
  margin: 0 0 12px;
}

.profile-name {
  font-size: 26px;
  font-weight: 700;
}

.profile-email {
  color: #64748b;
}

.profile-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  padding: 10px 16px;
  background: #0f766e;
  color: #fff;
  border: 1px solid transparent;
  text-decoration: none;
  font-weight: 700;
  cursor: pointer;
}

.profile-link--soft {
  background: #fff;
  color: #0f766e;
  border-color: #99f6e4;
}

.profile-link--danger {
  background: #fff1f2;
  color: #be123c;
  border-color: #fecdd3;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.stat-card {
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  padding: 14px;
  background: #f8fafc;
}

.stat-label {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.stat-value {
  margin: 6px 0 0;
  font-size: 24px;
  font-weight: 800;
}

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 12px;
}

.section-head h2 {
  margin: 0;
  font-size: 20px;
}

.section-note {
  color: #64748b;
  font-size: 13px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.quick-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  border-radius: 12px;
  border: 1px solid #cbd5e1;
  background: #fff;
  color: #0f172a;
  font-weight: 600;
}

.quick-link:hover {
  border-color: #14b8a6;
  background: #f0fdfa;
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.fav-card {
  display: block;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  overflow: hidden;
  background: #fff;
}

.fav-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.fav-body {
  padding: 10px;
}

.fav-title,
.fav-meta,
.fav-price {
  margin: 0;
}

.fav-title {
  font-weight: 700;
  line-height: 1.2;
}

.fav-meta {
  margin-top: 6px;
  color: #64748b;
  font-size: 12px;
}

.fav-price {
  margin-top: 8px;
  font-weight: 800;
}

.empty {
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  color: #64748b;
  padding: 14px;
}

.profile-card--guest {
  max-width: 720px;
  margin: 0 auto;
}

@media (max-width: 960px) {
  .profile-card--hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    justify-content: flex-start;
  }

  .stats-row,
  .quick-links,
  .favorites-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 640px) {
  .profile-page {
    padding-left: 10px;
    padding-right: 10px;
  }

  .profile-card {
    padding: 16px;
    border-radius: 16px;
  }

  .hero-left {
    width: 100%;
  }

  .stats-row,
  .quick-links,
  .favorites-grid {
    grid-template-columns: 1fr;
  }
}
</style>
