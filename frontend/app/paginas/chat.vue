<template>
  <div class="chat-page">
    <div class="chat-shell" :class="{ 'chat-shell--mobile-conversation': isMobileConversationOpen }">
      <aside v-show="!isMobileConversationOpen" class="chat-sidebar">
        <div class="sidebar-heading">
          <p class="eyebrow">Mensajes</p>
          <h1>Bandeja</h1>
          <p class="sidebar-sub">Gestiona tus conversaciones de compra y venta</p>
        </div>

        <div v-if="loadingConversations" class="sidebar-empty">
          Cargando conversaciones...
        </div>
        <div v-else-if="!filteredConversations.length" class="sidebar-empty">
          Aún no tienes conversaciones. Empieza enviando un mensaje a un vendedor.
        </div>
        <div v-else class="conversations-list">
          <button
            v-for="conversation in filteredConversations"
            :key="conversation.id"
            class="conversation-row"
            :class="{ active: selectedConversationId === conversation.id }"
            @click="selectConversation(conversation.id)"
          >
            <div class="row-main">
              <div class="row-avatar" aria-hidden="true">{{ getInitial(conversation.counterpartName) }}</div>
              <div class="row-content">
                <p class="row-title">{{ conversation.counterpartName }}</p>
                <p class="row-preview">{{ conversation.lastMessage || 'Sin mensajes aún' }}</p>
              </div>
              <div v-if="conversation.unreadCount > 0" class="pill pill--unread">
                {{ conversation.unreadCount }}
              </div>
            </div>
            <div class="row-footer">
              <span>{{ conversation.unreadCount > 0 ? 'Sin leer' : 'Leído' }}</span>
              <span class="row-dot" aria-hidden="true"></span>
              <span>{{ formatConversationStatus(conversation) }}</span>
              <button
                class="row-delete"
                type="button"
                title="Borrar chat"
                aria-label="Borrar chat"
                :disabled="deletingConversationId === conversation.id"
                @click.stop="deleteConversation(conversation.id)"
              >
                ×
              </button>
            </div>
          </button>
        </div>
      </aside>

      <section v-show="!isMobileLayout || !!selectedConversationId" class="chat-main">
        <div v-if="!sessionReady" class="chat-empty">
          Cargando tu sesión...
        </div>

        <div v-else-if="!canUseRealChat" class="chat-empty">
          <p>Necesitas iniciar sesión para usar el chat.</p>
          <NuxtLink class="primary-btn link-btn" to="/autenticacion?mode=login&redirect=/chat">
            Ir a login
          </NuxtLink>
        </div>

        <div v-else-if="!selectedConversation" class="chat-empty">
          <p class="chat-empty-title">Selecciona una conversación</p>
          <p>Elige una conversación de la izquierda para leer y responder mensajes.</p>
        </div>

        <template v-else>
          <header class="chat-header">
            <div class="chat-header-main">
              <span class="header-avatar" aria-hidden="true">{{ getInitial(selectedConversation.counterpartName) }}</span>
              <div>
                <p class="eyebrow">Conversación</p>
                <h2>{{ selectedConversation.counterpartName }}</h2>
                <p class="header-sub">Disponible para responder</p>
              </div>
            </div>
            <div class="header-meta">
              <span class="header-badge header-badge--live"></span>
              Activo ahora
            </div>
          </header>

          <button
            v-if="isMobileConversationOpen"
            class="mobile-back-btn"
            type="button"
            @click="closeConversation"
          >
            Volver a bandeja
          </button>

          <div class="messages-panel">
            <div v-if="loadingMessages" class="chat-empty">Cargando mensajes...</div>
            <div v-else-if="!messages.length" class="chat-empty">
              Todavía no hay mensajes. Escribe el primero.
            </div>

            <div v-if="isTypingDemo" class="typing-bubble">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
            </div>

            <article
              v-for="message in orderedMessages"
              :key="message.id"
              ref="messageRows"
              class="message-bubble"
              :class="{ own: message.senderId === sessionUser?.id }"
            >
              <p class="message-author">{{ message.senderName }}</p>
              <p class="message-content">{{ message.content }}</p>
              <p class="message-time">
                {{ formatDate(message.createdAt) }}
                <span v-if="message.senderId === sessionUser?.id" class="message-state">{{ message.isRead ? 'Leido' : 'Enviado' }}</span>
              </p>
            </article>
          </div>

          <form class="composer" @submit.prevent="sendMessage">
            <div class="composer-topbar">
              <span class="composer-hint">Mensaje privado</span>
            </div>
            <div class="composer-box">
              <textarea
                v-model="draftMessage"
                rows="3"
                maxlength="1000"
                placeholder="Escribe tu mensaje..."
                @keydown.enter.exact.prevent="sendMessage"
              />
              <button class="composer-send" type="submit" :disabled="sendingMessage || !draftMessage.trim()">
                {{ sendingMessage ? 'Enviando...' : 'Enviar' }}
              </button>
            </div>
            <div class="composer-actions">
              <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
              <p v-else class="counter-text">{{ draftMessage.trim().length }}/1000</p>
            </div>
          </form>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { nextTick } from 'vue'

type ChatUser = {
  id: string
  username: string
  email: string
}

type Conversation = {
  id: string
  itemId: string
  counterpartId: string
  counterpartName: string
  unreadCount: number
  lastMessage?: string
  updatedAt?: string
}

type Message = {
  id: string
  conversationId: string
  senderId: string
  senderName: string
  content: string
  isRead: boolean
  createdAt: string
}

type MessageMap = Record<string, Message[]>

const config = useRuntimeConfig()
const route = useRoute()
const router = useRouter()
const { sessionUser, loadSessionUser, storageEventName } = useSessionUser()
const { refreshUnreadChatCount } = useUnreadChatCount()
const uiMessages = useUiMessages()

let supabase: any = null
if (typeof window !== 'undefined') {
  supabase = useSupabaseClient()
}

const sessionReady = ref(false)
const conversations = ref<Conversation[]>([])
const messages = ref<Message[]>([])
const messageRows = ref<HTMLElement[] | null>(null)
const loadingConversations = ref(false)
const loadingMessages = ref(false)
const sendingMessage = ref(false)
const isTypingDemo = ref(false)
const isMobileLayout = ref(false)
const errorMessage = ref('')
const selectedConversationId = ref('')
const draftMessage = ref('')
const deletingConversationId = ref('')
let conversationChannel: any = null
let activeChannelConversationId = ''
const itemId = computed(() => typeof route.query.itemId === 'string' ? route.query.itemId : '')
const itemTitle = computed(() => typeof route.query.itemTitle === 'string' ? route.query.itemTitle : '')
const sellerIdFromQuery = computed(() => typeof route.query.sellerId === 'string' ? route.query.sellerId : '')
const sellerNameFromQuery = computed(() => typeof route.query.sellerName === 'string' ? route.query.sellerName : '')
const conversationIdFromQuery = computed(() => typeof route.query.conversationId === 'string' ? route.query.conversationId : '')
const canUseRealChat = computed(() => Boolean(sessionUser.value?.id && sessionUser.value?.token))

const selectedConversation = computed(() =>
  activeConversations.value.find((conversation) => conversation.id === selectedConversationId.value) ?? null
)

const activeConversations = computed(() => conversations.value ?? [])

const activeMessages = computed(() => messages.value ?? [])

const orderedMessages = computed(() =>
  [...(activeMessages.value ?? [])].sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
)

const filteredConversations = computed(() => activeConversations.value ?? [])

const isMobileConversationOpen = computed(() => isMobileLayout.value && !!selectedConversationId.value)

function getInitial(name?: string) {
  const normalized = (name ?? '').trim()
  return normalized ? normalized.charAt(0).toUpperCase() : '?'
}

function parseApiError(error: any, fallback: string) {
  return error?.data?.message || error?.data || fallback
}

async function scrollToBottom() {
  await nextTick()
  const rows = messageRows.value
  if (!rows || !rows.length) {
    return
  }

  rows[rows.length - 1]?.scrollIntoView({ behavior: 'smooth', block: 'end' })
}

function syncSession() {
  loadSessionUser()
}

function formatDate(value?: string) {
  if (!value) {
    return ''
  }

  return new Intl.DateTimeFormat('es-ES', {
    dateStyle: 'short',
    timeStyle: 'short'
  }).format(new Date(value))
}

function formatRelativeDate(value?: string) {
  if (!value) return 'Ahora'

  const diffMs = Date.now() - new Date(value).getTime()
  const diffMinutes = Math.max(1, Math.round(diffMs / 60000))

  if (diffMinutes < 60) {
    return diffMinutes === 1 ? 'Hace 1 min' : `Hace ${diffMinutes} min`
  }

  const diffHours = Math.round(diffMinutes / 60)
  if (diffHours < 24) {
    return diffHours === 1 ? 'Hace 1 h' : `Hace ${diffHours} h`
  }

  const diffDays = Math.round(diffHours / 24)
  return diffDays === 1 ? 'Hace 1 día' : `Hace ${diffDays} días`
}

function isConversationRecent(conversation: Conversation) {
  if (!conversation.updatedAt) {
    return false
  }

  const diffMinutes = Math.max(1, Math.round((Date.now() - new Date(conversation.updatedAt).getTime()) / 60000))
  return diffMinutes <= 20
}

function formatConversationStatus(conversation: Conversation) {
  return isConversationRecent(conversation) ? 'Activo ahora' : formatRelativeDate(conversation.updatedAt)
}

function notifyAttachmentSoon() {
  uiMessages.info('Adjuntar archivos estara disponible en una siguiente fase.')
}

function getAuthHeaders() {
  const token = sessionUser.value?.token
  if (!token) {
    return null
  }

  return {
    Authorization: `Bearer ${token}`
  }
}

function syncLayout() {
  if (!process.client) {
    return
  }

  isMobileLayout.value = window.innerWidth <= 960
}

function closeConversation() {
  selectedConversationId.value = ''
}

async function stopConversationSubscription() {
  if (!conversationChannel) {
    activeChannelConversationId = ''
    return
  }

  await supabase.removeChannel(conversationChannel)
  conversationChannel = null
  activeChannelConversationId = ''
}

async function subscribeToConversationMessages(conversationId: string) {
  if (!process.client || !conversationId || !supabase) {
    return
  }

  await stopConversationSubscription()
  activeChannelConversationId = conversationId

  conversationChannel = supabase
    .channel(`conversation:${conversationId}:messages`)
    .on('broadcast', { event: 'message_created' }, async () => {
      if (selectedConversationId.value !== conversationId) {
        return
      }

      await loadMessages(conversationId)
      await loadConversations()
      void refreshUnreadChatCount()
    })
    .subscribe()
}

async function broadcastMessageCreated(conversationId: string) {
  if (!process.client || !conversationChannel || activeChannelConversationId !== conversationId) {
    return
  }

  await conversationChannel.send({
    type: 'broadcast',
    event: 'message_created',
    payload: { conversationId }
  })
}

function getConversationContext() {
  const sellerId = sellerIdFromQuery.value
  if (!itemId.value || !sellerId || !sessionUser.value?.id) {
    return null
  }

  return {
    itemId: itemId.value,
    buyerId: sessionUser.value.id,
    sellerId,
    itemLabel: itemTitle.value || `Producto ${itemId.value}`,
    sellerLabel: sellerNameFromQuery.value || 'Vendedor'
  }
}

async function loadConversations() {
  if (!canUseRealChat.value) {
    conversations.value = []
    return
  }

  const headers = getAuthHeaders()
  if (!headers) {
    conversations.value = []
    return
  }

  loadingConversations.value = true
  try {
    const loadedConversations = await $fetch<Conversation[]>(`${config.public.API_BASE_URL}/chat/conversations`, {
      headers,
      params: { userId: sessionUser.value.id }
    })
    conversations.value = [...loadedConversations].sort((a, b) => {
      const aTime = new Date(a.updatedAt ?? 0).getTime()
      const bTime = new Date(b.updatedAt ?? 0).getTime()
      return bTime - aTime
    })
  } catch (error: any) {
    conversations.value = []
    if (error?.statusCode === 401 || error?.statusCode === 403) {
      errorMessage.value = 'Tu sesión ha caducado. Vuelve a iniciar sesión para usar el chat real.'
    }
  } finally {
    loadingConversations.value = false
    void refreshUnreadChatCount()
  }
}

async function loadMessages(conversationId: string) {
  if (!canUseRealChat.value) {
    return
  }

  const headers = getAuthHeaders()
  if (!headers) {
    return
  }

  loadingMessages.value = true
  try {
    messages.value = await $fetch<Message[]>(`${config.public.API_BASE_URL}/chat/conversations/${conversationId}/messages`, {
      headers,
      params: { userId: sessionUser.value.id }
    })
    await scrollToBottom()
  } catch (error: any) {
    messages.value = []
    if (error?.statusCode === 401 || error?.statusCode === 403) {
      errorMessage.value = 'Tu sesión ha caducado. Vuelve a iniciar sesión para leer y escribir mensajes.'
    }
  } finally {
    loadingMessages.value = false
  }
}

async function selectConversation(conversationId: string) {
  selectedConversationId.value = conversationId
  await loadMessages(conversationId)
  await subscribeToConversationMessages(conversationId)
  if (canUseRealChat.value) {
    const headers = getAuthHeaders()
    if (!headers) {
      return
    }

    await $fetch(`${config.public.API_BASE_URL}/chat/conversations/${conversationId}/read`, {
      method: 'PATCH',
      headers,
      params: { userId: sessionUser.value.id }
    })
    await loadConversations()
    void refreshUnreadChatCount()
  }
}

async function openConversationFromProduct() {
  const context = getConversationContext()
  if (!context) {
    return
  }

  if (String(context.buyerId) === String(context.sellerId)) {
    errorMessage.value = 'Este artículo es tuyo. Inicia sesión con la cuenta compradora para abrir el chat real.'
    uiMessages.error(errorMessage.value)
    return
  }

  const headers = getAuthHeaders()
  if (!headers) {
    errorMessage.value = 'Sesion invalida. Vuelve a iniciar sesion.'
    return
  }

  errorMessage.value = ''
  try {
    const conversation = await $fetch<Conversation>(`${config.public.API_BASE_URL}/chat/conversations`, {
      method: 'POST',
      headers,
      body: {
        itemId: context.itemId,
        buyerId: context.buyerId,
        sellerId: context.sellerId
      }
    })

    await loadConversations()
    void refreshUnreadChatCount()
    selectedConversationId.value = conversation.id
    await loadMessages(conversation.id)
    await subscribeToConversationMessages(conversation.id)
    await router.replace({ path: '/chat', query: {} })
    uiMessages.success(`Chat abierto con ${context.sellerLabel}.`)
  } catch (error: any) {
    errorMessage.value = parseApiError(error, 'No se pudo abrir la conversacion.')
    uiMessages.error(errorMessage.value)
  }
}

async function sendMessage() {
  if (!selectedConversationId.value || !draftMessage.value.trim()) {
    return
  }

  if (!canUseRealChat.value) {
    errorMessage.value = 'Inicia sesion para enviar mensajes reales.'
    return
  }

  const headers = getAuthHeaders()
  if (!headers) {
    errorMessage.value = 'Sesion invalida. Vuelve a iniciar sesion.'
    return
  }

  errorMessage.value = ''
  sendingMessage.value = true
  try {
    const savedMessage = await $fetch<Message>(`${config.public.API_BASE_URL}/chat/conversations/${selectedConversationId.value}/messages`, {
      method: 'POST',
      headers,
      body: {
        senderId: sessionUser.value.id,
        content: draftMessage.value.trim()
      }
    })
    draftMessage.value = ''
    await loadMessages(selectedConversationId.value)
    await loadConversations()
    void refreshUnreadChatCount()
    await broadcastMessageCreated(savedMessage.conversationId)
  } catch (error: any) {
    errorMessage.value = parseApiError(error, 'No se pudo enviar el mensaje.')
    uiMessages.error(errorMessage.value)
  } finally {
    sendingMessage.value = false
  }
}

async function deleteConversation(conversationId: string) {
  if (!canUseRealChat.value) {
    errorMessage.value = 'Inicia sesion para borrar el chat.'
    return
  }

  const confirmationMessage = 'Esta accion eliminara este chat y todos sus mensajes. ¿Quieres continuar?'
  if (!window.confirm(confirmationMessage)) {
    return
  }

  const headers = getAuthHeaders()
  if (!headers) {
    errorMessage.value = 'Sesion invalida. Vuelve a iniciar sesion.'
    return
  }

  deletingConversationId.value = conversationId
  errorMessage.value = ''

  try {
    await $fetch(`${config.public.API_BASE_URL}/chat/conversations/${conversationId}`, {
      method: 'DELETE',
      headers,
      params: { userId: sessionUser.value.id }
    })
  } catch (error: any) {
    errorMessage.value = parseApiError(error, 'No se pudo borrar el chat.')
    uiMessages.error(errorMessage.value)
    return
  } finally {
    deletingConversationId.value = ''
  }

  const wasSelected = selectedConversationId.value === conversationId
  conversations.value = conversations.value.filter((conversation) => conversation.id !== conversationId)
  if (wasSelected) {
    selectedConversationId.value = ''
    draftMessage.value = ''
    messages.value = []
    await stopConversationSubscription()
  }

  void loadConversations()
  void refreshUnreadChatCount()
  uiMessages.success('Chat borrado correctamente.')
}

async function loadData() {
  if (itemId.value && sellerIdFromQuery.value) {
    await openConversationFromProduct()
    await loadConversations()
    void refreshUnreadChatCount()
    return
  }

  await loadConversations()
  void refreshUnreadChatCount()
  
  // Si viene un conversationId en query, seleccionarlo directamente
  if (conversationIdFromQuery.value) {
    selectedConversationId.value = conversationIdFromQuery.value
    await loadMessages(selectedConversationId.value)
    await subscribeToConversationMessages(selectedConversationId.value)
    await router.replace({ path: '/chat', query: {} })
    return
  }
  
  if (!selectedConversationId.value && conversations.value.length) {
    selectedConversationId.value = conversations.value[0].id
    await loadMessages(selectedConversationId.value)
    await subscribeToConversationMessages(selectedConversationId.value)
  }
}

onMounted(async () => {
  loadSessionUser()
  sessionReady.value = true

  syncLayout()
  window.addEventListener('resize', syncLayout)

  window.addEventListener(storageEventName, syncSession)
  window.addEventListener('storage', syncSession)

  await loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', syncLayout)
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
  void stopConversationSubscription()
})

watch(orderedMessages, async () => {
  await scrollToBottom()
})
</script>

<style scoped>
.chat-page {
  padding: 18px 16px 48px;
  background:
    radial-gradient(1200px 420px at 0% 0%, rgba(31, 185, 129, 0.08), transparent 65%),
    radial-gradient(900px 360px at 100% 100%, rgba(15, 23, 42, 0.06), transparent 70%);
}

.chat-shell {
  max-width: 1320px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 18px;
  align-items: start;
}

.chat-sidebar,
.chat-main {
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid var(--rm-border);
  border-radius: 22px;
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(4px);
}

.chat-sidebar {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  position: sticky;
  top: calc(var(--catnav-top, 72px) + var(--catnav-height, 56px) + 12px);
  max-height: calc(100vh - (var(--catnav-top, 72px) + var(--catnav-height, 56px) + 28px));
  overflow: auto;
}

.sidebar-heading h1 {
  font-size: 1.55rem;
}

.sidebar-sub {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 0.9rem;
}

.sidebar-stats {
  border: 1px dashed #bfdbfe;
  background: #f8fbff;
  border-radius: 12px;
  padding: 8px 12px;
  color: #475569;
  font-size: 0.82rem;
  font-weight: 600;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #dbe4ee;
  border-radius: 14px;
  background: #f8fafc;
  padding: 0 12px;
  min-height: 46px;
}

.search-box__icon {
  width: 18px;
  height: 18px;
  color: #94a3b8;
  flex-shrink: 0;
}

.search-box__icon svg {
  width: 100%;
  height: 100%;
  display: block;
}

.search-box input {
  cursor: pointer;
  border: 0;
  background: transparent;
  padding: 0;
  min-height: 42px;
  font-size: 0.95rem;
}

.search-box input:focus {
  outline: none;
}
.row-delete {
  margin-left: auto;
  width: 26px;
  height: 26px;
  border: 1px solid #fecaca;
  border-radius: 999px;
  background: #fff1f2;
  color: #dc2626;
  font-size: 1.05rem;
  line-height: 1;
  font-weight: 800;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.15s ease, background 0.15s ease, border-color 0.15s ease;
}
.row-delete:hover {
  background: #ffe4e6;
  border-color: #fda4af;
  transform: translateY(-1px);
}
.row-delete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.sidebar-top,
.chat-header,
.composer-actions,
.row-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.sidebar-actions {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.eyebrow {
  margin: 0 0 6px;
  color: #1fb981;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

.sidebar-top h1,
.chat-header h2,
.start-title {
  margin: 0;
}

.start-card {
  background: #f5fffd;
  border: 1px solid #c8f2e9;
  border-radius: 18px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.start-label,
.row-sub,
.row-preview,
.header-meta,
.sidebar-empty,
.chat-empty,
.message-time {
  color: #64748b;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field select,
.composer textarea {
  flex: 1;
  width: 100%;
  border: 1px solid var(--rm-border);
  border-radius: 14px;
  padding: 12px 14px;
  background: #fff;
}

.conversation-row {
  border: 1px solid var(--rm-border);
  background: #fff;
  border-radius: 16px;
  padding: 12px;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.15s ease, background 0.15s ease, transform 0.15s ease;
}

.conversation-row:hover,
.conversation-row.active {
  border-color: #14b8a6;
  background: #f8fffe;
  transform: translateY(-1px);
}

.pill {
  min-width: 24px;
  height: 24px;
  border-radius: 999px;
  background: #1fb981;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.pill--unread {
  background: #1fb981;
  color: #fff;
}

.pill--live {
  min-width: auto;
  padding: 0 10px;
  background: #dcfce7;
  color: #166534;
  border: 1px solid #86efac;
}

.row-avatar {
  width: 34px;
  height: 34px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  text-align: center;
  flex-shrink: 0;
  background: linear-gradient(180deg, #ecfdf5 0%, #d1fae5 100%);
  color: #065f46;
  font-weight: 800;
  font-size: 0.9rem;
  border: 1px solid #a7f3d0;
}

.row-copy {
  min-width: 0;
  flex: 1;
}

.row-copy__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.row-copy strong {
  display: block;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.row-time {
  color: #94a3b8;
  font-size: 0.76rem;
  font-weight: 700;
  white-space: nowrap;
  flex-shrink: 0;
}

.row-sub {
  margin-top: 2px;
  font-size: 0.78rem;
}

.row-preview {
  margin-top: 8px;
  font-size: 0.88rem;
  line-height: 1.35;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.row-preview__flag {
  display: inline-flex;
  align-items: center;
  margin-right: 8px;
  padding: 2px 8px;
  border-radius: 999px;
  background: #eff6ff;
  color: #2563eb;
  font-size: 0.72rem;
  font-weight: 700;
}

.row-footer {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 0.74rem;
}

.row-dot {
  width: 4px;
  height: 4px;
  border-radius: 999px;
  background: #cbd5e1;
}

.chat-main {
  min-height: min(760px, calc(100vh - (var(--catnav-top, 72px) + var(--catnav-height, 56px) + 28px)));
  display: flex;
  flex-direction: column;
}

.chat-empty-title {
  margin: 0 0 6px;
  font-size: 1.18rem;
  font-weight: 800;
  color: #0f172a;
}

.chat-header,
.composer {
  padding: 20px 24px;
}

.chat-context {
  margin: 0 24px 14px;
  border: 1px solid #dbe4ee;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fbff 0%, #ffffff 100%);
  padding: 14px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
}

.chat-context__label {
  margin: 0 0 4px;
  color: #64748b;
  font-size: 0.76rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.chat-context__title {
  margin: 0;
  color: #0f172a;
  font-size: 1rem;
  font-weight: 800;
}

.chat-context__desc {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 0.88rem;
}

.chat-context__meta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.chat-context__badge {
  border-radius: 999px;
  padding: 6px 10px;
  background: #dcfce7;
  color: #166534;
  font-size: 0.76rem;
  font-weight: 800;
}

.chat-context__badge--soft {
  background: #e0f2fe;
  color: #075985;
}

.messages-panel {
  flex: 1;
  padding: 0 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;
  background: linear-gradient(180deg, rgba(255,255,255,0.2) 0%, rgba(248,250,252,0.66) 100%);
}

.chat-header-main {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.header-avatar {
  width: 40px;
  height: 40px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  text-align: center;
  background: linear-gradient(180deg, #dcfce7 0%, #bbf7d0 100%);
  color: #065f46;
  font-weight: 800;
  border: 1px solid #86efac;
}

.header-sub {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 0.86rem;
}

.header-badge {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  display: inline-block;
  background: #cbd5e1;
}

.header-badge--live {
  background: #22c55e;
  box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.14);
}

.header-divider {
  width: 1px;
  height: 14px;
  background: #dbe4ee;
  display: inline-block;
}

.message-bubble {
  max-width: 75%;
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  position: relative;
}

.message-bubble.own {
  margin-left: auto;
  background: #dff8f2;
  border-color: #bcebdd;
}

.message-bubble.own::after {
  content: '';
  position: absolute;
  right: -6px;
  top: 16px;
  width: 12px;
  height: 12px;
  background: #dff8f2;
  transform: rotate(45deg);
  border-right: 1px solid #bcebdd;
  border-top: 1px solid #bcebdd;
}

.message-bubble:not(.own)::after {
  content: '';
  position: absolute;
  left: -6px;
  top: 16px;
  width: 12px;
  height: 12px;
  background: #f8fafc;
  transform: rotate(45deg);
  border-left: 1px solid #e2e8f0;
  border-bottom: 1px solid #e2e8f0;
}

.typing-bubble {
  width: fit-content;
  max-width: 75%;
  padding: 12px 14px;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #94a3b8;
  animation: typingPulse 1s infinite ease-in-out;
}

.typing-dot:nth-child(2) {
  animation-delay: 0.12s;
}

.typing-dot:nth-child(3) {
  animation-delay: 0.24s;
}

.message-author,
.message-content {
  margin: 0;
}

.message-author {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 6px;
}

.message-content {
  white-space: pre-wrap;
}

.message-time {
  margin: 8px 0 0;
  font-size: 12px;
}

.message-state {
  margin-left: 8px;
  color: #1fb981;
  font-weight: 600;
}

.order-alert {
  margin: 0 24px 12px;
  padding: 14px 16px;
  border-radius: 16px;
  background: #f0fdf4;
  border: 1px solid #86efac;
  color: #14532d;
}

.order-alert strong {
  display: block;
  margin-bottom: 6px;
}

.order-alert p {
  margin: 0;
  line-height: 1.45;
}

.chat-empty,
.sidebar-empty {
  padding: 20px 8px;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
}

.composer {
  border-top: 1px solid var(--rm-border);
}

.composer-topbar {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 10px;
  color: #64748b;
  font-size: 0.82rem;
}

.composer-hint--muted {
  color: #94a3b8;
}

.composer textarea {
  resize: vertical;
  min-height: 96px;
}

.composer-box {
  display: flex;
  gap: 12px;
  align-items: end;
}

.composer-attach,
.composer-send {
  min-height: 44px;
  border-radius: 14px;
  border: 1px solid #dbe4ee;
  background: #ffffff;
  color: #0f172a;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.composer-attach svg {
  width: 18px;
  height: 18px;
}

.composer-send {
  background: linear-gradient(135deg, #1fb981 0%, #10b981 100%);
  color: #ffffff;
  font-weight: 800;
  border: 0;
  flex: 0 0 160px;
}

.composer-send:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.composer-chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  padding: 4px 10px;
  background: #eefbf7;
  color: #0f766e;
  font-size: 0.76rem;
  font-weight: 700;
}

@keyframes typingPulse {
  0%, 80%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  40% {
    transform: translateY(-3px);
    opacity: 1;
  }
}

.composer textarea:focus {
  outline: none;
  border-color: #34d399;
  box-shadow: 0 0 0 3px rgba(52, 211, 153, 0.16);
}

.primary-btn,
.ghost-btn,
.link-btn {
  border-radius: 999px;
  padding: 10px 16px;
  font-weight: 700;
  text-decoration: none;
}

.primary-btn {
  background: #1fb981;
  color: #fff;
  border: none;
}

.primary-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ghost-btn {
  background: transparent;
  border: 1px solid var(--rm-border);
  color: var(--rm-text);
}

.error-text {
  margin: 0;
  color: #dc2626;
}

.counter-text {
  margin: 0;
  color: #64748b;
  font-size: 12px;
}

.conversations-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.row-main {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.row-avatar {
  width: 44px;
  height: 44px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  text-align: center;
  background: linear-gradient(180deg, #dcfce7 0%, #bbf7d0 100%);
  color: #065f46;
  font-weight: 800;
  border: 1px solid #86efac;
  flex-shrink: 0;
}

.row-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.row-title {
  margin: 0;
  font-weight: 700;
  color: #0f172a;
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

@media (max-width: 960px) {
  .chat-page {
    margin-top: 0;
    padding: 0;
    min-height: 100dvh;
  }

  .chat-shell {
    grid-template-columns: 1fr;
    gap: 0;
    min-height: 100dvh;
  }

  .chat-shell--mobile-conversation {
    min-height: 100dvh;
  }

  .chat-sidebar {
    position: static;
    max-height: none;
    border-radius: 0;
    border-left: 0;
    border-right: 0;
    border-top: 0;
  }

  .chat-main {
    min-height: 100dvh;
    height: 100dvh;
    border-radius: 0;
    box-shadow: none;
    overflow: hidden;
  }

  .chat-header,
  .composer {
    padding: 14px;
  }

  .chat-header {
    position: sticky;
    top: 0;
    z-index: 3;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(10px);
    border-bottom: 1px solid var(--rm-border);
  }

  .chat-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .header-meta {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .sidebar-actions {
    width: 100%;
    justify-content: flex-start;
    flex-wrap: wrap;
  }

  .composer-topbar {
    flex-direction: column;
    align-items: flex-start;
  }

  .messages-panel {
    padding: 0 14px 14px;
    min-height: 0;
  }

  .chat-context {
    margin: 0 14px 12px;
    flex-direction: column;
    align-items: flex-start;
  }

  .composer-box {
    grid-template-columns: 44px 1fr;
  }

  .composer-send {
    grid-column: 1 / -1;
    width: 100%;
  }

  .order-alert {
    margin: 0 14px 12px;
  }

  .message-bubble {
    max-width: 90%;
  }

  .mobile-back-btn {
    margin: 0 14px 12px;
    width: calc(100% - 28px);
    border: 1px solid #cbd5e1;
    border-radius: 14px;
    background: #f8fafc;
    color: #0f172a;
    font-weight: 700;
    padding: 11px 14px;
  }

  .composer {
    position: sticky;
    bottom: 0;
    z-index: 3;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(10px);
    padding-bottom: calc(14px + env(safe-area-inset-bottom, 0px));
  }

  .chat-context {
    margin: 0 14px 12px;
    padding: 12px 14px;
  }

  .chat-shell--mobile-conversation .chat-sidebar {
    display: none;
  }
}
</style>
