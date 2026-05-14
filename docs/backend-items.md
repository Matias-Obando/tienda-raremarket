# Artículos e imágenes

Este módulo cubre listado, detalle, creación, edición, subida de imágenes y eliminación de artículos.

## Endpoints

### `GET /api/items`

Lista artículos disponibles con filtros opcionales.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts)

Query params soportados:

- `query`
- `categoria`
- `subcategoria`
- `genero`
- `talla`
- `estado`
- `minPrice`
- `maxPrice`
- `sellerId`
- `sort`

Response:

```json
[
  {
    "id": "item-123",
    "sellerId": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
    "titulo": "Chaqueta vaquera oversize",
    "descripcion": "Chaqueta en muy buen estado...",
    "precioEur": 24.99,
    "categoria": "Ropa",
    "subcategoria": "Chaquetas",
    "genero": "Unisex",
    "marca": "Zara",
    "talla": "M",
    "estado": "Como nuevo",
    "imagen": "https://.../items/item-123/main.jpg",
    "images": ["https://.../main.jpg", "https://.../back.jpg"],
    "creadoHace": "hace 2 días",
    "createdAt": "2026-05-12T10:00:00Z",
    "updatedAt": "2026-05-13T14:00:00Z"
  }
]
```

### `GET /api/items/{id}`

Devuelve un artículo concreto.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) y [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue)

Response:

```json
{
  "id": "item-123",
  "sellerId": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
  "titulo": "Chaqueta vaquera oversize",
  "descripcion": "Chaqueta en muy buen estado...",
  "precioEur": 24.99,
  "categoria": "Ropa",
  "subcategoria": "Chaquetas",
  "genero": "Unisex",
  "marca": "Zara",
  "talla": "M",
  "estado": "Como nuevo",
  "imagen": "https://.../items/item-123/main.jpg",
  "images": ["https://.../main.jpg", "https://.../back.jpg"],
  "creadoHace": "hace 2 días",
  "createdAt": "2026-05-12T10:00:00Z",
  "updatedAt": "2026-05-13T14:00:00Z"
}
```

### `POST /api/items`

Crea un artículo autenticado.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) y [frontend/app/pages/vender.vue](../frontend/app/pages/vender.vue)

Request:

```json
{
  "titulo": "Chaqueta vaquera oversize",
  "descripcion": "Chaqueta en muy buen estado...",
  "precioEur": 24.99,
  "categoria": "Ropa",
  "subcategoria": "Chaquetas",
  "genero": "Unisex",
  "marca": "Zara",
  "talla": "M",
  "estado": "Como nuevo",
  "imagen": "https://.../items/item-123/main.jpg",
  "images": ["https://.../main.jpg", "https://.../back.jpg"]
}
```

Response:

```json
{
  "id": "item-123",
  "sellerId": "9d9e7f7e-0ab3-4a4f-b76b-9b8a3e6d6e21",
  "titulo": "Chaqueta vaquera oversize",
  "descripcion": "Chaqueta en muy buen estado...",
  "precioEur": 24.99,
  "categoria": "Ropa",
  "subcategoria": "Chaquetas",
  "genero": "Unisex",
  "marca": "Zara",
  "talla": "M",
  "estado": "Como nuevo",
  "imagen": "https://.../items/item-123/main.jpg",
  "images": ["https://.../main.jpg", "https://.../back.jpg"],
  "creadoHace": "ahora",
  "createdAt": "2026-05-14T09:00:00Z",
  "updatedAt": "2026-05-14T09:00:00Z"
}
```

### `PUT /api/items/{id}`

Edita un artículo propio.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) y [frontend/app/pages/vender.vue](../frontend/app/pages/vender.vue)

Request: mismo contrato que `POST /api/items`.

Response: mismo formato que `GET /api/items/{id}`.

### `DELETE /api/items/{id}`

Elimina un artículo y sus imágenes asociadas.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) y [frontend/app/pages/perfil.vue](../frontend/app/pages/perfil.vue)

Response:

```json
{}
```

### `POST /api/items/images`

Sube imágenes del artículo a Supabase Storage.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts)

Request: `multipart/form-data` con campo `files` repetido.

Response:

```json
{
  "urls": [
    "https://.../storage/v1/object/public/item-images/items/user-1/photo-1.jpg",
    "https://.../storage/v1/object/public/item-images/items/user-1/photo-2.jpg"
  ]
}
```

### `POST /api/items/images/cleanup`

Borra imágenes subidas que finalmente no se usaron.

Frontend que lo usa: [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts)

Request:

```json
{
  "urls": [
    "https://.../storage/v1/object/public/item-images/items/user-1/photo-1.jpg"
  ]
}
```

Response:

```json
{
  "deleted": 1
}
```

## Servicios que intervienen

- [backend/src/main/java/com/raremarket/backend/controller/ItemController.java](../backend/src/main/java/com/raremarket/backend/controller/ItemController.java)
- [backend/src/main/java/com/raremarket/backend/service/ItemService.java](../backend/src/main/java/com/raremarket/backend/service/ItemService.java)
- [backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java](../backend/src/main/java/com/raremarket/backend/service/SupabaseStorageService.java)
- [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts)

## Cómo funciona en el frontend

- [frontend/app/pages/vender.vue](../frontend/app/pages/vender.vue) muestra preview local de imágenes antes de subir.
- [frontend/app/stores/useItemsStore.ts](../frontend/app/stores/useItemsStore.ts) centraliza la carga de catálogo y las mutaciones.
- [frontend/app/pages/item/[id].vue](../frontend/app/pages/item/[id].vue) usa los datos de imagen principal y galería para mostrar la ficha del producto.