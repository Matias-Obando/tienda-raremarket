<template>
  <div class="chat-page">
    <div class="chat-shell">
      <aside class="chat-sidebar">
        <div class="sidebar-top">
          <div>
            <p class="eyebrow">Mensajes</p>
            <h1>Tu bandeja</h1>
          </div>
          <div class="sidebar-actions">
            <button class="ghost-btn" type="button" @click="demoMode = !demoMode">
              {{ demoMode ? 'Modo real' : 'Chat simulado' }}
            </button>
            <button class="ghost-btn" type="button" @click="loadData">Actualizar</button>
          </div>
        </div>

        <div v-if="itemId" class="start-card">
          <p class="start-label">Nuevo chat sobre</p>
          <p class="start-title">{{ itemTitle || `Producto ${itemId}` }}</p>
          <label class="field">
            <span>Hablar con</span>
            <select v-model="selectedSellerId">
              <option value="">Selecciona un usuario</option>
              <option v-for="user in recipientOptions" :key="user.id" :value="user.id">
                {{ user.username }} · {{ user.email }}
              </option>
            </select>
          </label>
          <button class="primary-btn" type="button" :disabled="!selectedSellerId || startingConversation" @click="startConversation">
            {{ startingConversation ? 'Abriendo...' : 'Abrir conversación' }}
          </button>
        </div>

        <div v-if="!demoMode && loadingConversations" class="sidebar-empty">Cargando conversaciones...</div>
        <div v-else-if="!activeConversations.length" class="sidebar-empty">
          Aún no tienes conversaciones. Registra dos usuarios y abre un chat desde un producto o desde aquí.
        </div>

        <button
          v-for="conversation in activeConversations"
          :key="conversation.id"
          type="button"
          class="conversation-row"
          :class="{ active: conversation.id === selectedConversationId }"
          @click="selectConversation(conversation.id)"
        >
          <div class="row-main">
            <strong>{{ conversation.counterpartName }}</strong>
            <span v-if="conversation.unreadCount" class="pill">{{ conversation.unreadCount }}</span>
          </div>
          <div class="row-sub">Item {{ conversation.itemId }}</div>
          <div class="row-preview">{{ conversation.lastMessage || 'Sin mensajes aún' }}</div>
        </button>
      </aside>

      <section class="chat-main">
        <div v-if="!demoMode && !sessionReady" class="chat-empty">
          Cargando tu sesión...
        </div>

        <div v-else-if="!demoMode && !sessionUser" class="chat-empty">
          <p>Necesitas iniciar sesión para usar el chat.</p>
          <NuxtLink class="primary-btn link-btn" :to="{ path: '/auth', query: { mode: 'login', redirect: '/chat' } }">
            Ir a login
          </NuxtLink>
          <button class="ghost-btn" type="button" @click="demoMode = true">Probar chat simulado</button>
        </div>

        <div v-else-if="!selectedConversation" class="chat-empty">
          <p>Selecciona una conversación para ver mensajes.</p>
          <p v-if="itemId">También puedes abrir un chat nuevo usando el bloque lateral.</p>
        </div>

        <template v-else>
          <header class="chat-header">
            <div>
              <p class="eyebrow">Conversación</p>
              <h2>{{ selectedConversation.counterpartName }}</h2>
            </div>
            <div class="header-meta">Item {{ selectedConversation.itemId }}</div>
          </header>

          <div class="messages-panel">
            <div v-if="loadingMessages" class="chat-empty">Cargando mensajes...</div>
            <div v-else-if="!messages.length" class="chat-empty">
              Todavía no hay mensajes. Escribe el primero.
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
            <textarea
              v-model="draftMessage"
              rows="3"
              maxlength="1000"
              placeholder="Escribe tu mensaje"
              @keydown.enter.exact.prevent="sendMessage"
            />
            <div class="composer-actions">
              <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
              <p v-else class="counter-text">{{ draftMessage.trim().length }}/1000</p>
              <button class="primary-btn" type="submit" :disabled="sendingMessage || !draftMessage.trim()">
                {{ sendingMessage ? 'Enviando...' : 'Enviar' }}
              </button>
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
const uiMessages = useUiMessages()

const sessionReady = ref(false)
const demoMode = ref(false)
const users = ref<ChatUser[]>([])
const conversations = ref<Conversation[]>([])
const messages = ref<Message[]>([])
const messageRows = ref<HTMLElement[] | null>(null)
const loadingConversations = ref(false)
const loadingMessages = ref(false)
const startingConversation = ref(false)
const sendingMessage = ref(false)
const errorMessage = ref('')
const selectedConversationId = ref('')
const selectedSellerId = ref('')
const draftMessage = ref('')

const itemId = computed(() => typeof route.query.itemId === 'string' ? route.query.itemId : '')
const itemTitle = computed(() => typeof route.query.itemTitle === 'string' ? route.query.itemTitle : '')

const demoConversations = ref<Conversation[]>([
  {
    id: 'demo-conv-1',
    itemId: 'demo-item-1',
    counterpartId: 'demo-seller-1',
    counterpartName: 'Laura Vintage',
    unreadCount: 1,
    lastMessage: 'Si, sigue disponible.'
  },
  {
    id: 'demo-conv-2',
    itemId: 'demo-item-2',
    counterpartId: 'demo-seller-2',
    counterpartName: 'Alex Closet',
    unreadCount: 0,
    lastMessage: 'Te puedo hacer envio hoy.'
  }
])

const demoMessages = ref<MessageMap>({
  'demo-conv-1': [
    {
      id: 'demo-msg-1',
      conversationId: 'demo-conv-1',
      senderId: 'demo-seller-1',
      senderName: 'Laura Vintage',
      content: 'Hola! Te interesa la chaqueta?',
      isRead: true,
      createdAt: new Date(Date.now() - 1000 * 60 * 16).toISOString()
    },
    {
      id: 'demo-msg-2',
      conversationId: 'demo-conv-1',
      senderId: 'guest-user',
      senderName: 'Tu',
      content: 'Si, sigue disponible?',
      isRead: true,
      createdAt: new Date(Date.now() - 1000 * 60 * 13).toISOString()
    },
    {
      id: 'demo-msg-3',
      conversationId: 'demo-conv-1',
      senderId: 'demo-seller-1',
      senderName: 'Laura Vintage',
      content: 'Si, sigue disponible.',
      isRead: false,
      createdAt: new Date(Date.now() - 1000 * 60 * 9).toISOString()
    }
  ],
  'demo-conv-2': [
    {
      id: 'demo-msg-4',
      conversationId: 'demo-conv-2',
      senderId: 'demo-seller-2',
      senderName: 'Alex Closet',
      content: 'Te puedo hacer envio hoy.',
      isRead: true,
      createdAt: new Date(Date.now() - 1000 * 60 * 35).toISOString()
    }
  ]
})

const recipientOptions = computed(() =>
  users.value.filter((user) => user.id !== sessionUser.value?.id)
)

const selectedConversation = computed(() =>
  activeConversations.value.find((conversation) => conversation.id === selectedConversationId.value) ?? null
)

const activeConversations = computed(() =>
  demoMode.value ? demoConversations.value : conversations.value
)

const activeMessages = computed(() => {
  if (demoMode.value) {
    return demoMessages.value[selectedConversationId.value] ?? []
  }
  return messages.value
})

const orderedMessages = computed(() =>
  [...activeMessages.value].sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime())
)

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

async function loadUsers() {
  users.value = await $fetch<ChatUser[]>(`${config.public.API_BASE_URL}/users`)
}

async function loadConversations() {
  if (!sessionUser.value) {
    conversations.value = []
    return
  }

  loadingConversations.value = true
  try {
    conversations.value = await $fetch<Conversation[]>(`${config.public.API_BASE_URL}/chat/conversations`, {
      params: { userId: sessionUser.value.id }
    })
  } finally {
    loadingConversations.value = false
  }
}

async function loadMessages(conversationId: string) {
  if (!sessionUser.value) {
    return
  }

  loadingMessages.value = true
  try {
    messages.value = await $fetch<Message[]>(`${config.public.API_BASE_URL}/chat/conversations/${conversationId}/messages`, {
      params: { userId: sessionUser.value.id }
    })
    await scrollToBottom()
  } finally {
    loadingMessages.value = false
  }
}

async function selectConversation(conversationId: string) {
  if (demoMode.value) {
    selectedConversationId.value = conversationId
    demoConversations.value = demoConversations.value.map((conversation) =>
      conversation.id === conversationId ? { ...conversation, unreadCount: 0 } : conversation
    )
    await scrollToBottom()
    return
  }

  selectedConversationId.value = conversationId
  await loadMessages(conversationId)
  if (sessionUser.value) {
    await $fetch(`${config.public.API_BASE_URL}/chat/conversations/${conversationId}/read`, {
      method: 'PATCH',
      params: { userId: sessionUser.value.id }
    })
    await loadConversations()
  }
}

async function startConversation() {
  if (!sessionUser.value || !itemId.value || !selectedSellerId.value) {
    return
  }

  errorMessage.value = ''
  startingConversation.value = true
  try {
    const conversation = await $fetch<Conversation>(`${config.public.API_BASE_URL}/chat/conversations`, {
      method: 'POST',
      body: {
        itemId: itemId.value,
        buyerId: sessionUser.value.id,
        sellerId: selectedSellerId.value
      }
    })

    await loadConversations()
    await router.replace({ path: '/chat', query: {} })
    selectedConversationId.value = conversation.id
    await loadMessages(conversation.id)
    uiMessages.success('Conversacion iniciada correctamente.')
  } catch (error: any) {
    errorMessage.value = parseApiError(error, 'No se pudo abrir la conversacion.')
    uiMessages.error(errorMessage.value)
  } finally {
    startingConversation.value = false
  }
}

async function sendMessage() {
  if (!selectedConversationId.value || !draftMessage.value.trim()) {
    return
  }

  if (demoMode.value) {
    const content = draftMessage.value.trim()
    const conversationId = selectedConversationId.value
    const timestamp = new Date().toISOString()
    const currentConversation = demoConversations.value.find((conversation) => conversation.id === conversationId)

    const outgoing: Message = {
      id: `demo-local-${Date.now()}`,
      conversationId,
      senderId: sessionUser.value?.id ?? 'guest-user',
      senderName: sessionUser.value?.username ?? 'Tu',
      content,
      isRead: false,
      createdAt: timestamp
    }

    demoMessages.value[conversationId] = [...(demoMessages.value[conversationId] ?? []), outgoing]
    draftMessage.value = ''
    demoConversations.value = demoConversations.value.map((conversation) =>
      conversation.id === conversationId ? { ...conversation, lastMessage: content } : conversation
    )
    await scrollToBottom()

    if (process.client && currentConversation) {
      window.setTimeout(async () => {
        const replyContent = 'Perfecto, si quieres lo cerramos hoy mismo.'
        const incoming: Message = {
          id: `demo-reply-${Date.now()}`,
          conversationId,
          senderId: currentConversation.counterpartId,
          senderName: currentConversation.counterpartName,
          content: replyContent,
          isRead: false,
          createdAt: new Date().toISOString()
        }

        demoMessages.value[conversationId] = [...(demoMessages.value[conversationId] ?? []), incoming]
        demoConversations.value = demoConversations.value.map((conversation) =>
          conversation.id === conversationId
            ? { ...conversation, lastMessage: replyContent, unreadCount: conversation.unreadCount + 1 }
            : conversation
        )
        await scrollToBottom()
      }, 850)
    }

    return
  }

  if (!sessionUser.value) {
    return
  }

  errorMessage.value = ''
  sendingMessage.value = true
  try {
    await $fetch(`${config.public.API_BASE_URL}/chat/conversations/${selectedConversationId.value}/messages`, {
      method: 'POST',
      body: {
        senderId: sessionUser.value.id,
        content: draftMessage.value.trim()
      }
    })
    draftMessage.value = ''
    await loadMessages(selectedConversationId.value)
    await loadConversations()
  } catch (error: any) {
    errorMessage.value = parseApiError(error, 'No se pudo enviar el mensaje.')
    uiMessages.error(errorMessage.value)
  } finally {
    sendingMessage.value = false
  }
}

async function loadData() {
  if (demoMode.value) {
    if (!selectedConversationId.value && demoConversations.value.length) {
      selectedConversationId.value = demoConversations.value[0].id
    }
    return
  }

  await loadUsers()
  await loadConversations()
  if (!selectedConversationId.value && conversations.value.length) {
    selectedConversationId.value = conversations.value[0].id
    await loadMessages(selectedConversationId.value)
  }
}

onMounted(async () => {
  loadSessionUser()
  sessionReady.value = true

  window.addEventListener(storageEventName, syncSession)
  window.addEventListener('storage', syncSession)

  await loadData()
})

onBeforeUnmount(() => {
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
})

watch(orderedMessages, async () => {
  await scrollToBottom()
})

watch(demoMode, async (enabled) => {
  selectedConversationId.value = ''
  messages.value = []

  if (enabled) {
    uiMessages.info('Modo chat simulado activado.')
  } else {
    uiMessages.info('Modo chat real activado.')
  }

  await loadData()
})
</script>

<style scoped>
.chat-page {
  padding: 18px 16px 48px;
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
  padding: 14px;
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
  min-width: 26px;
  height: 26px;
  border-radius: 999px;
  background: #1fb981;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.chat-main {
  min-height: min(760px, calc(100vh - (var(--catnav-top, 72px) + var(--catnav-height, 56px) + 28px)));
  display: flex;
  flex-direction: column;
}

.chat-header,
.composer {
  padding: 20px 24px;
}

.messages-panel {
  flex: 1;
  padding: 8px 24px 24px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;
  background: linear-gradient(180deg, rgba(255,255,255,0.2) 0%, rgba(248,250,252,0.66) 100%);
}

.message-bubble {
  max-width: 75%;
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}

.message-bubble.own {
  margin-left: auto;
  background: #dff8f2;
  border-color: #bcebdd;
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

.composer textarea {
  resize: vertical;
  min-height: 96px;
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

@media (max-width: 960px) {
  .chat-page {
    margin-top: 10px;
    padding-left: 10px;
    padding-right: 10px;
  }

  .chat-shell {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .chat-sidebar {
    position: static;
    max-height: none;
  }

  .chat-main {
    min-height: 62vh;
  }

  .chat-header,
  .composer {
    padding: 14px;
  }

  .messages-panel {
    padding: 8px 14px 14px;
  }

  .message-bubble {
    max-width: 90%;
  }
}
</style>
