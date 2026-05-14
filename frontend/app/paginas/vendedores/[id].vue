<template>
  <div class="seller-profile-page">
    <div class="seller-profile-shell">
      <a class="back-link" href="#" @click.prevent="goBack">
        <svg class="back-icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
          <path fill-rule="evenodd" d="M9.707 16.707a1 1 0 01-1.414 0l-6-6a1 1 0 010-1.414l6-6a1 1 0 011.414 1.414L5.414 9H17a1 1 0 110 2H5.414l4.293 4.293a1 1 0 010 1.414z" clip-rule="evenodd"/>
        </svg>
        Volver
      </a>

      <section class="seller-hero card">
        <div class="hero-top">
          <div class="seller-avatar" aria-hidden="true">
            <img v-if="sellerAvatar" :src="sellerAvatar" :alt="`Avatar de ${sellerName}`" class="seller-avatar__img" />
            <span v-else>{{ sellerInitial }}</span>
          </div>

          <div class="seller-meta">
            <p class="seller-kicker">Perfil del vendedor</p>
            <h1 class="seller-name">{{ sellerName }}</h1>
            <div class="seller-reputation">
              <span class="seller-stars" aria-hidden="true">★★★★★</span>
              <span class="reputation-label">Excellent seller</span>
            </div>
            <p class="seller-stats">{{ publishedItems.length }} artículos publicados</p>
          </div>
        </div>

        <div class="hero-actions">
          <button class="action-btn action-btn--primary" @click="openContact" :disabled="loadingContact">
            <svg class="action-icon" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
              <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
              <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
            </svg>
            Enviar mensaje
          </button>
        </div>
      </section>

      <section class="card seller-info">
        <div class="info-grid">
          <div class="info-item">
            <p class="info-label">Artículos publicados</p>
            <p class="info-value">{{ publishedItems.length }}</p>
          </div>
          <div class="info-item">
            <p class="info-label">Miembro desde</p>
            <p class="info-value">2024</p>
          </div>
          <div class="info-item">
            <p class="info-label">Velocidad de respuesta</p>
            <p class="info-value">Rápida</p>
          </div>
          <div class="info-item">
            <p class="info-label">Tasa de aceptación</p>
            <p class="info-value">98%</p>
          </div>
        </div>
      </section>

      <section class="card seller-map">
        <div class="seller-map__head">
          <div>
            <p class="seller-map__kicker">Ubicación</p>
            <h2 class="seller-map__title">Dónde se encuentra {{ sellerName }}</h2>
          </div>
          <p class="seller-map__location">
            {{ sellerLocationLabel }}
          </p>
        </div>

        <div v-if="sellerHasLocation" class="seller-map__frame">
          <iframe
            class="seller-map__iframe"
            :src="sellerMapSrc"
            :title="`Mapa de ubicación de ${sellerName}`"
            loading="lazy"
            referrerpolicy="no-referrer-when-downgrade"
          ></iframe>
        </div>

        <p v-else class="seller-map__empty">
          Este vendedor todavía no ha añadido una ubicación pública.
        </p>
      </section>

      <section class="card seller-items">
        <div class="seller-items__head">
          <h2>Artículos de {{ sellerName }}</h2>
          <span v-if="publishedItems.length" class="items-count">{{ publishedItems.length }}</span>
        </div>

        <p v-if="loading" class="state">Cargando artículos...</p>
        <p v-else-if="!publishedItems.length" class="state empty-state">
          <svg class="empty-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" aria-hidden="true">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
          </svg>
          Este vendedor todavía no tiene artículos publicados.
        </p>

        <div v-else class="grid">
          <ItemCard v-for="it in publishedItems" :key="it.id" :item="it" />
        </div>
      </section>
    </div>

    <Teleport to="body">
      <div v-if="contactModalOpen" class="modal-overlay" @click.self="closeContact">
        <div class="modal-card">
          <div class="modal-header">
            <h3>Enviar mensaje a {{ sellerName }}</h3>
            <button class="modal-close" @click="closeContact" aria-label="Cerrar">
              <svg viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
              </svg>
            </button>
          </div>
          <div class="modal-body">
            <textarea v-model="contactMessage" placeholder="Escribe tu mensaje..." class="contact-textarea" rows="5"></textarea>
          </div>
          <div class="modal-footer">
            <button class="modal-btn modal-btn--secondary" @click="closeContact">Cancelar</button>
            <button class="modal-btn modal-btn--primary" @click="sendContact" :disabled="loadingContact || !contactMessage.trim()">
              {{ loadingContact ? 'Enviando...' : 'Enviar mensaje' }}
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  alias: ['/vendedor/:id']
})

import ItemCard from '~/componentes/TarjetaArticulo.vue'
import type { Item } from '~/tiendas/articulos'

type SellerPreview = {
  id: string
  username: string
  avatarUrl?: string
  location?: string
}

const route = useRoute()
const router = useRouter()
const store = useItemsStore()
const seller = ref<SellerPreview | null>(null)
const loading = ref(false)
const contactModalOpen = ref(false)
const contactMessage = ref('')
const loadingContact = ref(false)

const sellerId = computed(() => String(route.params.id || ''))
const sellerName = computed(() => seller.value?.username?.trim() || 'Vendedor Closely')
const sellerAvatar = computed(() => seller.value?.avatarUrl || '')
const sellerLocation = computed(() => seller.value?.location?.trim() || '')
const sellerHasLocation = computed(() => sellerLocation.value.length > 0)
const sellerLocationLabel = computed(() => sellerHasLocation.value ? sellerLocation.value : 'Sin ubicación pública')
const sellerMapSrc = computed(() => {
  if (!sellerHasLocation.value) return ''
  return `https://www.google.com/maps?q=${encodeURIComponent(sellerLocation.value)}&output=embed`
})
const sellerInitial = computed(() => sellerName.value.charAt(0).toUpperCase())

const publishedItems = computed<Item[]>(() => {
  const id = sellerId.value
  if (!id) return []
  return store.items.filter((item) => String(item.sellerId ?? '') === id)
})

async function loadSeller() {
  if (!sellerId.value) return

  loading.value = true
  try {
    if (!store.items.length) {
      await store.fetchAll()
    }

    const config = useRuntimeConfig()
    const users = await $fetch<SellerPreview[]>(`${config.public.API_BASE_URL}/users`)
    seller.value = users.find((user) => String(user.id) === sellerId.value) ?? null
  } finally {
    loading.value = false
  }
}

function openContact() {
  contactModalOpen.value = true
}

function closeContact() {
  contactModalOpen.value = false
  contactMessage.value = ''
}

async function sendContact() {
  if (!contactMessage.value.trim()) return

  loadingContact.value = true
  try {
    // TODO: Implementar envío de mensaje a través de la API
    await new Promise(resolve => setTimeout(resolve, 800))
    closeContact()
    // Show success message
    console.log('Mensaje enviado a', sellerName.value)
  } finally {
    loadingContact.value = false
  }
}

function goBack() {
  const from = route.query.from
  if (typeof from === 'string' && from.startsWith('/')) {
    navigateTo(from)
    return
  }
  if (window.history.length > 1) {
    router.back()
    return
  }
  navigateTo('/inicio')
}

onMounted(async () => {
  await loadSeller()
})
</script>

<style scoped>
.seller-profile-page {
  min-height: 100vh;
  background: var(--rm-page-bg);
  padding: 24px 16px 56px;
}

.seller-profile-shell {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 18px;
}

/* Back Link */
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #475569;
  text-decoration: none;
  font-weight: 600;
  padding: 6px 0;
  transition: all 0.2s ease;
}

.back-link:hover {
  color: #1fb981;
}

.back-icon {
  width: 18px;
  height: 18px;
  display: block;
}

/* Card Base */
.card {
  border: 1px solid #dbe4ee;
  border-radius: 18px;
  background: #ffffff;
  box-shadow: 0 14px 34px rgba(15, 23, 42, 0.07);
  transition: all 0.3s ease;
}

.card:hover {
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.1);
}

/* Hero Section */
.seller-hero {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 24px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fcfb 52%, #eefcf8 100%);
}

.hero-top {
  display: flex;
  align-items: center;
  gap: 18px;
}

.seller-avatar {
  width: 80px;
  height: 80px;
  border-radius: 999px;
  background: linear-gradient(135deg, #1fb981 0%, #0f766e 100%);
  color: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: 700;
  overflow: hidden;
  flex-shrink: 0;
  border: 3px solid #dcfce7;
  box-shadow: 0 10px 28px rgba(31, 185, 129, 0.2);
}

.seller-avatar__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.seller-meta {
  flex: 1;
  min-width: 0;
}

.seller-kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #1fb981;
  font-size: 11px;
  font-weight: 800;
}

.seller-name {
  margin: 6px 0 0;
  font-size: clamp(1.5rem, 3vw, 2.2rem);
  line-height: 1.1;
  color: #0f172a;
  font-weight: 900;
}

.seller-reputation {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  font-size: 0.95rem;
}

.seller-stars {
  color: #f59e0b;
  font-size: 1.1rem;
  letter-spacing: 0.02em;
}

.reputation-label {
  color: #059669;
  font-weight: 600;
}

.seller-stats {
  margin: 6px 0 0;
  color: #64748b;
  font-size: 0.95rem;
}

/* Hero Actions */
.hero-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  text-decoration: none;
}

.action-btn--primary {
  background: var(--rm-primary);
  border-color: var(--rm-primary);
  color: #ffffff;
}

.action-btn--primary:hover:not(:disabled) {
  background: var(--rm-primary-hover);
  border-color: var(--rm-primary-hover);
  box-shadow: 0 8px 20px rgba(31, 185, 129, 0.3);
}

.action-btn--secondary {
  background: #f0fdf9;
  border-color: #dcfce7;
  color: #1fb981;
}

.action-btn--secondary:hover {
  background: #dcfce7;
  border-color: #bbf7d0;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-icon {
  width: 18px;
  height: 18px;
  display: block;
}

/* Info Section */
.seller-info {
  padding: 20px 24px;
}

.seller-map {
  padding: 20px 24px;
}

.seller-map__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.seller-map__kicker {
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #1fb981;
  font-size: 11px;
  font-weight: 800;
}

.seller-map__title {
  margin: 6px 0 0;
  font-size: 1.05rem;
  color: #0f172a;
  font-weight: 800;
}

.seller-map__location {
  margin: 0;
  color: #64748b;
  font-size: 0.92rem;
  text-align: right;
  max-width: 46%;
}

.seller-map__frame {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #dbe4ee;
  background: #f8fafc;
  min-height: 280px;
}

.seller-map__iframe {
  display: block;
  width: 100%;
  height: 100%;
  min-height: 280px;
  border: 0;
}

.seller-map__empty {
  margin: 0;
  padding: 18px;
  border-radius: 14px;
  background: #f8fafc;
  color: #64748b;
  border: 1px dashed #dbe4ee;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 14px;
}

.info-item {
  text-align: center;
  padding: 12px;
  border-radius: 12px;
  background: #f8fafc;
  transition: all 0.2s ease;
}

.info-item:hover {
  background: #f1f5f9;
  transform: translateY(-2px);
}

.info-label {
  margin: 0;
  font-size: 0.85rem;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.info-value {
  margin: 6px 0 0;
  font-size: 1.4rem;
  font-weight: 900;
  color: #1fb981;
}

/* Items Section */
.seller-items {
  padding: 24px;
}

.seller-items__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  gap: 12px;
}

.seller-items__head h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #0f172a;
  font-weight: 800;
}

.items-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  border-radius: 999px;
  background: #dcfce7;
  color: #1fb981;
  font-weight: 700;
  font-size: 0.9rem;
}

.state {
  margin: 0;
  color: #64748b;
  text-align: center;
  padding: 20px;
  font-size: 0.95rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #94a3b8;
  border: 2px dashed #dbe4ee;
  border-radius: 12px;
  background: #f8fafc;
  padding: 40px 20px;
}

.empty-icon {
  width: 48px;
  height: 48px;
  opacity: 0.5;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(190px, 220px));
  justify-content: center;
  gap: 10px;
}

.grid :deep(.card) {
  max-width: 220px;
  margin: 0 auto;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 16px;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-card {
  background: #ffffff;
  border-radius: 18px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  max-width: 500px;
  width: 100%;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.15rem;
  color: #0f172a;
  font-weight: 700;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #e5e7eb;
  color: #1f2937;
}

.modal-close svg {
  width: 20px;
  height: 20px;
}

.modal-body {
  padding: 20px 24px;
}

.contact-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  font-family: inherit;
  font-size: 0.95rem;
  color: #0f172a;
  resize: vertical;
  transition: all 0.2s ease;
}

.contact-textarea:focus {
  outline: none;
  border-color: #1fb981;
  box-shadow: 0 0 0 3px rgba(31, 185, 129, 0.1);
}

.modal-footer {
  display: flex;
  gap: 10px;
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  justify-content: flex-end;
}

.modal-btn {
  height: 40px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-btn--primary {
  background: var(--rm-primary);
  border-color: var(--rm-primary);
  color: #ffffff;
}

.modal-btn--primary:hover:not(:disabled) {
  background: var(--rm-primary-hover);
}

.modal-btn--primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.modal-btn--secondary {
  background: #f8fafc;
  border-color: #e5e7eb;
  color: #64748b;
}

.modal-btn--secondary:hover {
  background: #e5e7eb;
  color: #0f172a;
}

/* Responsive */
@media (max-width: 900px) {
  .grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }

  .seller-hero {
    padding: 18px;
  }

  .hero-top {
    gap: 14px;
  }

  .seller-avatar {
    width: 72px;
    height: 72px;
    font-size: 1.8rem;
  }

  .seller-name {
    font-size: 1.5rem;
  }

  .info-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 700px) {
  .seller-profile-page {
    padding-left: 12px;
    padding-right: 12px;
    padding-top: 16px;
  }

  .seller-hero {
    padding: 16px;
  }

  .hero-top {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .seller-avatar {
    width: 64px;
    height: 64px;
    font-size: 1.6rem;
  }

  .seller-name {
    font-size: 1.3rem;
  }

  .hero-actions {
    width: 100%;
  }

  .action-btn {
    flex: 1;
    min-height: 44px;
  }

  .seller-items {
    padding: 18px;
  }

  .seller-map {
    padding: 18px;
  }

  .seller-map__head {
    flex-direction: column;
  }

  .seller-map__location {
    text-align: left;
    max-width: none;
  }

  .grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 10px;
  }

  .info-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .info-item {
    padding: 10px;
  }

  .modal-card {
    margin: 16px;
  }
}

@media (max-width: 480px) {
  .grid {
    grid-template-columns: 1fr;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .seller-items__head {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }
}
</style>
