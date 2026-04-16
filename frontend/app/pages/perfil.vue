<template>
  <div class="profile-page">
    <section v-if="sessionReady && profileUser" class="profile-grid">
      <article class="profile-card profile-card--hero">
        <div class="hero-left">
          <span class="avatar">{{ userInitial }}</span>
          <div>
            <p class="eyebrow">Cuenta</p>
            <h1>Mi perfil</h1>
            <p class="profile-name">{{ profileUser.username }}</p>
            <p class="profile-email">{{ profileUser.email }}</p>
            <p class="hero-note">Gestiona tus favoritos, conversaciones y anuncios en un solo lugar.</p>
            <p v-if="isDemoMode" class="section-note">Vista demo activa</p>
          </div>
        </div>

        <div class="hero-actions">
          <NuxtLink to="/editprofile" class="profile-link profile-link--soft">Editar perfil</NuxtLink>
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
          <p class="stat-value">{{ isDemoMode ? 'Demo' : 'Activa' }}</p>
        </div>
      </article>

      <article class="profile-card">
        <div class="section-head">
          <h2>Accesos rápidos</h2>
        </div>

        <div class="quick-links">
          <NuxtLink
            v-for="action in quickActions"
            :key="action.to"
            :to="action.to"
            class="quick-link"
          >
            <span class="quick-link__title">{{ action.title }}</span>
            <span class="quick-link__desc">{{ action.description }}</span>
            <span class="quick-link__cta">{{ action.cta }}</span>
          </NuxtLink>
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
      <p class="eyebrow">Cuenta</p>
      <h1>Mi perfil</h1>
      <p class="profile-email">Todavía no has iniciado sesión. Entra para ver tus favoritos y actividad.</p>
      <div class="hero-actions">
        <NuxtLink :to="{ path: '/auth', query: { mode: 'login', redirect: '/perfil' } }" class="profile-link">
          Iniciar sesión
        </NuxtLink>
        <NuxtLink :to="{ path: '/perfil', query: { demo: '1' } }" class="profile-link profile-link--soft">
          Ver vista demo
        </NuxtLink>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Item } from '~/stores/items'
import type { SessionUser } from '~/composables/useSessionUser'

const { sessionUser, loadSessionUser, clearSessionUser, storageEventName } = useSessionUser()
const store = useItemsStore()
const route = useRoute()
const uiMessages = useUiMessages()
const sessionReady = ref(false)
const loadingFavorites = ref(false)
const conversationsCount = ref(0)
const favoriteItems = ref<Item[]>([])
const LS_FAVORITES_KEY = 'closely:favorites'

const demoUser: SessionUser = {
  id: 'demo-user',
  username: 'Closely Demo',
  email: 'demo@closely.app'
}

const isDemoMode = computed(() => route.query.demo === '1' && !sessionUser.value)
const profileUser = computed<SessionUser | null>(() => {
  if (sessionUser.value) {
    return sessionUser.value
  }
  return isDemoMode.value ? demoUser : null
})

const userInitial = computed(() => profileUser.value?.username?.charAt(0).toUpperCase() || 'U')

const quickActions = [
  { to: '/editprofile', title: 'Editar perfil', description: 'Actualiza tus datos y tu foto para generar mas confianza.', cta: 'Editar' },
  { to: '/vender', title: 'Publicar articulo', description: 'Sube una nueva prenda y activa tu anuncio en minutos.', cta: 'Publicar' },
  { to: '/chat', title: 'Responder mensajes', description: 'Gestiona tus conversaciones con compradores y vendedores.', cta: 'Responder' },
  { to: '/explorar', title: 'Buscar gangas', description: 'Descubre piezas interesantes para tu armario.', cta: 'Explorar' }
]

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
  if (!profileUser.value) {
    conversationsCount.value = 0
    return
  }

  if (isDemoMode.value) {
    conversationsCount.value = 4
    return
  }

  try {
    const config = useRuntimeConfig()
    const list = await $fetch<Array<{ id: string }>>(`${config.public.API_BASE_URL}/api/chat/conversations`, {
      params: { userId: profileUser.value.id }
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

    if (isDemoMode.value) {
      favoriteItems.value = store.items.slice(0, 6)
      return
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
  if (isDemoMode.value) {
    navigateTo('/perfil')
    return
  }

  clearSessionUser()
  conversationsCount.value = 0
  favoriteItems.value = []
  uiMessages.info('Sesion cerrada correctamente.')
  navigateTo('/')
}

function syncSession() {
  loadSessionUser()
  if (profileUser.value) {
    refreshProfileData()
  }
}

onMounted(async () => {
  loadSessionUser()
  sessionReady.value = true

  if (profileUser.value) {
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
  padding: 24px 16px 56px;
  background:
    radial-gradient(circle at 15% 8%, rgba(15, 118, 110, 0.1), transparent 28%),
    radial-gradient(circle at 88% 2%, rgba(15, 23, 42, 0.08), transparent 24%),
    linear-gradient(180deg, #f8fafc 0%, #eef2f6 100%);
}

.profile-card {
  width: 100%;
  background: #fff;
  border: 1px solid #e6ebf1;
  border-radius: 26px;
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.08);
  padding: 30px;
}

.profile-grid {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 18px;
}

.profile-card--hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fcfb 52%, #eefcf8 100%);
}

.hero-left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.avatar {
  width: 78px;
  height: 78px;
  border-radius: 999px;
  background: linear-gradient(145deg, #dcfce7 0%, #ccfbf1 100%);
  color: #0f766e;
  border: 1px solid #99f6e4;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  font-weight: 800;
  box-shadow: 0 8px 22px rgba(20, 184, 166, 0.22);
}

.hero-actions {
  display: flex;
  gap: 12px;
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
  margin: 0 0 10px;
}

.profile-card h1 {
  font-size: clamp(1.85rem, 2.5vw, 2.35rem);
  line-height: 1.08;
  letter-spacing: -0.04em;
}

.profile-name {
  font-size: 27px;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.profile-email {
  color: #64748b;
}

.hero-note {
  margin: 0;
  max-width: 56ch;
  color: #475569;
  font-size: 0.94rem;
}

.profile-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  min-height: 44px;
  padding: 0 16px;
  background: #0f766e;
  color: #fff;
  border: 1px solid transparent;
  text-decoration: none;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.profile-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(15, 118, 110, 0.2);
}

.profile-link--soft {
  background: #fff;
  color: #0f766e;
  border-color: #99f6e4;
}

.profile-link--danger {
  background: #fff5f7;
  color: #be123c;
  border-color: #fecdd3;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.stat-card {
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 16px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.stat-label {
  margin: 0;
  color: #64748b;
  font-size: 13px;
}

.stat-value {
  margin: 6px 0 0;
  font-size: 27px;
  font-weight: 800;
  letter-spacing: -0.04em;
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
  font-size: 22px;
  line-height: 1.12;
  letter-spacing: -0.03em;
}

.section-note {
  color: #64748b;
  font-size: 13px;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.quick-link {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-height: 120px;
  border-radius: 16px;
  border: 1px solid #dbe4ee;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  color: #0f172a;
  padding: 14px;
  text-align: left;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-link__title {
  font-weight: 700;
  font-size: 0.98rem;
  line-height: 1.2;
}

.quick-link__desc {
  color: #64748b;
  font-size: 0.86rem;
  line-height: 1.45;
}

.quick-link__cta {
  margin-top: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 30px;
  width: fit-content;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid #a7f3d0;
  color: #0f766e;
  font-size: 0.78rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.quick-link:hover {
  border-color: #14b8a6;
  transform: translateY(-1px);
  box-shadow: 0 10px 18px rgba(15, 118, 110, 0.14);
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
  border-radius: 16px;
  overflow: hidden;
  background: #fff;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.fav-card:hover {
  transform: translateY(-2px);
  border-color: #99f6e4;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
}

.fav-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.fav-body {
  padding: 12px;
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
  border-radius: 14px;
  background: #f8fafc;
  color: #64748b;
  padding: 16px;
}

.profile-card--guest {
  max-width: 720px;
  margin: 0 auto;
  text-align: left;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbfe 100%);
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
    padding-top: 16px;
    padding-bottom: 36px;
  }

  .profile-card {
    padding: 18px;
    border-radius: 16px;
  }

  .profile-card h1 {
    font-size: 1.75rem;
  }

  .profile-name {
    font-size: 1.4rem;
  }

  .hero-left {
    width: 100%;
    align-items: flex-start;
  }

  .avatar {
    width: 64px;
    height: 64px;
    font-size: 24px;
  }

  .hero-actions {
    width: 100%;
    gap: 8px;
  }

  .profile-link {
    width: 100%;
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .stat-card {
    padding: 14px;
  }

  .stat-value {
    font-size: 24px;
  }

  .stats-row,
  .favorites-grid {
    grid-template-columns: 1fr;
  }

  .quick-links {
    grid-template-columns: 1fr;
  }
}
</style>
