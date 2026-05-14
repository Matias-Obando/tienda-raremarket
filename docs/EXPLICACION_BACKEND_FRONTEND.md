# Índice de documentación técnica

La documentación del backend se separó por módulos para que sea más fácil de mantener y consultar.

## Módulos

- [Autenticación y perfil](./backend-auth.md)
- [Artículos e imágenes](./backend-items.md)
- [Pedidos y checkout](./backend-orders.md)
- [Chat y conversaciones](./backend-chat.md)
- [Integraciones externas: Supabase y Brevo](./backend-integraciones-externas.md)

## Qué incluye cada módulo

- Endpoints disponibles.
- Ejemplos de request y response en JSON.
- Qué servicio ejecuta la lógica.
- Qué pantalla o composable del frontend consume cada endpoint.

## Flujo general

1. El frontend llama al backend con `$fetch`.
2. El controller valida y delega.
3. El service aplica reglas de negocio.
4. El repository persiste en PostgreSQL/Supabase.
5. Cuando aplica, se usan servicios auxiliares como Supabase Storage y correo.