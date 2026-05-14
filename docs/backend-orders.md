# Pedidos y checkout

Este módulo cubre la creación de pedidos, el historial de compras y ventas, el cambio de estados y la relación entre pedido y chat.

## Endpoints

### `POST /api/orders`

Crea un pedido autenticado y marca el artículo como no disponible.

Frontend que lo usa: [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue)

Request:

```json
{
  "itemId": "item-123",
  "deliveryMethod": "shipping",
  "shippingFullName": "Ana Perez",
  "shippingPhone": "+34 600 000 000",
  "shippingAddressLine1": "Calle Mayor 1",
  "shippingCity": "Madrid",
  "shippingPostalCode": "28001",
  "shippingCountry": "Espana",
  "cardNumber": "4242424242424242",
  "cardHolder": "Ana Perez",
  "cardExpiry": "12/28",
  "cardCvv": "123"
}
```

Response:

```json
{
  "id": "order-456",
  "itemId": "item-123",
  "buyerId": "buyer-1",
  "sellerId": "seller-1",
  "itemTitle": "Chaqueta vaquera oversize",
  "itemImage": "https://.../items/item-123/main.jpg",
  "amountEur": 24.99,
  "deliveryMethod": "shipping",
  "shippingFullName": "Ana Perez",
  "shippingPhone": "+34 600 000 000",
  "shippingAddressLine1": "Calle Mayor 1",
  "shippingCity": "Madrid",
  "shippingPostalCode": "28001",
  "shippingCountry": "Espana",
  "pickupCity": null,
  "pickupNotes": null,
  "paymentBrand": "Visa",
  "paymentLast4": "4242",
  "status": "PREPARANDO_ENVIO",
  "createdAt": "2026-05-14T10:10:00Z",
  "updatedAt": "2026-05-14T10:10:00Z"
}
```

### `GET /api/orders`

Lista pedidos del usuario autenticado.

Frontend que lo usa: [frontend/app/pages/perfil.vue](../frontend/app/pages/perfil.vue)

Query params:

- `role=buyer` o `role=seller`

Response:

```json
[
  {
    "id": "order-456",
    "itemId": "item-123",
    "buyerId": "buyer-1",
    "sellerId": "seller-1",
    "itemTitle": "Chaqueta vaquera oversize",
    "itemImage": "https://.../items/item-123/main.jpg",
    "amountEur": 24.99,
    "deliveryMethod": "shipping",
    "shippingFullName": "Ana Perez",
    "shippingPhone": "+34 600 000 000",
    "shippingAddressLine1": "Calle Mayor 1",
    "shippingCity": "Madrid",
    "shippingPostalCode": "28001",
    "shippingCountry": "Espana",
    "pickupCity": null,
    "pickupNotes": null,
    "paymentBrand": "Visa",
    "paymentLast4": "4242",
    "status": "PREPARANDO_ENVIO",
    "createdAt": "2026-05-14T10:10:00Z",
    "updatedAt": "2026-05-14T10:10:00Z"
  }
]
```

### `PATCH /api/orders/{orderId}/status`

Actualiza el estado del pedido según reglas de transición.

Frontend que lo usa: [frontend/app/pages/perfil.vue](../frontend/app/pages/perfil.vue)

Request:

```json
{
  "status": "ENVIADO"
}
```

Response:

```json
{
  "id": "order-456",
  "status": "ENVIADO",
  "updatedAt": "2026-05-14T12:30:00Z"
}
```

### `GET /api/orders/{orderId}/conversation`

Devuelve la conversación vinculada a una orden.

Frontend que lo usa: el flujo de chat cuando viene desde una orden.

Response:

```json
{
  "conversationId": "7e1a3cc5-4ce7-4d26-a7d5-4b5c04a8c9d0"
}
```

## Servicios que intervienen

- [backend/src/main/java/com/raremarket/backend/controller/OrderController.java](../backend/src/main/java/com/raremarket/backend/controller/OrderController.java)
- [backend/src/main/java/com/raremarket/backend/service/OrderService.java](../backend/src/main/java/com/raremarket/backend/service/OrderService.java)
- [backend/src/main/java/com/raremarket/backend/repository/OrderRepository.java](../backend/src/main/java/com/raremarket/backend/repository/OrderRepository.java)
- [backend/src/main/java/com/raremarket/backend/service/ChatService.java](../backend/src/main/java/com/raremarket/backend/service/ChatService.java)

## Cómo funciona en el frontend

- [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue) abre un checkout simulado, recoge dirección y tarjeta, y llama a `POST /api/orders`.
- [frontend/app/pages/perfil.vue](../frontend/app/pages/perfil.vue) alterna entre compras y ventas y muestra la evolución del pedido.
- El frontend mantiene el token de sesión con [frontend/app/composables/useSessionUser.ts](../frontend/app/composables/useSessionUser.ts).