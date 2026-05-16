export type SessionUser = {
  id: string
  username: string
  email: string
  role?: string
  token?: string
  avatarUrl?: string
  location?: string
  phone?: string
  bio?: string
}

const STORAGE_KEY = 'closely:session-user'
const EVENT_NAME = 'closely:session-user-updated'

export function useSessionUser() {
  const sessionUser = useState<SessionUser | null>('session-user', () => null)

  const loadSessionUser = () => {
    if (!process.client) {
      return sessionUser
    }

    try {
      const raw = localStorage.getItem(STORAGE_KEY)
      sessionUser.value = raw ? JSON.parse(raw) : null
    } catch {
      sessionUser.value = null
    }

    return sessionUser
  }

  const saveSessionUser = (user: SessionUser) => {
    sessionUser.value = user

    if (process.client) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
      window.dispatchEvent(new CustomEvent(EVENT_NAME))
    }

    return sessionUser
  }

  const clearSessionUser = () => {
    sessionUser.value = null

    if (process.client) {
      localStorage.removeItem(STORAGE_KEY)
      window.dispatchEvent(new CustomEvent(EVENT_NAME))
    }
  }

  return {
    sessionUser,
    loadSessionUser,
    saveSessionUser,
    clearSessionUser,
    storageEventName: EVENT_NAME
  }
}
