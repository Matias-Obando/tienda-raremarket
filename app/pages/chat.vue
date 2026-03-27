<template>
  <div class="chat-page">
    <div class="chat-shell">
      <aside class="chat-sidebar">
        <div class="sidebar-top">
          <div>
            <p class="eyebrow">Mensajes</p>
            <h1>Tu bandeja</h1>
          </div>
          <button class="ghost-btn" type="button" @click="loadData">Actualizar</button>
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

        <div v-if="loadingConversations" class="sidebar-empty">Cargando conversaciones...</div>
        <div v-else-if="!conversations.length" class="sidebar-empty">
          Aún no tienes conversaciones. Registra dos usuarios y abre un chat desde un producto o desde aquí.
        </div>

        <button
          v-for="conversation in conversations"
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
        <div v-if="!sessionReady" class="chat-empty">
          Cargando tu sesión...
        </div>

        <div v-else-if="!sessionUser" class="chat-empty">
          <p>Necesitas iniciar sesión para usar el chat.</p>
          <NuxtLink class="primary-btn link-btn" :to="{ path: '/auth', query: { mode: 'login', redirect: '/chat' } }">
            Ir a login
          </NuxtLink>
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
              v-for="message in messages"
              :key="message.id"
              class="message-bubble"
              :class="{ own: message.senderId === sessionUser?.id }"
            >
              <p class="message-author">{{ message.senderName }}</p>
              <p class="message-content">{{ message.content }}</p>
              <p class="message-time">{{ formatDate(message.createdAt) }}</p>
            </article>
          </div>

          <form class="composer" @submit.prevent="sendMessage">
            <textarea
              v-model="draftMessage"
              rows="3"
              maxlength="1000"
              placeholder="Escribe tu mensaje"
            />
            <div class="composer-actions">
              <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
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
  createdAt: string
}

const config = useRuntimeConfig()
const route = useRoute()
const router = useRouter()
const { sessionUser, loadSessionUser, storageEventName } = useSessionUser()

const sessionReady = ref(false)
const users = ref<ChatUser[]>([])
const conversations = ref<Conversation[]>([])
const messages = ref<Message[]>([])
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

const recipientOptions = computed(() =>
  users.value.filter((user) => user.id !== sessionUser.value?.id)
)

const selectedConversation = computed(() =>
  conversations.value.find((conversation) => conversation.id === selectedConversationId.value) ?? null
)

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
  users.value = await $fetch<ChatUser[]>(`${config.public.API_BASE_URL}/api/users`)
}

async function loadConversations() {
  if (!sessionUser.value) {
    conversations.value = []
    return
  }

  loadingConversations.value = true
  try {
    conversations.value = await $fetch<Conversation[]>(`${config.public.API_BASE_URL}/api/chat/conversations`, {
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
    messages.value = await $fetch<Message[]>(`${config.public.API_BASE_URL}/api/chat/conversations/${conversationId}/messages`, {
      params: { userId: sessionUser.value.id }
    })
  } finally {
    loadingMessages.value = false
  }
}

async function selectConversation(conversationId: string) {
  selectedConversationId.value = conversationId
  await loadMessages(conversationId)
  if (sessionUser.value) {
    await $fetch(`${config.public.API_BASE_URL}/api/chat/conversations/${conversationId}/read`, {
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
    const conversation = await $fetch<Conversation>(`${config.public.API_BASE_URL}/api/chat/conversations`, {
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
  } catch (error: any) {
    errorMessage.value = error?.data?.message || 'No se pudo abrir la conversación.'
  } finally {
    startingConversation.value = false
  }
}

async function sendMessage() {
  if (!sessionUser.value || !selectedConversationId.value || !draftMessage.value.trim()) {
    return
  }

  errorMessage.value = ''
  sendingMessage.value = true
  try {
    await $fetch(`${config.public.API_BASE_URL}/api/chat/conversations/${selectedConversationId.value}/messages`, {
      method: 'POST',
      body: {
        senderId: sessionUser.value.id,
        content: draftMessage.value
      }
    })
    draftMessage.value = ''
    await loadMessages(selectedConversationId.value)
    await loadConversations()
  } catch (error: any) {
    errorMessage.value = error?.data?.message || 'No se pudo enviar el mensaje.'
  } finally {
    sendingMessage.value = false
  }
}

async function loadData() {
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
</script>

<style scoped>
.chat-page {
  padding: 24px 16px 48px;
}

.chat-shell {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 20px;
}

.chat-sidebar,
.chat-main {
  background: #fff;
  border: 1px solid var(--rm-border);
  border-radius: 22px;
  box-shadow: var(--rm-shadow);
}

.chat-sidebar {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
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

.eyebrow {
  margin: 0 0 6px;
  color: #0f766e;
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
  background: #0f766e;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.chat-main {
  min-height: 680px;
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

.chat-empty,
.sidebar-empty {
  padding: 20px 8px;
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
  background: #0f766e;
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

@media (max-width: 960px) {
  .chat-shell {
    grid-template-columns: 1fr;
  }
}
</style>
