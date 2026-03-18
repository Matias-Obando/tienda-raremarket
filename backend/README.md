# Backend Spring Boot - RareMarket

Backend base para conectar el frontend Nuxt de este repositorio.

## Requisitos

- Java 17+
- Maven 3.9+

## Ejecutar

1. Entrar en la carpeta backend.
2. Ejecutar: `mvn spring-boot:run`
3. API disponible en `http://localhost:8080`

## Endpoints

### Health rapido

- `GET /api/items`

### Items

- `GET /api/items` Lista productos
- `GET /api/items/{id}` Detalle de producto
- `POST /api/items` Crear producto

Body de ejemplo para crear item:

```json
{
  "titulo": "Sudadera Champion",
  "descripcion": "Buen estado general",
  "precioEur": 29.9,
  "categoria": "SUDADERAS",
  "marca": "Champion",
  "talla": "M",
  "estado": "COMO_NUEVO",
  "imagen": "https://picsum.photos/seed/new/800/800"
}
```

### Auth (demo, en memoria)

- `POST /api/auth/register`
- `POST /api/auth/login`

Body auth:

```json
{
  "email": "demo@raremarket.com",
  "password": "123456"
}
```

## CORS

Se permite origen `http://localhost:3000` para rutas `/api/**`.

## Integracion rapida en Nuxt

1. Reemplaza los mocks por llamadas a `http://localhost:8080/api/items`.
2. Para detalle, usa `http://localhost:8080/api/items/:id`.
3. Para login/registro, usa `/api/auth/login` y `/api/auth/register`.

## Siguiente paso recomendado

- Sustituir repositorio en memoria por Postgres (Spring Data JPA) y JWT real.
