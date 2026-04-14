export type UiMessageKind = 'success' | 'error' | 'info'

export type UiMessage = {
  id: string
  text: string
  kind: UiMessageKind
  createdAt: number
}

function randomId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
}

export function useUiMessages() {
  const messages = useState<UiMessage[]>('ui-messages', () => [])

  const remove = (id: string) => {
    messages.value = messages.value.filter((message) => message.id !== id)
  }

  const push = (text: string, kind: UiMessageKind = 'info', durationMs = 3200) => {
    const message: UiMessage = {
      id: randomId(),
      text,
      kind,
      createdAt: Date.now()
    }

    messages.value = [...messages.value, message]

    if (process.client && durationMs > 0) {
      window.setTimeout(() => remove(message.id), durationMs)
    }

    return message.id
  }

  return {
    messages,
    push,
    remove,
    success: (text: string, durationMs?: number) => push(text, 'success', durationMs),
    error: (text: string, durationMs?: number) => push(text, 'error', durationMs),
    info: (text: string, durationMs?: number) => push(text, 'info', durationMs)
  }
}
