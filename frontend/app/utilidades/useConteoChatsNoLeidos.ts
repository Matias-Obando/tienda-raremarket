type ConversationSummary = {
  unreadCount?: number
}

const UNREAD_CHAT_COUNT_KEY = 'chat:unread-count'
const UNREAD_CHAT_POLLING_KEY = 'chat:unread-polling'

export function useUnreadChatCount() {
  const config = useRuntimeConfig()
  const { sessionUser } = useSessionUser()
  const unreadChatCount = useState<number>(UNREAD_CHAT_COUNT_KEY, () => 0)
  const pollingHandle = useState<number | null>(UNREAD_CHAT_POLLING_KEY, () => null)

  const stopUnreadChatPolling = () => {
    if (!process.client) {
      return
    }

    if (pollingHandle.value !== null) {
      window.clearInterval(pollingHandle.value)
      pollingHandle.value = null
    }
  }

  const refreshUnreadChatCount = async () => {
    if (!process.client) {
      return unreadChatCount
    }

    const user = sessionUser.value
    if (!user?.id || !user.token) {
      unreadChatCount.value = 0
      return unreadChatCount
    }

    try {
      const conversations = await $fetch<ConversationSummary[]>(`${config.public.API_BASE_URL}/chat/conversations`, {
        headers: {
          Authorization: `Bearer ${user.token}`
        },
        params: {
          userId: user.id
        }
      })

      unreadChatCount.value = conversations.reduce((total, conversation) => {
        return total + Number(conversation.unreadCount ?? 0)
      }, 0)
    } catch {
      unreadChatCount.value = 0
    }

    return unreadChatCount
  }

  const startUnreadChatPolling = (intervalMs = 30000) => {
    if (!process.client || pollingHandle.value !== null) {
      return
    }

    void refreshUnreadChatCount()
    pollingHandle.value = window.setInterval(() => {
      void refreshUnreadChatCount()
    }, intervalMs)
  }

  return {
    unreadChatCount,
    refreshUnreadChatCount,
    startUnreadChatPolling,
    stopUnreadChatPolling
  }
}