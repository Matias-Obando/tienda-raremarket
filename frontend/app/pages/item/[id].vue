<template>
  <div class="rm-page">
    <div class="rm-container page">
    <div v-if="loading" class="notfound">Cargando producto...</div>

    <div v-else-if="!item" class="notfound">No se encontró el producto.</div>

    <div v-else class="product-grid">
      <div class="leftCol product-panel product-panel--media">
        <div class="media">
          <span
            class="badge"
            :class="{
              'badge-new': item.estado === 'Nuevo',
              'badge-like-new': item.estado === 'Como nuevo',
              'badge-used': item.estado === 'Usado'
            }"
            aria-hidden="true"
          >{{ item.estado }}</span>

          <button
            class="fav-btn"
            @click.prevent="toggleFavorite"
            :aria-pressed="isFav"
            :title="isFav ? 'Quitar de favoritos' : 'Añadir a favoritos'"
          >
            <svg v-if="isFav" class="icon fav-on" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 6 4 4 6.5 4c1.74 0 3.41.81 4.5 2.09C12.09 4.81 13.76 4 15.5 4 18 4 20 6 20 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            <svg v-else class="icon fav-off" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" aria-hidden="true">
              <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/>
            </svg>
          </button>

          <img :src="currentImage" :alt="item.titulo" class="img" />
        </div>

        <div class="thumbs" role="list" aria-label="Miniaturas">
          <button
            v-for="(t, i) in thumbs"
            :key="i"
            class="thumb"
            :class="{ active: t === currentImage }"
            @click="selectImage(t)"
            @keyup.enter.space.prevent="selectImage(t)"
            :aria-pressed="t === currentImage"
            role="listitem"
            type="button"
            :title="`Ver imagen ${i + 1}`"
          >
            <img :src="t" :alt="`Miniatura ${i + 1} de ${item.titulo}`" />
          </button>
        </div>
      </div>

      <div class="rightCol product-panel product-panel--info">
        <h1 class="title">{{ item.titulo }}</h1>

        <div class="priceWrap">
          <div class="price">{{ item.precioEur }} €</div>
        </div>

        <div class="item-meta-line">
          <span v-if="item.marca">{{ item.marca }}</span>
          <span v-if="item.marca && item.talla" class="meta-sep">·</span>
          <span v-if="item.talla">Talla {{ item.talla }}</span>
          <span v-if="(item.marca || item.talla) && item.estado" class="meta-sep">·</span>
          <span v-if="item.estado">{{ item.estado }}</span>
        </div>

        <p class="desc">{{ item.descripcion }}</p>

        <div class="actions">
          <button class="rm-btn rm-btn--primary" :disabled="isOwnItem" @click="openCheckout">{{ isOwnItem ? 'Tu producto' : 'Comprar' }}</button>
          <button class="rm-btn rm-btn--secondary" @click="openContact">Enviar mensaje</button>
        </div>

        <p v-if="checkoutSuccess" class="checkout-msg checkout-msg--ok">{{ checkoutSuccess }}</p>
        <p v-if="checkoutError" class="checkout-msg checkout-msg--error">{{ checkoutError }}</p>

        <div class="meta small">Publicado {{ item.creadoHace }}</div>

        <div class="seller-card" role="group" aria-label="Vendedor del articulo">
          <div class="seller-main">
            <div class="seller-avatar" aria-hidden="true">
              <img v-if="sellerAvatar" :src="sellerAvatar" :alt="`Avatar de ${sellerDisplayName}`" class="seller-avatar__img" />
              <span v-else>{{ sellerInitial }}</span>
            </div>

            <div class="seller-copy">
              <p class="seller-name">{{ sellerDisplayName }}</p>
              <p class="seller-reputation">
                <span class="seller-stars" aria-hidden="true">★★★★★</span>
                <span>{{ sellerReviews }}</span>
              </p>
            </div>
          </div>

          <button
            class="seller-open"
            type="button"
            :disabled="loadingSeller || !item?.sellerId"
            :title="`Ver perfil de ${sellerDisplayName}`"
            @click="goToSellerProfile"
          >
            <svg class="seller-open__icon" viewBox="0 0 20 20" fill="none" aria-hidden="true">
              <path d="M7.5 4.5L12.5 10L7.5 15.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
          </button>
        </div>

        <NuxtLink
          v-if="isOwnItem"
          :to="{ path: '/vender', query: { edit: item.id } }"
          class="edit-publication-btn"
        >
          Editar publicación
        </NuxtLink>
      </div>
    </div>

    <hr class="separator" />

    <section v-if="relatedItems.length" class="related">
      <h2 class="relatedTitle">{{ relatedTitle }}</h2>
      <div class="relatedGrid">
        <ItemCard v-for="r in relatedItems" :key="r.id" :item="r" />
      </div>
    </section>

    <Teleport to="body">
      <div v-if="checkoutOpen" class="checkout-modal" role="dialog" aria-modal="true" aria-label="Checkout simulado">
        <button class="checkout-overlay" aria-label="Cerrar checkout" @click="closeCheckout"></button>

        <div class="checkout-shell">
          <div class="checkout-card">
            <div class="checkout-head">
              <div>
                <h3>Confirmar compra</h3>
              </div>
              <button class="checkout-close" :disabled="checkoutLoading" @click="closeCheckout" aria-label="Cerrar checkout">
                <svg class="checkout-close__icon" viewBox="0 0 20 20" fill="none" aria-hidden="true">
                  <path d="M5 5L15 15" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
                  <path d="M15 5L5 15" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
                </svg>
              </button>
            </div>

            <div class="checkout-steps" aria-label="Pasos de checkout">
              <div class="checkout-step" :class="{ 'checkout-step--active': checkoutStep === 1 }">
                <span class="checkout-step__num">1</span>
                <span>Envio y direccion</span>
              </div>
              <div class="checkout-step" :class="{ 'checkout-step--active': checkoutStep === 2 }">
                <span class="checkout-step__num">2</span>
                <span>Pago</span>
              </div>
            </div>

            <form class="checkout-form" @submit.prevent="submitCheckout">
              <section class="checkout-block checkout-block--soft">
                <p class="checkout-label">Articulo</p>
                <div class="checkout-product">
                  <img :src="currentImage" :alt="item?.titulo || 'Articulo'" class="checkout-product__img" />
                  <div class="checkout-product__copy">
                    <p class="checkout-product__title">{{ item?.titulo }}</p>
                    <p class="checkout-product__meta">{{ item?.categoria }} · {{ item?.estado }}</p>
                    <p class="checkout-product__price">{{ formatMoney(itemPrice) }}</p>
                  </div>
                </div>
              </section>

              <section v-if="checkoutStep === 1" class="checkout-block">
                <p class="checkout-label">Tipo de entrega</p>
                <label class="checkout-option checkout-option--active">
                  <span class="checkout-option__main">A domicilio</span>
                  <span class="checkout-option__sub">Entrega estandar con seguimiento interno</span>
                  <span class="checkout-option__chip">Seleccionado</span>
                </label>
              </section>

              <section v-if="checkoutStep === 1" class="checkout-block">
                <p class="checkout-label">Direccion de envio <span class="field-required">*</span></p>
                <div class="checkout-grid">
                  <input v-model="checkoutForm.shippingFullName" class="checkout-input" :disabled="checkoutLoading" placeholder="Nombre completo *" required />
                  <input v-model="checkoutForm.shippingPhone" class="checkout-input" :disabled="checkoutLoading" placeholder="Telefono *" required />
                  <input v-model="checkoutForm.shippingAddressLine1" class="checkout-input checkout-input--full" :disabled="checkoutLoading" placeholder="Calle y numero *" required />
                  <input v-model="checkoutForm.shippingCity" class="checkout-input" :disabled="checkoutLoading" placeholder="Ciudad *" required />
                  <input v-model="checkoutForm.shippingPostalCode" class="checkout-input" :disabled="checkoutLoading" placeholder="Codigo postal *" required />
                  <input v-model="checkoutForm.shippingCountry" class="checkout-input checkout-input--full" :disabled="checkoutLoading" placeholder="Pais *" required />
                </div>
                <p class="checkout-help">Los campos con * son obligatorios.</p>
              </section>

              <section v-if="checkoutStep === 2" class="checkout-block">
                <p class="checkout-label">Metodo de pago <span class="field-required">*</span></p>

                <div class="payment-methods" aria-label="Opciones de pago">
                  <button type="button" class="payment-method payment-method--active" aria-pressed="true">
                    <span class="payment-method__top">
                      <span class="payment-method__name">Tarjeta</span>
                      <span class="payment-method__status payment-method__status--active">Activa</span>
                    </span>
                    <span class="payment-method__logos">
                      <span class="logo-chip logo-chip--visa" aria-label="Visa">
                        <FontAwesomeIcon :icon="faCcVisa" />
                      </span>
                      <span class="logo-chip logo-chip--mc" aria-label="Mastercard">
                        <FontAwesomeIcon :icon="faCcMastercard" />
                      </span>
                    </span>
                  </button>

                  <button type="button" class="payment-method" disabled>
                    <span class="payment-method__top">
                      <span class="payment-method__name">PayPal</span>
                      <span class="payment-method__status">Proximamente</span>
                    </span>
                    <span class="payment-method__logos">
                      <span class="logo-chip logo-chip--paypal" aria-label="PayPal">
                        <FontAwesomeIcon :icon="faPaypal" />
                      </span>
                    </span>
                  </button>

                  <button type="button" class="payment-method" disabled>
                    <span class="payment-method__top">
                      <span class="payment-method__name">Apple Pay</span>
                      <span class="payment-method__status">Proximamente</span>
                    </span>
                    <span class="payment-method__logos">
                      <span class="logo-chip logo-chip--apple" aria-label="Apple Pay">
                        <FontAwesomeIcon :icon="faApplePay" />
                      </span>
                    </span>
                  </button>
                </div>

                <div class="checkout-grid payment-grid">
                  <input v-model="checkoutForm.cardNumber" class="checkout-input checkout-input--full" :disabled="checkoutLoading" placeholder="Numero de tarjeta *" required />
                  <input v-model="checkoutForm.cardHolder" class="checkout-input checkout-input--full" :disabled="checkoutLoading" placeholder="Titular *" required />
                  <input v-model="checkoutForm.cardExpiry" class="checkout-input" :disabled="checkoutLoading" placeholder="MM/AA *" required />
                  <input v-model="checkoutForm.cardCvv" class="checkout-input" :disabled="checkoutLoading" placeholder="CVV *" required />
                </div>
                <p class="checkout-help checkout-help--secure">Pago seguro cifrado. PayPal y Apple Pay estaran disponibles proximamente.</p>
              </section>

              <p v-if="checkoutError" class="checkout-msg checkout-msg--error">{{ checkoutError }}</p>

              <div class="checkout-mobile-total" aria-hidden="true">
                <div>
                  <p class="checkout-mobile-total__label">Total</p>
                  <p class="checkout-mobile-total__value">{{ formatMoney(totalPrice) }}</p>
                </div>
                <button
                  type="button"
                  class="checkout-mobile-total__pay"
                  :disabled="checkoutLoading"
                  @click="handlePrimaryAction"
                >
                  {{ checkoutLoading ? 'Procesando...' : (checkoutStep === 1 ? 'Siguiente' : 'Pagar ahora') }}
                </button>
              </div>

              <div class="checkout-actions checkout-actions--mobile">
                <button
                  type="button"
                  class="rm-btn rm-btn--secondary"
                  :disabled="checkoutLoading"
                  @click="checkoutStep === 1 ? closeCheckout() : prevCheckoutStep()"
                >
                  {{ checkoutStep === 1 ? 'Cancelar' : 'Atras' }}
                </button>
                <button type="button" class="rm-btn rm-btn--primary" :disabled="checkoutLoading" @click="handlePrimaryAction">
                  {{ checkoutLoading ? 'Procesando...' : (checkoutStep === 1 ? 'Siguiente' : `Pagar ${formatMoney(totalPrice)}`) }}
                </button>
              </div>
            </form>
          </div>

          <aside class="checkout-summary" aria-label="Resumen de compra">
            <h4>Desglose del precio</h4>
            <ul class="summary-list">
              <li>
                <span>Articulo</span>
                <strong>{{ formatMoney(itemPrice) }}</strong>
              </li>
              <li>
                <span>Proteccion comprador</span>
                <strong>{{ formatMoney(buyerProtectionFee) }}</strong>
              </li>
              <li>
                <span>Envio</span>
                <strong>{{ formatMoney(shippingFee) }}</strong>
              </li>
            </ul>
            <div class="summary-total">
              <span>Total</span>
              <strong>{{ formatMoney(totalPrice) }}</strong>
            </div>

            <button
              type="button"
              class="summary-pay"
              :disabled="checkoutLoading"
              @click="handlePrimaryAction"
            >
              {{ checkoutLoading ? 'Procesando...' : (checkoutStep === 1 ? 'Siguiente: Pago' : `Pagar ${formatMoney(totalPrice)}`) }}
            </button>

            <p class="summary-note">{{ checkoutStep === 1 ? 'Completa envio y direccion para pasar al pago.' : 'Tus datos de pago se usan solo en esta simulacion.' }}</p>
          </aside>
        </div>
      </div>
    </Teleport>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, computed, watch } from 'vue'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { faCcVisa, faCcMastercard, faPaypal, faApplePay } from '@fortawesome/free-brands-svg-icons'
import ItemCard from '~/components/ItemCard.vue'
import type { Item } from '~/stores/items'
import { useItemsStore } from '~/stores/useItemsStore'

const route = useRoute()
const router = useRouter()
const { sessionUser, loadSessionUser } = useSessionUser()
const uiMessages = useUiMessages()
const store = useItemsStore()

const id = computed(() => String(route.params.id))
const item = ref<Item | null>(null)
const loading = ref(false)

type SellerPreview = {
  id: string
  username: string
  avatarUrl?: string
}

const seller = ref<SellerPreview | null>(null)
const loadingSeller = ref(false)

const sellerDisplayName = computed(() => seller.value?.username?.trim() || 'Vendedor Closely')
const sellerAvatar = computed(() => seller.value?.avatarUrl || '')
const sellerInitial = computed(() => sellerDisplayName.value.charAt(0).toUpperCase())
const isOwnItem = computed(() => {
  if (!sessionUser.value || !item.value) return false
  return String(sessionUser.value.id) === String(item.value.sellerId)
})
const sellerReviews = computed(() => {
  const seedBase = item.value?.sellerId ?? item.value?.id ?? '0'
  const seed = Array.from(seedBase).reduce((acc, ch) => acc + ch.charCodeAt(0), 0)
  return 35 + (seed % 170)
})

async function loadSellerInfo(sellerId?: string) {
  seller.value = null
  if (!sellerId) return

  loadingSeller.value = true
  try {
    const config = useRuntimeConfig()
    const users = await $fetch<SellerPreview[]>(`${config.public.API_BASE_URL}/users`)
    seller.value = users.find((u) => String(u.id) === String(sellerId)) ?? null
  } catch (e) {
    console.error('No se pudo cargar el vendedor del articulo:', e)
  } finally {
    loadingSeller.value = false
  }
}

async function loadItem() {
  loading.value = true
  try {
    item.value = await store.fetchById(id.value)

    if (store.items.length === 0) {
      await store.fetchAll()
    }

    await loadSellerInfo(item.value?.sellerId)
  } finally {
    loading.value = false
  }
}

const thumbs = computed<string[]>(() => {
  if (!item.value) return []
  if (Array.isArray((item.value as any).images) && (item.value as any).images.length > 0) {
    return (item.value as any).images
  }
  const base = item.value.imagen
  return [base, base] 
})

const currentImage = ref<string>('')

watch(item, (n) => {
  currentImage.value = thumbs.value[0] ?? ''
}, { immediate: true })

watch(id, async () => {
  await loadItem()
})

function selectImage(src: string) {
  currentImage.value = src
}

const hasSameCategoryRelated = computed(() => {
  if (!item.value) return false
  return store.items.some((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id))
})

const relatedTitle = computed(() => {
  if (!item.value) return 'Te puede interesar'
  return hasSameCategoryRelated.value ? `Más de ${item.value.categoria}` : 'Te puede interesar'
})

const relatedItems = computed(() => {
  if (!item.value) return []
  const sameCategory = store.items.filter((x) => x.categoria === item.value!.categoria && String(x.id) !== String(item.value!.id)).slice(0, 4)
  if (sameCategory.length) return sameCategory
  return store.items.filter((x) => String(x.id) !== String(item.value!.id)).slice(0, 4)
})

function goBack() {
  const from = route.query.from
  if (typeof from === 'string' && from.startsWith('/')) {
    navigateTo(from)
    return
  }
  if (window.history.length > 1) { router.back(); return }
  navigateTo('/explorar')
}

const checkoutOpen = ref(false)
const checkoutStep = ref<1 | 2>(1)
const checkoutLoading = ref(false)
const checkoutError = ref('')
const checkoutSuccess = ref('')
const checkoutForm = reactive({
  shippingFullName: '',
  shippingPhone: '',
  shippingAddressLine1: '',
  shippingCity: '',
  shippingPostalCode: '',
  shippingCountry: 'Espana',
  cardNumber: '',
  cardHolder: '',
  cardExpiry: '',
  cardCvv: ''
})

const itemPrice = computed(() => {
  const raw = Number(item.value?.precioEur ?? 0)
  return Number.isFinite(raw) ? Math.max(raw, 0) : 0
})

const buyerProtectionFee = computed(() => {
  const fee = itemPrice.value * 0.075
  return Math.max(1.2, Math.round(fee * 100) / 100)
})

const shippingFee = computed(() => {
  if (itemPrice.value >= 60) return 2.65
  if (itemPrice.value >= 30) return 3.19
  return 3.89
})

const totalPrice = computed(() => {
  return Number((itemPrice.value + buyerProtectionFee.value + shippingFee.value).toFixed(2))
})

function formatMoney(value: number) {
  return new Intl.NumberFormat('es-ES', {
    style: 'currency',
    currency: 'EUR'
  }).format(value)
}

function openCheckout() {
  loadSessionUser()
  checkoutError.value = ''
  checkoutSuccess.value = ''
  checkoutStep.value = 1

  if (!item.value) return

  if (!sessionUser.value?.token) {
    navigateTo({
      path: '/auth',
      query: {
        mode: 'login',
        redirect: route.fullPath
      }
    })
    return
  }

  checkoutOpen.value = true
}

function validateStepOne() {
  const requiredFields = [
    checkoutForm.shippingFullName,
    checkoutForm.shippingPhone,
    checkoutForm.shippingAddressLine1,
    checkoutForm.shippingCity,
    checkoutForm.shippingPostalCode,
    checkoutForm.shippingCountry
  ]

  const isValid = requiredFields.every((value) => value.trim().length > 0)
  if (!isValid) {
    checkoutError.value = 'Completa los datos de envio para continuar al pago.'
  }
  return isValid
}

function validateStepTwo() {
  const requiredFields = [
    checkoutForm.cardNumber,
    checkoutForm.cardHolder,
    checkoutForm.cardExpiry,
    checkoutForm.cardCvv
  ]

  const isValid = requiredFields.every((value) => value.trim().length > 0)
  if (!isValid) {
    checkoutError.value = 'Completa los datos de pago para confirmar la compra.'
  }
  return isValid
}

function nextCheckoutStep() {
  checkoutError.value = ''
  if (!validateStepOne()) return
  checkoutStep.value = 2
}

function prevCheckoutStep() {
  checkoutError.value = ''
  checkoutStep.value = 1
}

function closeCheckout() {
  checkoutError.value = ''
  checkoutLoading.value = false
  checkoutStep.value = 1
  checkoutOpen.value = false
}

function handlePrimaryAction() {
  if (checkoutStep.value === 1) {
    nextCheckoutStep()
    return
  }
  void submitCheckout()
}

watch(checkoutOpen, (open) => {
  if (typeof document === 'undefined') return
  document.body.classList.toggle('rm-lock-scroll', open)
})

async function submitCheckout() {
  if (!item.value) return

  checkoutError.value = ''
  if (!validateStepOne() || !validateStepTwo()) {
    return
  }

  loadSessionUser()
  if (!sessionUser.value?.token) {
    checkoutError.value = 'Necesitas iniciar sesion para comprar.'
    await navigateTo({
      path: '/auth',
      query: {
        mode: 'login',
        redirect: route.fullPath
      }
    })
    return
  }

  checkoutLoading.value = true
  checkoutError.value = ''

  try {
    const config = useRuntimeConfig()
    await $fetch(`${config.public.API_BASE_URL}/orders`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${sessionUser.value.token}`
      },
      body: {
        itemId: item.value.id,
        deliveryMethod: 'shipping',
        shippingFullName: checkoutForm.shippingFullName,
        shippingPhone: checkoutForm.shippingPhone,
        shippingAddressLine1: checkoutForm.shippingAddressLine1,
        shippingCity: checkoutForm.shippingCity,
        shippingPostalCode: checkoutForm.shippingPostalCode,
        shippingCountry: checkoutForm.shippingCountry,
        cardNumber: checkoutForm.cardNumber,
        cardHolder: checkoutForm.cardHolder,
        cardExpiry: checkoutForm.cardExpiry,
        cardCvv: checkoutForm.cardCvv
      }
    })

    checkoutOpen.value = false
    checkoutSuccess.value = 'Pedido creado. El vendedor debe aceptarlo.'
    uiMessages.success('Pedido creado correctamente.')
    await store.fetchAll()
  } catch (error: any) {
    checkoutError.value =
      error?.data?.message ||
      (typeof error?.data === 'string' ? error.data : '') ||
      error?.message ||
      'No se pudo completar la compra simulada.'
    uiMessages.error(checkoutError.value)
  } finally {
    checkoutLoading.value = false
  }
}

function openContact() {
  loadSessionUser()
  if (!item.value) return

  if (!sessionUser.value) {
    navigateTo({
      path: '/auth',
      query: {
        mode: 'login',
        redirect: `/chat?itemId=${encodeURIComponent(item.value.id)}&itemTitle=${encodeURIComponent(item.value.titulo)}&sellerId=${encodeURIComponent(item.value.sellerId)}&sellerName=${encodeURIComponent(sellerDisplayName.value)}`
      }
    })
    return
  }

  navigateTo({
    path: '/chat',
    query: {
      itemId: item.value.id,
      itemTitle: item.value.titulo,
      sellerId: item.value.sellerId,
      sellerName: sellerDisplayName.value
    }
  })
}

function goToSellerProfile() {
  if (!item.value?.sellerId) return
  navigateTo(`/vendedor/${encodeURIComponent(item.value.sellerId)}`)
}

const LS_KEY = 'closely:favorites'
const isFav = ref(false)
function readFavorites(): string[] {
  try { const raw = localStorage.getItem(LS_KEY); return raw ? JSON.parse(raw) : [] } catch { return [] }
}
function writeFavorites(arr: string[]) {
  try {
    localStorage.setItem(LS_KEY, JSON.stringify(arr))
    window.dispatchEvent(new CustomEvent('closely:favs:updated', { detail: arr }))
  } catch {}
}
function toggleFavorite() {
  if (!item.value) return
  const favs = readFavorites()
  const idx = favs.indexOf(item.value.id)
  if (idx >= 0) { favs.splice(idx, 1); isFav.value = false } else { favs.push(item.value.id); isFav.value = true }
  writeFavorites(favs)
}
function syncFavs() {
  if (!item.value) { isFav.value = false; return }
  const favs = readFavorites()
  isFav.value = favs.includes(item.value.id)
}
onMounted(() => { syncFavs(); window.addEventListener('storage', syncFavs); window.addEventListener('closely:favs:updated', syncFavs) })
onBeforeUnmount(() => { window.removeEventListener('storage', syncFavs); window.removeEventListener('closely:favs:updated', syncFavs) })

onMounted(async () => {
  loadSessionUser()
  await loadItem()
})

onBeforeUnmount(() => {
  if (typeof document === 'undefined') return
  document.body.classList.remove('rm-lock-scroll')
})
</script>

<style scoped>
  :global(body.rm-lock-scroll) {
    overflow: hidden;
  }
  
  .rm-page {
    min-height: 100vh;
    background: var(--rm-page-bg);
  }
  
  .rm-container {
    box-sizing: border-box;
    max-width: 1360px;
    margin: 0 auto;
    padding-left: 40px;
    padding-right: 40px;
  }
  
  .page { padding: 20px 0 60px; }
  
  .product-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 18px;
  }
  @media (min-width: 900px) {
    .product-grid { grid-template-columns: 56% 44%; align-items: start; }
  }
  
  .product-panel {
    border: 1px solid rgba(148, 163, 184, 0.18);
    border-radius: 24px;
    background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.06);
  }
  
  .leftCol {
    display: flex;
    flex-direction: column;
    gap: 14px;
    padding: 14px;
  }
  
  .media {
    position: relative;
    border-radius: 18px;
    overflow: hidden;
    background: var(--rm-soft);
    height: 440px;
  }
  @media (min-width: 1200px) { .media { height: 470px; } }
  
  .img { width: 100%; height: 100%; object-fit: cover; display: block; }
  
  .badge {
    position: absolute;
    left: 16px;
    top: 16px;
    z-index: 20;
    font-size: 13px;
    font-weight: 700;
    color: white;
    padding: 8px 12px;
    border-radius: 999px;
    box-shadow: 0 6px 18px rgba(0,0,0,0.12);
  }
  .badge-new { background: #16a34a; }
  .badge-like-new { background: #4f46e5; }
  .badge-used { background: #374151; }
  
  .fav-btn {
    position: absolute;
    right: 18px;
    bottom: 18px;
    z-index: 30;
    width: 46px;
    height: 46px;
    border-radius: 999px;
    background: rgba(255,255,255,0.95);
    border: 1px solid rgba(0,0,0,0.04);
    display: inline-flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 6px 18px rgba(0,0,0,0.08);
    transition: transform .12s ease;
  }
  .fav-btn:hover { transform: translateY(-3px); }
  .icon { width: 18px; height: 18px; display: block; }
  .fav-on { color: #ef4444; }
  .fav-off { color: #6b7280; }
  
  .thumbs {
    display: flex;
    gap: 12px;
    margin-top: 12px;
  }
  .thumb {
    flex: 1 1 0;
    height: 96px;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid var(--rm-border);
    padding: 0;
    background: transparent;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
  .thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }
  
  .thumb.active {
    outline: 3px solid rgba(16,185,129,0.18);
    box-shadow: 0 8px 20px rgba(16,185,129,0.06);
    transform: translateY(-2px);
  }
  
  .rightCol {
    display: flex;
    flex-direction: column;
    padding: 24px 24px 22px;
  }
  
  .title {
    margin: 4px 0 10px;
    font-size: clamp(2rem, 3vw, 2.85rem);
    font-weight: 800;
    line-height: 1.02;
    letter-spacing: -0.04em;
    color: #0f172a;
  }
  
  @media (max-width: 1200px) { .title { font-size: clamp(1.9rem, 3vw, 2.6rem); 
} }
  @media (max-width: 960px) { .title { font-size: 1.9rem; } }
  
  .priceWrap { margin-bottom: 12px; }
  .price {
    font-weight: 850;
    font-size: clamp(1.9rem, 2.1vw, 2.35rem);
    color: #0f172a;
    letter-spacing: -0.03em;
  }
  .item-meta-line {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;
    margin-bottom: 18px;
    color: #64748b;
    font-size: 13px;
    font-weight: 500;
  }
  .item-meta-line span {
    line-height: 1.2;
  }
  .meta-sep {
    color: #cbd5e1;
  }
  
  .desc {
    margin: 0 0 18px;
    color: #334155;
    font-size: 0.98rem;
    line-height: 1.65;
    max-width: 56ch;
  }
  
  .actions { display:flex; gap:10px; align-items:center; margin-bottom:14px; 
flex-wrap: wrap; }
  
  .rm-btn {
    min-height: 44px;
    padding: 0 16px;
    border-radius: 999px;
    border: 1px solid transparent;
    font-size: 0.92rem;
    font-weight: 700;
    cursor: pointer;
  }
  
  .rm-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
  
  .rm-btn--primary {
    background: #1fb981;
    color: #fff;
    box-shadow: 0 10px 24px rgba(15, 118, 110, 0.24);
  }
  
  .rm-btn--secondary {
    background: #ffffff;
    color: #1fb981;
    border-color: #99f6e4;
  }
  
  .edit-publication-btn {
    display: block;
    width: 100%;
    min-height: 36px;
    padding: 0 14px;
    border-radius: 999px;
    border: 1px solid #d2dae4;
    font-size: 0.85rem;
    font-weight: 700;
    cursor: pointer;
    background: #f8fafc;
    color: #5b6472;
    text-decoration: none;
    text-align: center;
    line-height: 36px;
    transition: all 0.2s ease;
    margin-top: 12px;
  }
  
  .edit-publication-btn:hover {
    border-color: #1fb981;
    background: #f0fdf9;
    color: #1fb981;
  }
  
  .small { font-size: 12px; color: #9aa0a6; margin-top:8px; }
  
  .seller-card {
    margin-top: auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 14px;
    border: 1px solid rgba(148, 163, 184, 0.18);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.82);
    backdrop-filter: blur(8px);
    padding: 14px 16px;
  }
  
  .seller-main {
    display: inline-flex;
    align-items: center;
    gap: 12px;
    min-width: 0;
  }
  
  .seller-avatar {
    width: 52px;
    height: 52px;
    border-radius: 999px;
    background: linear-gradient(135deg, #1fb981, #0f766e);
    color: #ffffff;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 1.55rem;
    font-weight: 700;
    overflow: hidden;
    flex-shrink: 0;
  }
  
  .seller-avatar__img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .seller-copy {
    min-width: 0;
  }
  
  .seller-name {
    margin: 0;
    font-size: 0.98rem;
    font-weight: 700;
    color: #0f172a;
    line-height: 1.2;
  }
  
  .seller-reputation {
    margin: 4px 0 0;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    color: #5b6472;
    font-size: 0.9rem;
  }
  
  .seller-stars {
    color: #f59e0b;
    letter-spacing: 0.02em;
    font-size: 0.95rem;
    line-height: 1;
  }
  
  .seller-open {
    width: 40px;
    height: 40px;
    border-radius: 999px;
    border: 1px solid #d2dae4;
    background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
    color: #5b6472;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    flex-shrink: 0;
  }
  
  .seller-open__icon {
    width: 16px;
    height: 16px;
    display: block;
  }
  
  .seller-open:hover {
    border-color: #1fb981;
    color: #1fb981;
  }
  
  .seller-open:disabled {
    opacity: 0.6;
    cursor: default;
  }
  
  .separator { border: 0; border-top: 1px solid var(--rm-border); margin: 28px 
0; }
  .related { margin-top: 18px; }
  .relatedTitle {
    font-weight: 800;
    font-size: 1.3rem;
    margin: 6px 0 12px;
    color: #0f172a;
  }
  .relatedGrid { display:grid; grid-template-columns: repeat(2,1fr); gap: 
12px; }
  
  @media (min-width: 900px) { .relatedGrid { grid-template-columns: 
repeat(4,1fr); } }
  
  .notfound { padding: 16px; border: 1px dashed var(--rm-border); 
border-radius:8px; }
  
  .checkout-msg {
    margin: 0;
    font-size: 0.92rem;
  }
  
  .checkout-msg--ok {
    color: #0f766e;
  }
  
  .checkout-msg--error {
    color: #b91c1c;
  }
  
  .checkout-modal {
    position: fixed;
    inset: 0;
    z-index: 4000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
  }
  
  .checkout-overlay {
    position: absolute;
    inset: 0;
    background: rgba(15, 23, 42, 0.62);
    backdrop-filter: blur(3px);
    border: 0;
  }

  .checkout-shell {
    position: relative;
    z-index: 1;
    width: min(1180px, 100%);
    max-height: calc(100vh - 40px);
    display: grid;
    grid-template-columns: minmax(0, 1fr) 320px;
    gap: 16px;
    align-items: start;
  }
  
  .checkout-card {
    max-height: calc(100vh - 40px);
    overflow-y: auto;
    border-radius: 20px;
    border: 1px solid #dbe4ee;
    background: linear-gradient(180deg, #ffffff 0%, #f7fbff 100%);
    box-shadow: 0 28px 64px rgba(15, 23, 42, 0.24);
    padding: 22px;
  }
  
  .checkout-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
  }
  
  .checkout-head h3 {
    margin: 0;
    font-size: 1.45rem;
    color: #0f172a;
  }
  
  .checkout-close {
    border: 1px solid #d1d5db;
    background: #ffffff;
    width: 40px;
    height: 40px;
    border-radius: 999px;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    color: #1f2937;
    transition: border-color .16s ease, background-color .16s ease, color .16s ease;
  }

  .checkout-close:hover {
    border-color: #9ca3af;
    background: #f8fafc;
  }

  .checkout-close:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }

  .checkout-close__icon {
    width: 16px;
    height: 16px;
    display: block;
  }
  
  .checkout-subtitle {
    margin: 6px 0 0;
    color: #475569;
    font-size: 0.95rem;
  }

  .checkout-steps {
    margin-top: 14px;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }

  .checkout-step {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #dbe4ee;
    border-radius: 999px;
    padding: 8px 10px;
    color: #64748b;
    background: #f8fafc;
    font-size: 0.86rem;
    font-weight: 600;
  }

  .checkout-step--active {
    color: #065f46;
    border-color: #99f6e4;
    background: #ecfdf5;
  }

  .checkout-step__num {
    width: 22px;
    height: 22px;
    border-radius: 999px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: #e2e8f0;
    color: #0f172a;
    font-weight: 800;
    font-size: 0.78rem;
  }

  .checkout-step--active .checkout-step__num {
    background: #10b981;
    color: #ffffff;
  }
  
  .checkout-form {
    display: flex;
    flex-direction: column;
    gap: 14px;
    margin-top: 16px;
  }
  
  .checkout-block {
    border: 1px solid #e2e8f0;
    border-radius: 14px;
    padding: 14px;
    background: #f8fbff;
  }

  .checkout-block--soft {
    background: #f4faf7;
    border-color: #d1fae5;
  }
  
  .checkout-label {
    margin: 0 0 8px;
    font-weight: 700;
    color: #0f172a;
    font-size: 0.95rem;
  }

  .field-required {
    color: #dc2626;
    font-weight: 800;
  }

  .checkout-help {
    margin: 8px 0 0;
    color: #64748b;
    font-size: 0.82rem;
  }

  .checkout-help--secure {
    display: inline-flex;
    align-items: center;
    gap: 6px;
  }

  .checkout-help--secure::before {
    content: "\1F512";
    font-size: 0.9rem;
    line-height: 1;
  }

  .checkout-product {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .checkout-product__img {
    width: 74px;
    height: 74px;
    border-radius: 10px;
    object-fit: cover;
    border: 1px solid #dbe4ee;
    flex-shrink: 0;
  }

  .checkout-product__copy {
    min-width: 0;
  }

  .checkout-product__title {
    margin: 0;
    color: #0f172a;
    font-size: 1rem;
    font-weight: 700;
  }

  .checkout-product__meta {
    margin: 2px 0 0;
    color: #64748b;
    font-size: 0.9rem;
  }

  .checkout-product__price {
    margin: 6px 0 0;
    color: #0f172a;
    font-weight: 800;
  }

  .checkout-option {
    border: 1px solid #b7efd8;
    background: #ecfdf5;
    border-radius: 12px;
    padding: 12px;
    display: grid;
    gap: 4px;
  }

  .checkout-option__main {
    color: #065f46;
    font-weight: 700;
  }

  .checkout-option__sub {
    color: #0f766e;
    font-size: 0.9rem;
  }

  .checkout-option__chip {
    margin-top: 2px;
    display: inline-flex;
    width: fit-content;
    font-size: 0.78rem;
    font-weight: 700;
    color: #065f46;
    background: #d1fae5;
    border-radius: 999px;
    padding: 4px 9px;
  }
  
  .checkout-info {
    margin: 8px 0 0;
    padding: 8px 12px;
    background: #f0fdf4;
    border-left: 3px solid #1fb981;
    border-radius: 4px;
    font-size: 0.93rem;
    color: #0f766e;
    line-height: 1.4;
  }
  
  .checkout-switch {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
  }
  
  .checkout-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }
  
  .checkout-input {
    min-height: 42px;
    border-radius: 10px;
    border: 1px solid #cbd5e1;
    background: #ffffff;
    padding: 0 12px;
    font-size: 0.94rem;
  }

  .checkout-input:focus {
    outline: none;
    border-color: #1fb981;
    box-shadow: 0 0 0 3px rgba(31, 185, 129, 0.16);
  }
  
  .checkout-input--full {
    grid-column: 1 / -1;
  }

  .payment-methods {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
    margin-bottom: 10px;
  }

  .payment-method {
    border: 1px solid #dbe4ee;
    background: #ffffff;
    border-radius: 12px;
    min-height: 72px;
    padding: 10px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: center;
    gap: 6px;
    color: #334155;
    transition: border-color .18s ease, box-shadow .18s ease;
  }

  .payment-method--active {
    border-color: #10b981;
    background: linear-gradient(180deg, #ecfdf5 0%, #e8faf3 100%);
    box-shadow: 0 8px 18px rgba(16, 185, 129, 0.12);
  }

  .payment-method:disabled {
    opacity: 0.72;
    background: #f8fafc;
    cursor: not-allowed;
  }

  .payment-method__name {
    font-size: 0.92rem;
    font-weight: 700;
    line-height: 1;
  }

  .payment-method__top {
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 8px;
  }

  .payment-method__status {
    border-radius: 999px;
    border: 1px solid #dbe4ee;
    background: #ffffff;
    color: #64748b;
    font-size: 0.68rem;
    font-weight: 700;
    padding: 3px 8px;
    line-height: 1;
  }

  .payment-method__status--active {
    border-color: #34d399;
    background: #dcfce7;
    color: #166534;
  }

  .payment-method__logos {
    display: inline-flex;
    gap: 6px;
  }

  .logo-chip {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-height: 24px;
    border-radius: 999px;
    padding: 0 10px;
    font-size: 0.92rem;
    font-weight: 800;
    letter-spacing: 0.02em;
    border: 1px solid transparent;
  }

  .logo-chip :deep(svg) {
    width: auto;
    height: 0.92rem;
  }

  .logo-chip--visa {
    background: linear-gradient(180deg, #e0ecff 0%, #dbeafe 100%);
    color: #1d4ed8;
    border-color: #bfdbfe;
  }

  .logo-chip--mc {
    background: linear-gradient(180deg, #fff1f2 0%, #ffe4e6 100%);
    color: #be123c;
    border-color: #fecdd3;
  }

  .logo-chip--paypal {
    background: linear-gradient(180deg, #e0f2fe 0%, #cffafe 100%);
    color: #0369a1;
    border-color: #bae6fd;
  }

  .logo-chip--apple {
    background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
    color: #0f172a;
    border-color: #cbd5e1;
  }

  .payment-grid {
    margin-top: 2px;
  }
  
  .checkout-actions {
    margin-top: 4px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
  }

  .checkout-mobile-total {
    display: none;
  }

  .checkout-summary {
    border: 1px solid #dbe4ee;
    border-radius: 20px;
    background: #ffffff;
    box-shadow: 0 20px 40px rgba(15, 23, 42, 0.14);
    padding: 18px;
    position: sticky;
    top: 0;
  }

  .checkout-summary h4 {
    margin: 0;
    font-size: 1.2rem;
    color: #0f172a;
  }

  .summary-list {
    list-style: none;
    margin: 14px 0;
    padding: 0;
    border-bottom: 1px solid #e2e8f0;
  }

  .summary-list li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 10px;
    margin-bottom: 10px;
    color: #334155;
  }

  .summary-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 8px 0 12px;
    font-size: 1.08rem;
    color: #0f172a;
    font-weight: 800;
  }

  .summary-pay {
    width: 100%;
    min-height: 48px;
    border: 0;
    border-radius: 12px;
    background: linear-gradient(135deg, #1fb981 0%, #10b981 100%);
    color: #ffffff;
    font-size: 1rem;
    font-weight: 800;
    cursor: pointer;
    box-shadow: 0 12px 26px rgba(16, 185, 129, 0.28);
  }

  .summary-pay:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }

  .summary-note {
    margin: 10px 0 0;
    color: #64748b;
    font-size: 0.86rem;
    text-align: center;
  }
  
  @media (max-width: 899px) {
    .media { height: 320px; }
    .thumb { height: 84px; }
    .rm-container {
      padding-left: 14px;
      padding-right: 14px;
    }
    .rightCol {
      padding: 16px;
    }
    .seller-card {
      margin-top: 14px;
    }
    .checkout-grid {
      grid-template-columns: 1fr;
    }
    .payment-methods {
      grid-template-columns: 1fr;
    }
    .checkout-shell {
      grid-template-columns: 1fr;
      max-height: calc(100vh - 20px);
      gap: 10px;
    }
    .checkout-modal {
      padding: 10px;
      align-items: flex-start;
    }
    .checkout-card {
      max-height: calc(100vh - 74px);
      border-radius: 16px;
      padding: 14px 14px calc(94px + env(safe-area-inset-bottom));
    }
    .checkout-steps {
      grid-template-columns: 1fr;
    }
    .checkout-summary {
      position: static;
      border-radius: 16px;
      padding: 14px;
      display: none;
    }
    .checkout-actions--mobile {
      display: none;
    }
    .checkout-mobile-total {
      position: fixed;
      left: 10px;
      right: 10px;
      bottom: max(10px, env(safe-area-inset-bottom));
      z-index: 2;
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 10px;
      border: 1px solid #dbe4ee;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(4px);
      box-shadow: 0 16px 32px rgba(15, 23, 42, 0.16);
      padding: 10px;
    }
    .checkout-mobile-total__label {
      margin: 0;
      color: #64748b;
      font-size: 0.78rem;
      line-height: 1.2;
    }
    .checkout-mobile-total__value {
      margin: 2px 0 0;
      color: #0f172a;
      font-size: 1rem;
      font-weight: 800;
      line-height: 1.2;
    }
    .checkout-mobile-total__pay {
      min-height: 44px;
      border: 0;
      border-radius: 10px;
      padding: 0 14px;
      background: linear-gradient(135deg, #1fb981 0%, #10b981 100%);
      color: #fff;
      font-size: 0.92rem;
      font-weight: 800;
      cursor: pointer;
      white-space: nowrap;
    }
    .checkout-mobile-total__pay:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
</style>



