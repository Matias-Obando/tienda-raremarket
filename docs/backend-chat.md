# Chat y conversaciones

Este módulo cubre creación de conversaciones, listado de mensajes, envío, lectura y borrado.

## Endpoints

### `POST /api/chat/conversations`

Crea o reutiliza una conversación entre comprador y vendedor para un artículo.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue) y [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue)

Request:

```json
{
  "itemId": "item-123",
  "buyerId": "11111111-1111-1111-1111-111111111111",
  "sellerId": "22222222-2222-2222-2222-222222222222"
}
```

Response:

```json
{
  "id": "7e1a3cc5-4ce7-4d26-a7d5-4b5c04a8c9d0",
  "itemId": "item-123",
  "buyerId": "11111111-1111-1111-1111-111111111111",
  "buyerName": "Ana Perez",
  "sellerId": "22222222-2222-2222-2222-222222222222",
  "sellerName": "Carlos Ruiz",
  "counterpartId": "22222222-2222-2222-2222-222222222222",
  "counterpartName": "Carlos Ruiz",
  "lastMessage": "Hola, sigue disponible?",
  "lastMessageAt": "2026-05-14T09:40:00Z",
  "unreadCount": 1,
  "updatedAt": "2026-05-14T09:40:00Z"
}
```

### `GET /api/chat/conversations`

Lista conversaciones de un usuario.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue) y [frontend/app/composables/useUnreadChatCount.ts](../frontend/app/composables/useUnreadChatCount.ts)

Query params:

- `userId=...`

Response:

```json
[
  {
    "id": "7e1a3cc5-4ce7-4d26-a7d5-4b5c04a8c9d0",
    "itemId": "item-123",
    "buyerId": "11111111-1111-1111-1111-111111111111",
    "buyerName": "Ana Perez",
    "sellerId": "22222222-2222-2222-2222-222222222222",
    "sellerName": "Carlos Ruiz",
    "counterpartId": "22222222-2222-2222-2222-222222222222",
    "counterpartName": "Carlos Ruiz",
    "lastMessage": "Hola, sigue disponible?",
    "lastMessageAt": "2026-05-14T09:40:00Z",
    "unreadCount": 1,
    "updatedAt": "2026-05-14T09:40:00Z"
  }
]
```

### `GET /api/chat/conversations/{conversationId}/messages`

Devuelve los mensajes de una conversación.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue)

Query params:

- `userId=...`

Response:

```json
[
  {
    "id": "msg-1",
    "conversationId": "7e1a3cc5-4ce7-4d26-a7d5-4b5c04a8c9d0",
    "senderId": "11111111-1111-1111-1111-111111111111",
    "senderName": "Ana Perez",
    "content": "Hola, sigue disponible?",
    "read": true,
    "createdAt": "2026-05-14T09:40:00Z"
  }
]
```

### `POST /api/chat/conversations/{conversationId}/messages`

Envía un mensaje nuevo.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue)

Request:

```json
{
  "senderId": "11111111-1111-1111-1111-111111111111",
  "content": "Hola, sigue disponible?"
}
```

Response:

```json
{
  "id": "msg-1",
  "conversationId": "7e1a3cc5-4ce7-4d26-a7d5-4b5c04a8c9d0",
  "senderId": "11111111-1111-1111-1111-111111111111",
  "senderName": "Ana Perez",
  "content": "Hola, sigue disponible?",
  "read": false,
  "createdAt": "2026-05-14T09:40:00Z"
}
```

### `PATCH /api/chat/conversations/{conversationId}/read`

Marca como leídos los mensajes visibles para un usuario.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue)

Query params:

- `userId=...`

Response:

```json
{
  "updated": 3
}
```

### `DELETE /api/chat/conversations/{conversationId}`

Elimina una conversación y sus mensajes.

Frontend que lo usa: [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue)

Query params:

- `userId=...`

Response:

```json
{
  "status": "deleted"
}
```

## Servicios que intervienen

- [backend/src/main/java/com/raremarket/backend/controller/ChatController.java](../backend/src/main/java/com/raremarket/backend/controller/ChatController.java)
- [backend/src/main/java/com/raremarket/backend/service/ChatService.java](../backend/src/main/java/com/raremarket/backend/service/ChatService.java)
- [backend/src/main/java/com/raremarket/backend/repository/ConversationRepository.java](../backend/src/main/java/com/raremarket/backend/repository/ConversationRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/MessageRepository.java](../backend/src/main/java/com/raremarket/backend/repository/MessageRepository.java)
- [backend/src/main/java/com/raremarket/backend/repository/OrderConversationRepository.java](../backend/src/main/java/com/raremarket/backend/repository/OrderConversationRepository.java)

## Cómo funciona en el frontend

- [frontend/app/pages/chat.vue](../frontend/app/pages/chat.vue) renderiza la bandeja, selecciona conversaciones y mantiene la vista móvil/desktop.
- [frontend/app/composables/useUnreadChatCount.ts](../frontend/app/composables/useUnreadChatCount.ts) refresca el contador global de mensajes no leídos.
- [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue) construye la ruta al chat con `itemId`, `sellerId` y `sellerName`.