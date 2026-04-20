<template>
  <div class="profile-page">
    <section v-if="sessionReady && profileUser" class="profile-grid">
      <article class="profile-card profile-card--hero">
        <NuxtLink to="/editprofile" class="profile-link profile-link--soft profile-edit-top">Editar perfil</NuxtLink>

        <div class="hero-left">
          <span class="avatar">{{ userInitial }}</span>
          <div>
            <p class="eyebrow">Cuenta</p>
            <p class="profile-name">{{ profileUser.username }}</p>
            <p class="profile-email">{{ profileUser.email }}</p>
            <p class="hero-note">Gestiona tus publicaciones y conversaciones en un solo lugar.</p>
            <p v-if="isDemoMode" class="section-note">Vista demo activa</p>
          </div>
        </div>

        <div class="hero-actions">
          <NuxtLink to="/chat" class="profile-link">Ir a mensajes</NuxtLink>
          <NuxtLink to="/explorar" class="profile-link profile-link--soft">Explorar artículos</NuxtLink>
          <button type="button" class="profile-link profile-link--danger" @click="signOut">Cerrar sesión</button>
        </div>
      </article>

      <article class="profile-card profile-card--compact">
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
          <h2>Mis publicaciones</h2>
          <span class="section-note">Los productos que subiste desde tu perfil</span>
        </div>

        <div v-if="loadingPublished" class="empty">Cargando tus publicaciones...</div>
        <div v-else-if="!publishedItems.length" class="empty">
          Todavía no has publicado artículos.
        </div>

        <div v-else class="published-grid">
          <ItemCard
            v-for="item in publishedItems"
            :key="item.id"
            :item="item"
            :show-badge="true"
            :show-fav="true"
          />
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
import ItemCard from '~/components/ItemCard.vue'
import type { Item } from '~/stores/items'
import type { SessionUser } from '~/composables/useSessionUser'

const { sessionUser, loadSessionUser, clearSessionUser, storageEventName } = useSessionUser()
const store = useItemsStore()
const route = useRoute()
const uiMessages = useUiMessages()
const sessionReady = ref(false)
const loadingPublished = ref(false)
const conversationsCount = ref(0)
const publishedItems = ref<Item[]>([])

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
  { to: '/vender', title: 'Publicar articulo', description: 'Sube una nueva prenda y activa tu anuncio en minutos.', cta: 'Publicar' },
  { to: '/chat', title: 'Responder mensajes', description: 'Gestiona tus conversaciones con compradores y vendedores.', cta: 'Responder' },
  { to: '/explorar', title: 'Buscar gangas', description: 'Descubre piezas interesantes para tu armario.', cta: 'Explorar' }
]

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

async function loadPublishedItems() {
  loadingPublished.value = true
  try {
    if (!store.items.length) {
      await store.fetchAll()
    }

    if (isDemoMode.value) {
      publishedItems.value = store.items.slice(0, 3)
      return
    }

    const currentUserId = profileUser.value?.id
    if (!currentUserId) {
      publishedItems.value = []
      return
    }

    publishedItems.value = store.items.filter((item) => String(item.sellerId ?? '') === currentUserId).slice(0, 6)
  } finally {
    loadingPublished.value = false
  }
}

async function refreshProfileData() {
  await Promise.all([
    loadPublishedItems(),
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
  publishedItems.value = []
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
})

onBeforeUnmount(() => {
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
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

.profile-card--compact {
  padding: 20px 18px;
}

.profile-grid {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 18px;
}

.profile-card--hero {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fcfb 52%, #eefcf8 100%);
}

.profile-edit-top {
  position: absolute;
  top: 18px;
  right: 20px;
  z-index: 2;
}

.hero-left {
  display: flex;
  align-items: center;
  gap: 18px;
  width: 100%;
}

.avatar {
  width: 108px;
  height: 108px;
  border-radius: 999px;
  background: linear-gradient(145deg, #dcfce7 0%, #ccfbf1 100%);
  color: #0f766e;
  border: 1px solid #99f6e4;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 44px;
  font-weight: 800;
  box-shadow: 0 14px 28px rgba(20, 184, 166, 0.24);
}

.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: nowrap;
  justify-content: flex-start;
  width: 100%;
}

.profile-card--hero .profile-link {
  min-height: 36px;
  padding: 0 14px;
  font-size: 0.78rem;
  letter-spacing: 0.01em;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0f766e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

.profile-name,
.profile-email {
  margin: 0 0 10px;
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

.section-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 10px;
}

.section-head h2 {
  margin: 0;
  font-size: 18px;
  line-height: 1.12;
  letter-spacing: -0.03em;
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
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-height: 74px;
  border-radius: 16px;
  border: 1px solid #dbe4ee;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  color: #0f172a;
  padding: 10px;
  text-align: left;
  transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-link__title {
  font-weight: 700;
  font-size: 0.89rem;
  line-height: 1.2;
}

.quick-link__desc {
  color: #64748b;
  font-size: 0.74rem;
  line-height: 1.35;
}

.quick-link__cta {
  margin-top: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 24px;
  width: fit-content;
  padding: 0 8px;
  border-radius: 999px;
  border: 1px solid #a7f3d0;
  color: #0f766e;
  font-size: 0.69rem;
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

.published-grid {
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
    align-items: flex-start;
  }

  .profile-edit-top {
    position: static;
    margin-left: auto;
    margin-bottom: 6px;
  }

  .hero-actions {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .quick-links,
  .favorites-grid,
  .published-grid {
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

  .profile-card--compact {
    padding: 16px;
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
    width: 84px;
    height: 84px;
    font-size: 34px;
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

  .favorites-grid,
  .published-grid {
    grid-template-columns: 1fr;
  }

  .quick-links {
    grid-template-columns: 1fr;
  }
}
</style>
