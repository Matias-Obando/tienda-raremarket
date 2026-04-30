<template>
  <div class="profile-page">
    <section v-if="sessionReady && profileUser" class="profile-grid">
      <article class="profile-card profile-card--hero">
        <NuxtLink to="/editprofile" class="profile-link profile-link--soft profile-edit-top">Editar perfil</NuxtLink>

        <div class="hero-left">
          <span class="avatar">
            <img v-if="profileAvatar" :src="profileAvatar" :alt="`Foto de perfil de ${profileUser.username}`" class="avatar__image" />
            <span v-else>{{ userInitial }}</span>
          </span>
          <div>
            <p class="eyebrow">Cuenta</p>
            <p class="profile-name">{{ profileUser.username }}</p>
            <p class="profile-email">{{ profileUser.email }}</p>
            <p v-if="profileSummary" class="profile-summary">{{ profileSummary }}</p>
            <p class="hero-note">Gestiona tus publicaciones y conversaciones en un solo lugar.</p>
            <p v-if="profileUser.bio" class="profile-bio">{{ profileUser.bio }}</p>
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
          <h2>Mis pedidos</h2>
          <span class="section-note">Gestiona compras y ventas simuladas</span>
        </div>

        <div class="order-tabs">
          <button
            type="button"
            class="order-tab"
            :class="{ active: ordersRole === 'buyer' }"
            @click="ordersRole = 'buyer'; loadOrders()"
          >Compras</button>
          <button
            type="button"
            class="order-tab"
            :class="{ active: ordersRole === 'seller' }"
            @click="ordersRole = 'seller'; loadOrders()"
          >Ventas</button>
        </div>

        <div v-if="loadingOrders" class="empty">Cargando pedidos...</div>
        <div v-else-if="!orders.length" class="empty">Todavia no tienes pedidos en esta vista.</div>

        <div v-else class="orders-list">
          <article v-for="order in orders" :key="order.id" class="order-card">
            <div class="order-top">
              <div>
                <p class="order-title">{{ order.itemTitle }}</p>
                <p class="order-meta">{{ order.amountEur }} EUR</p>
              </div>
              <span class="order-status">{{ prettyStatus(order.status) }}</span>
            </div>

            <div class="order-detail">
              <img :src="order.itemImage" :alt="order.itemTitle" class="order-image" />
              <div>
                <p v-if="order.deliveryMethod === 'shipping'" class="order-sub">
                  Envio a: {{ order.shippingFullName }} · {{ order.shippingCity }}
                </p>
                <p v-if="order.paymentBrand === 'EFECTIVO'" class="order-sub">Pago: En efectivo</p>
                <p v-else class="order-sub">Pago simulado: {{ order.paymentBrand }} ****{{ order.paymentLast4 }}</p>
              </div>
            </div>

            <div class="order-actions">
              <button
                v-for="action in orderActions(order)"
                :key="action.to"
                type="button"
                class="profile-link profile-link--soft order-action-btn"
                :disabled="updatingOrderId === order.id"
                @click="updateOrderStatus(order.id, action.to)"
              >
                {{ updatingOrderId === order.id ? 'Actualizando...' : action.label }}
              </button>
            </div>
          </article>
        </div>
      </article>

      <article id="mis-publicaciones" class="profile-card">
        <div class="section-head">
          <h2>Mis publicaciones</h2>
          <span class="section-note">Los productos que subiste desde tu perfil</span>
        </div>

        <div v-if="loadingPublished" class="empty">Cargando tus publicaciones...</div>
        <div v-else-if="!publishedItems.length" class="empty">
          Todavía no has publicado artículos.
        </div>

        <div v-else class="published-grid">
          <article v-for="item in publishedItems" :key="item.id" class="published-item">
            <ItemCard
              :item="item"
              :show-badge="true"
              :show-fav="false"
            />
            <div v-if="!isDemoMode" class="published-actions">
              <NuxtLink
                :to="{ path: '/vender', query: { edit: item.id } }"
                class="edit-publication-btn"
              >
                Editar publicación
              </NuxtLink>
              <button
                type="button"
                class="delete-publication-btn"
                :disabled="deletingItemId === item.id"
                @click="openDeleteModal(item.id, item.titulo)"
              >
                {{ deletingItemId === item.id ? 'Eliminando...' : 'Eliminar publicacion' }}
              </button>
            </div>
          </article>
        </div>
      </article>
    </section>

    <Teleport to="body">
      <div v-if="deleteModal.open" class="delete-modal" @click.self="closeDeleteModal">
        <div class="delete-modal__dialog" role="dialog" aria-modal="true" aria-labelledby="delete-modal-title">
          <p class="delete-modal__eyebrow">Eliminar publicacion</p>
          <h3 id="delete-modal-title">¿Seguro que quieres eliminar este articulo?</h3>
          <p class="delete-modal__text">
            Se eliminara <strong>"{{ deleteModal.itemTitle }}"</strong> de tu perfil y no podras recuperarlo.
          </p>
          <div class="delete-modal__actions">
            <button type="button" class="delete-modal__btn delete-modal__btn--ghost" @click="closeDeleteModal">
              Cancelar
            </button>
            <button
              type="button"
              class="delete-modal__btn delete-modal__btn--danger"
              :disabled="deletingItemId === deleteModal.itemId"
              @click="confirmDeletePublishedItem"
            >
              {{ deletingItemId === deleteModal.itemId ? 'Eliminando...' : 'Si, eliminar' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <section v-if="sessionReady && !profileUser" class="profile-card profile-card--guest">
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
const loadingOrders = ref(false)
const conversationsCount = ref(0)
const publishedItems = ref<Item[]>([])
const ordersRole = ref<'buyer' | 'seller'>('buyer')
const orders = ref<OrderSummary[]>([])
const updatingOrderId = ref<string | null>(null)
const deletingItemId = ref<string | null>(null)
const deleteModal = reactive({
  open: false,
  itemId: '',
  itemTitle: ''
})

type InboxConversation = {
  id: string
  itemId: string
  lastMessage?: string
  unreadCount: number
}

type OrderSummary = {
  id: string
  itemId: string
  buyerId: string
  sellerId: string
  itemTitle: string
  itemImage: string
  amountEur: number
  deliveryMethod: 'shipping'
  shippingFullName?: string
  shippingCity?: string
  paymentBrand: string
  paymentLast4: string
  status: string
  createdAt: string
}

type OrderAction = {
  label: string
  to: string
}

const demoUser: SessionUser = {
  id: 'demo-user',
  username: 'Closely Demo',
  email: 'demo@closely.app',
  location: 'Madrid',
  phone: '+34 600 111 222',
  bio: 'Perfil de prueba para ver cómo se comporta la edición sin tocar tu cuenta real.'
}

const isDemoMode = computed(() => route.query.demo === '1' && !sessionUser.value)
const profileUser = computed<SessionUser | null>(() => {
  if (sessionUser.value) {
    return sessionUser.value
  }
  return isDemoMode.value ? demoUser : null
})

const userInitial = computed(() => profileUser.value?.username?.charAt(0).toUpperCase() || 'U')
const profileAvatar = computed(() => profileUser.value?.avatarUrl || '')
const profileSummary = computed(() => {
  const parts = [profileUser.value?.location, profileUser.value?.phone].filter(Boolean)
  return parts.length ? parts.join(' · ') : ''
})

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

function prettyStatus(status: string) {
  const map: Record<string, string> = {
    PENDIENTE_ACEPTACION: 'Pendiente de aceptacion',
    ACEPTADO: 'Aceptado',
    RECHAZADO: 'Rechazado',
    PREPARANDO_ENVIO: 'Preparando envio',
    ENVIADO: 'Enviado',
    QUEDADA_ACORDADA: 'Quedada acordada',
    ENTREGADO: 'Vendido',
    COMPLETADO: 'Completado',
    CANCELADO: 'Cancelado'
  }
  return map[status] ?? status
}

function orderActions(order: OrderSummary): OrderAction[] {
  if (ordersRole.value === 'seller') {
    if (order.status === 'PENDIENTE_ACEPTACION') {
      return [
        { label: 'Aceptar', to: 'ACEPTADO' },
        { label: 'Rechazar', to: 'RECHAZADO' }
      ]
    }
    if (order.status === 'ACEPTADO' && order.deliveryMethod === 'shipping') {
      return [
        { label: 'Preparar envio', to: 'PREPARANDO_ENVIO' },
        { label: 'Marcar enviado', to: 'ENVIADO' }
      ]
    }
    if (order.status === 'PREPARANDO_ENVIO') {
      return [{ label: 'Marcar enviado', to: 'ENVIADO' }]
    }
    return []
  }

  if (order.status === 'PENDIENTE_ACEPTACION') {
    return [{ label: 'Cancelar pedido', to: 'CANCELADO' }]
  }
  if (order.status === 'ENVIADO' || order.status === 'ENTREGADO') {
    return [{ label: 'Confirmar completado', to: 'COMPLETADO' }]
  }
  return []
}

async function loadOrders() {
  if (!profileUser.value || isDemoMode.value) {
    orders.value = []
    return
  }

  const token = profileUser.value.token
  if (!token) {
    orders.value = []
    return
  }

  loadingOrders.value = true
  try {
    const config = useRuntimeConfig()
    orders.value = await $fetch<OrderSummary[]>(`${config.public.API_BASE_URL}/orders`, {
      params: { role: ordersRole.value },
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
  } catch (error: any) {
    orders.value = []
    uiMessages.error(error?.data?.message || 'No se pudieron cargar los pedidos.')
  } finally {
    loadingOrders.value = false
  }
}

async function updateOrderStatus(orderId: string, nextStatus: string) {
  if (!profileUser.value?.token) {
    uiMessages.error('Necesitas iniciar sesion para actualizar pedidos.')
    return
  }

  updatingOrderId.value = orderId
  try {
    const config = useRuntimeConfig()
    await $fetch(`${config.public.API_BASE_URL}/orders/${orderId}/status`, {
      method: 'PATCH',
      headers: {
        Authorization: `Bearer ${profileUser.value.token}`
      },
      body: {
        status: nextStatus
      }
    })

    await loadOrders()
    uiMessages.success('Estado del pedido actualizado.')
  } catch (error: any) {
    uiMessages.error(error?.data?.message || 'No se pudo actualizar el pedido.')
  } finally {
    updatingOrderId.value = null
  }
}

async function refreshProfileData() {
  await Promise.all([
    loadPublishedItems(),
    loadConversationsCount(),
    loadOrders()
  ])
}

function openDeleteModal(itemId: string, itemTitle: string) {
  deleteModal.open = true
  deleteModal.itemId = itemId
  deleteModal.itemTitle = itemTitle
}

function closeDeleteModal() {
  if (deletingItemId.value) {
    return
  }
  deleteModal.open = false
  deleteModal.itemId = ''
  deleteModal.itemTitle = ''
}

async function confirmDeletePublishedItem() {
  if (isDemoMode.value) {
    return
  }

  if (!deleteModal.itemId) {
    return
  }

  deletingItemId.value = deleteModal.itemId
  try {
    await store.deleteItem(deleteModal.itemId)
    publishedItems.value = publishedItems.value.filter((item) => item.id !== deleteModal.itemId)
    uiMessages.success('Publicacion eliminada correctamente.')
    closeDeleteModal()
  } catch (error: any) {
    const message = error?.data?.message || 'No se pudo eliminar la publicacion. Intentalo de nuevo.'
    uiMessages.error(message)
  } finally {
    deletingItemId.value = null
  }
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
  background: var(--rm-page-bg);
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
  overflow: hidden;
  background: linear-gradient(145deg, #dcfce7 0%, #ccfbf1 100%);
  color: #1fb981;
  border: 1px solid #99f6e4;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 44px;
  font-weight: 800;
  box-shadow: 0 14px 28px rgba(20, 184, 166, 0.24);
}

.avatar__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
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
  color: #1fb981;
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

.profile-summary {
  margin: 0 0 8px;
  color: #1fb981;
  font-weight: 600;
}

.hero-note {
  margin: 0;
  max-width: 56ch;
  color: #475569;
  font-size: 0.94rem;
}

.profile-bio {
  margin: 10px 0 0;
  max-width: 56ch;
  color: #334155;
  font-size: 0.92rem;
}

.profile-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  min-height: 44px;
  padding: 0 16px;
  background: #1fb981;
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
  color: #1fb981;
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

.order-tabs {
  display: inline-flex;
  gap: 8px;
  margin-bottom: 12px;
}

.order-tab {
  min-height: 34px;
  border-radius: 999px;
  border: 1px solid #cbd5e1;
  background: #ffffff;
  color: #334155;
  font-size: 0.8rem;
  font-weight: 700;
  padding: 0 12px;
  cursor: pointer;
}

.order-tab.active {
  border-color: #1fb981;
  background: #ecfdf5;
  color: #0f766e;
}

.orders-list {
  display: grid;
  gap: 10px;
}

.order-card {
  border: 1px solid #dbe4ee;
  border-radius: 16px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  padding: 12px;
}

.order-top {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: flex-start;
}

.order-title {
  margin: 0;
  font-weight: 700;
  color: #0f172a;
}

.order-meta {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 0.82rem;
}

.order-status {
  border-radius: 999px;
  border: 1px solid #99f6e4;
  color: #0f766e;
  background: #f0fdfa;
  padding: 4px 10px;
  font-size: 0.74rem;
  font-weight: 700;
  white-space: nowrap;
}

.order-detail {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-image {
  width: 62px;
  height: 62px;
  border-radius: 10px;
  border: 1px solid #dbe4ee;
  object-fit: cover;
  background: #fff;
}

.order-sub {
  margin: 0 0 4px;
  color: #475569;
  font-size: 0.83rem;
}

.order-actions {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.order-action-btn {
  min-height: 34px;
  font-size: 0.74rem;
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
  color: #1fb981;
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

.published-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.delete-publication-btn {
  min-height: 36px;
  border-radius: 10px;
  border: 1px solid #fecdd3;
  background: #fff1f2;
  color: #be123c;
  font-weight: 700;
  font-size: 0.82rem;
  cursor: pointer;
  flex: 1;
}

.delete-publication-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.published-actions {
  display: flex;
  gap: 8px;
  width: 100%;
}

.edit-publication-btn {
  flex: 1;
  min-height: 36px;
  border-radius: 10px;
  border: 1px solid #d2dae4;
  background: #f8fafc;
  color: #5b6472;
  font-weight: 700;
  font-size: 0.82rem;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.edit-publication-btn:hover {
  border-color: #1fb981;
  background: #f0fdf9;
  color: #1fb981;
}

.delete-modal {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(15, 23, 42, 0.48);
  backdrop-filter: blur(3px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px;
}

.delete-modal__dialog {
  width: min(520px, 100%);
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  background: linear-gradient(180deg, #ffffff 0%, #fff7f8 100%);
  box-shadow: 0 26px 60px rgba(15, 23, 42, 0.26);
  padding: 22px;
}

.delete-modal__eyebrow {
  margin: 0;
  color: #be123c;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.07em;
  text-transform: uppercase;
}

.delete-modal h3 {
  margin: 8px 0 0;
  color: #0f172a;
  font-size: 1.25rem;
  line-height: 1.2;
}

.delete-modal__text {
  margin: 12px 0 0;
  color: #475569;
  line-height: 1.5;
}

.delete-modal__actions {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.delete-modal__btn {
  min-height: 40px;
  border-radius: 999px;
  padding: 0 16px;
  font-size: 0.88rem;
  font-weight: 700;
  border: 1px solid transparent;
  cursor: pointer;
}

.delete-modal__btn--ghost {
  background: #ffffff;
  color: #334155;
  border-color: #cbd5e1;
}

.delete-modal__btn--danger {
  background: #e11d48;
  color: #ffffff;
  box-shadow: 0 12px 24px rgba(225, 29, 72, 0.28);
}

.delete-modal__btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
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

  .order-top {
    flex-direction: column;
    align-items: flex-start;
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
