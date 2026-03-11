<template>
  <div class="rm-container page">
    <!-- ✅ Volver “modo app”: usa from si existe; si no, vuelve atrás de verdad -->
    <a class="back" href="#" @click.prevent="goBack">← Volver</a>

    <div v-if="!item" class="notfound">
      No se encontró el producto.
    </div>

    <div v-else>
      <div class="layout">
        <div class="media">
          <img :src="item.imagen" :alt="item.titulo" class="img" />
        </div>

        <div class="info">
          <h1 class="title">{{ item.titulo }}</h1>

          <div class="priceRow">
            <div class="price">{{ item.precioEur.toFixed(2) }} €</div>
          </div>

          <div class="chips">
            <span class="chip">{{ item.categoria }}</span>
            <span class="chip">{{ item.marca }}</span>
            <span class="chip">Talla {{ item.talla }}</span>
            <span class="chip">{{ item.estado }}</span>
          </div>

          <p class="desc">{{ item.descripcion }}</p>

          <!-- Desktop: botón normal (en móvil lo ocultamos y usamos solo el sticky) -->
          <button class="btn rm-btn rm-btn--primary" type="button" @click="reservarMock">
            Reservar (mock)
          </button>

          <p class="small">Publicado {{ item.creadoHace }} · Solo envío (por ahora)</p>
        </div>
      </div>

      <!-- Relacionados (con fallback) -->
      <section v-if="relatedItems.length" class="related">
        <div class="relatedHeader">
          <h2 class="relatedTitle">{{ relatedTitle }}</h2>
          <NuxtLink to="/explorar" class="relatedLink">Ver todo</NuxtLink>
        </div>

        <div class="relatedGrid">
          <ItemCard v-for="r in relatedItems" :key="r.id" :item="r" />
        </div>
      </section>
    </div>

    <!-- Sticky CTA (mobile) -->
    <div v-if="item" class="stickyCta" role="region" aria-label="Acción principal">
      <div class="stickyInner">
        <div class="stickyLeft">
          <div class="stickyPrice">{{ item.precioEur.toFixed(2) }} €</div>
          <div class="stickyHint">{{ item.titulo }}</div>
        </div>

        <button class="rm-btn rm-btn--primary stickyBtn" type="button" @click="reservarMock">
          Reservar
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { mockItems } from '~/mock/items'

const route = useRoute()
const router = useRouter()

const id = computed(() => String(route.params.id))
const item = computed(() => mockItems.find((x) => String(x.id) === id.value) ?? null)

const hasSameCategoryRelated = computed(() => {
  if (!item.value) return false
  return mockItems.some(
    (x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id),
  )
})

const relatedTitle = computed(() => {
  if (!item.value) return 'Te puede interesar'
  return hasSameCategoryRelated.value ? `Más de ${item.value.categoria}` : 'Te puede interesar'
})

const relatedItems = computed(() => {
  if (!item.value) return []

  const sameCategory = mockItems
    .filter((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id))
    .slice(0, 4)

  if (sameCategory.length) return sameCategory

  return mockItems.filter((x) => String(x.id) !== String(item.value!.id)).slice(0, 4)
})

function goBack() {
  const from = route.query.from
  if (typeof from === 'string' && from.startsWith('/')) {
    navigateTo(from)
    return
  }

  // ✅ “modo app”: volver atrás de verdad si hay historial
  if (window.history.length > 1) {
    router.back()
    return
  }

  // fallback seguro
  navigateTo('/explorar')
}

function reservarMock() {
  if (!item.value) return
  alert(`Reserva simulada: ${item.value.titulo}. Luego lo conectamos con Supabase.`)
}
</script>

<style scoped>
.page {
  padding: 18px 12px 96px;
}

@media (min-width: 520px) {
  .page {
    padding-left: 0;
    padding-right: 0;
  }
}

.back {
  display: inline-block;
  margin: 10px 0 16px;
  color: var(--rm-text);
  text-decoration: none;
}

.layout {
  display: grid;
  grid-template-columns: 1fr;
  gap: 18px;
}

.media {
  border: 1px solid var(--rm-border);
  border-radius: var(--rm-radius);
  overflow: hidden;
  background: var(--rm-soft);
  aspect-ratio: 4 / 3;
}

.img {
  width: 100%;
  height: 100%;
  display: block;
  object-fit: cover;
}

.title {
  margin: 0 0 10px;
  font-size: 34px;
  line-height: 1.1;
  letter-spacing: -0.03em;
}

.priceRow {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 10px;
}

.price {
  font-weight: 900;
  font-size: 24px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 8px 0 14px;
}

.chip {
  font-size: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  border: 1px solid var(--rm-border);
  background: #fff;
  color: var(--rm-text);
}

.desc {
  margin: 0 0 14px;
  color: var(--rm-text);
}

.btn {
  display: none;
  width: 100%;
  justify-content: center;
}

.small {
  margin-top: 10px;
  color: var(--rm-muted);
  font-size: 12px;
}

.notfound {
  padding: 20px;
  border: 1px dashed var(--rm-border);
  border-radius: var(--rm-radius);
}

/* Relacionados */
.related {
  margin-top: 26px;
  padding-top: 14px;
  border-top: 1px solid var(--rm-border);
}

.relatedHeader {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.relatedTitle {
  margin: 0;
  font-size: 16px;
  letter-spacing: -0.02em;
}

.relatedLink {
  color: var(--rm-muted);
  text-decoration: none;
  font-size: 13px;
}

.relatedGrid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

@media (min-width: 520px) {
  .relatedGrid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* Sticky CTA */
.stickyCta {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;

  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-top: 1px solid var(--rm-border);

  padding: 10px 12px calc(10px + env(safe-area-inset-bottom));
}

.stickyInner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2px;
}

.stickyLeft {
  min-width: 0;
}

.stickyPrice {
  font-weight: 900;
  line-height: 1.1;
}

.stickyHint {
  font-size: 12px;
  color: var(--rm-muted);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 54vw;
}

.stickyBtn {
  min-height: 44px;
  white-space: nowrap;
}

/* Desktop */
@media (min-width: 900px) {
  .page {
    padding-bottom: 40px;
  }

  .layout {
    grid-template-columns: 1.1fr 0.9fr;
    align-items: start;
    gap: 22px;
  }

  .media {
    aspect-ratio: 1 / 1;
  }

  .info {
    position: sticky;
    top: 92px;
    align-self: start;
  }

  .title {
    font-size: 40px;
  }

  .btn {
    display: inline-flex;
    width: auto;
    min-width: 220px;
  }

  .stickyCta {
    display: none;
  }

  .relatedGrid {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>