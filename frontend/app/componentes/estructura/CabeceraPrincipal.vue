<template>
  <header class="sticky top-0 z-[90] bg-white border-b border-gray-200 pt-0 pb-0 shadow-none">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-2 md:py-3">
      <div class="flex items-center justify-between gap-4 sm:gap-5">
        <div class="flex items-center gap-4 flex-1 min-w-0">
          <NuxtLink to="/" class="flex items-center gap-3 shrink-0">
            <img :src="logo" alt="Closely" class="h-12 sm:h-16 md:h-24 w-auto rounded-md" />
          </NuxtLink>

          <div class="hidden md:flex flex-1 px-2">
            <form @submit.prevent="onSearch" class="w-full max-w-2xl">
              <label for="header-search" class="sr-only">Buscar artículos</label>
              <div class="relative">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">
                  <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fill-rule="evenodd" d="M12.9 14.32a8 8 0 111.414-1.414l4.387 4.387-1.414 1.414-4.387-4.387zM8.5 15a6.5 6.5 0 100-13 6.5 6.5 0 000 13z" clip-rule="evenodd" />
                  </svg>
                </span>

                <input
                  id="header-search"
                  v-model="query"
                  type="search"
                  placeholder="Busca artículos"
                  class="w-full bg-slate-100 rounded-full py-3 pl-12 pr-4 text-sm text-slate-700 placeholder-slate-500 border border-slate-200 focus:outline-none focus:ring-2 focus:ring-emerald-200 focus:bg-white focus:border-emerald-300"
                />
              </div>
            </form>
          </div>
        </div>

        <div class="flex items-center gap-2 sm:gap-3 shrink-0">
          <div class="hidden md:flex items-center gap-3">
            <template v-if="!user">
              <div class="inline-flex items-center rounded-full border border-emerald-300 text-emerald-600 overflow-hidden">
                <NuxtLink to="/autenticacion?mode=register" class="px-4 py-2 text-sm font-medium bg-white hover:bg-emerald-50 focus:outline-none">
                  Regístrate
                </NuxtLink>
                <span class="px-2 text-sm text-emerald-400 select-none" aria-hidden="true">|</span>
                <NuxtLink to="/autenticacion?mode=login" class="px-4 py-2 text-sm font-medium bg-white hover:bg-emerald-50 focus:outline-none">
                  Inicia sesión
                </NuxtLink>
              </div>
            </template>

            <button class="ml-1 bg-emerald-600 hover:bg-emerald-700 text-white px-5 py-2.5 rounded-full text-sm font-semibold shadow-sm transition-colors" @click="handleSell">
              Vender ahora
            </button>

            <div v-if="user" class="flex items-center gap-2">
              <NuxtLink to="/favoritos" class="action-pill" aria-label="Favoritos" title="Favoritos">
                <svg class="h-5 w-5 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78L12 21.23l8.84-8.84a5.5 5.5 0 0 0 0-7.78z"/>
                </svg>
                <span v-if="favoriteCount > 0" class="action-pill-count">{{ favoriteCount }}</span>
              </NuxtLink>

              <NuxtLink
                to="/chat"
                class="action-pill"
                :aria-label="unreadChatCount > 0 ? `Mensajes, ${unreadChatCount} sin leer` : 'Mensajes'"
                title="Mensajes"
              >
                <svg class="h-5 w-5 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                  <path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z" />
                </svg>
                <span v-if="unreadChatCount > 0" class="action-pill-count">{{ unreadChatCount > 99 ? '99+' : unreadChatCount }}</span>
              </NuxtLink>

              <div class="relative" ref="avatarRef">
                <button class="flex items-center gap-2 focus:outline-none" :aria-expanded="dropdownOpen" @click="toggleDropdown">
                  <span class="avatar-circle">
                    <img v-if="userAvatar" :src="userAvatar" alt="Foto de perfil" class="avatar-circle__image" />
                    <span v-else>{{ userInitial }}</span>
                  </span>
                </button>
                <transition name="fade">
                  <div v-if="dropdownOpen" class="absolute right-0 mt-2 w-44 bg-white border rounded-md shadow-lg py-1 z-50" role="menu" aria-label="User menu">
                    <NuxtLink to="/perfil" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" role="menuitem" @click="closeDropdown">Mi perfil</NuxtLink>
                    <NuxtLink to="/chat" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" role="menuitem" @click="closeDropdown">Mis mensajes</NuxtLink>
                    <NuxtLink to="/perfil#mis-publicaciones" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" role="menuitem" @click="closeDropdown">Mis publicaciones</NuxtLink>
                    <button class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-50" role="menuitem" @click="openSignOutConfirm">
                      Cerrar sesión
                    </button>
                  </div>
                </transition>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-1.5 md:hidden">
            <button class="p-2 rounded-full hover:bg-gray-100" aria-label="Buscar" @click="openMobileSearch">
              <svg class="h-6 w-6 text-gray-700" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M12.9 14.32a8 8 0 111.414-1.414l4.387 4.387-1.414 1.414-4.387-4.387zM8.5 15a6.5 6.5 0 100-13 6.5 6.5 0 000 13z" clip-rule="evenodd" />
              </svg>
            </button>

            <NuxtLink
              to="/ayuda"
              class="action-pill"
              aria-label="Centro de asistencia"
              title="Centro de asistencia"
            >
              <svg class="h-5 w-5 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true">
                <path d="M12 18h.01" />
                <path d="M9.09 9a3 3 0 1 1 5.82 1c0 2-3 2-3 5" />
                <circle cx="12" cy="12" r="9" />
              </svg>
            </NuxtLink>

            <button class="bg-emerald-600 text-white px-3 py-2 rounded-full text-sm whitespace-nowrap" @click="handleSell">Vender</button>

            <button class="p-2 rounded-md ml-1 focus:outline-none" aria-label="Abrir menú" @click="toggleMobile">
              <svg v-if="!mobileOpen" class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M4 6h16M4 12h16M4 18h16" />
              </svg>
              <svg v-else class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <NuxtLink
            to="/ayuda"
            class="hidden md:inline-flex items-center justify-center p-2 rounded-full hover:bg-gray-100 mr-2"
            aria-label="Centro de asistencia"
            title="Centro de asistencia"
          >
            <svg class="h-5 w-5 text-slate-500" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
              <circle cx="12" cy="12" r="9.5" />
              <path d="M12 17h.01" />
              <path d="M9.09 9a3 3 0 015.82 1c0 2-3 2-3 5" />
            </svg>
          </NuxtLink>

          <div class="hidden md:block text-sm font-medium text-slate-500 ml-2 tracking-wide">ES</div>
        </div>
      </div>
    </div>

    <transition name="slide-fade">
      <div v-if="mobileOpen" class="fixed inset-0 z-60">
        <div class="absolute inset-0 bg-black/30" aria-hidden="true" @click="toggleMobile"></div>
        <aside class="absolute right-0 top-0 bottom-0 w-full sm:w-80 bg-white p-4 overflow-auto">
          <div class="flex items-center justify-between mb-4">
            <NuxtLink to="/" class="flex items-center gap-3">
              <img :src="logo" alt="Closely" class="h-16 w-auto" />
            </NuxtLink>
            <button class="p-2 rounded-md" aria-label="Cerrar" @click="toggleMobile">
              <svg class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="mb-4">
            <input v-model="query" placeholder="Busca artículos" class="w-full rounded-full py-3 pl-4 pr-4 bg-gray-50 border" />
          </div>

          <div class="space-y-3 mb-4">
            <NuxtLink v-if="!user" to="/autenticacion?mode=register" class="block w-full py-3 rounded-full border text-emerald-600 text-center">
              Regístrate
            </NuxtLink>
            <NuxtLink v-if="!user" to="/autenticacion?mode=login" class="block w-full py-3 rounded-full bg-emerald-600 text-white text-center">
              Inicia sesión
            </NuxtLink>
            <NuxtLink v-if="user" to="/favoritos" class="block w-full py-3 rounded-full border text-gray-700 text-center">
              Favoritos
            </NuxtLink>
            <NuxtLink v-if="user" to="/chat" class="block w-full py-3 rounded-full border text-gray-700 text-center">
              Mis mensajes
            </NuxtLink>
            <NuxtLink to="/ayuda" class="block w-full py-3 rounded-full border text-gray-700 text-center">
              Centro de asistencia
            </NuxtLink>
            <button class="w-full py-3 rounded-full bg-emerald-600 text-white" @click="handleSell">Vender ahora</button>
            <button v-if="user" class="w-full py-3 rounded-full border text-red-600" @click="openSignOutConfirm">Cerrar sesión</button>
          </div>

          <div class="border-t pt-3 mb-2">
            <div class="flex flex-col gap-1">
              <NuxtLink
                v-for="link in navLinks"
                :key="`mobile-${link.to}`"
                :to="link.to"
                class="text-left py-2 px-1 text-sm text-gray-700 hover:text-emerald-600"
                @click="mobileOpen = false"
              >
                {{ link.label }}
              </NuxtLink>
            </div>
          </div>

          <div class="border-t pt-3">
            <div class="flex flex-col gap-2">
              <button v-for="(c, i) in categories" :key="'mob-' + i" class="text-left py-2 text-sm text-gray-700 hover:text-emerald-600" @click="$emit('select-category', c.key)">
                {{ c.label }}
              </button>
            </div>
          </div>
        </aside>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="signOutConfirmOpen" class="fixed inset-0 z-[80] flex items-center justify-center p-4">
        <div class="absolute inset-0 bg-black/45 backdrop-blur-[2px]" aria-hidden="true" @click="closeSignOutConfirm"></div>
        <div class="relative w-full max-w-md rounded-3xl bg-white border border-slate-200 shadow-2xl p-6 sm:p-7">
          <div class="flex items-start gap-4">
            <div class="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-emerald-50 text-emerald-600 border border-emerald-100">
              <svg class="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" aria-hidden="true">
                <path d="M15 12H3m0 0 4-4m-4 4 4 4" />
                <path d="M9 5v-1a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2h-6a2 2 0 0 1-2-2v-1" />
              </svg>
            </div>
            <div class="min-w-0 flex-1">
              <h2 class="text-lg sm:text-xl font-semibold text-slate-900">¿Estás seguro de que quieres cerrar sesión?</h2>
            </div>
          </div>

          <div class="mt-6 flex flex-col-reverse sm:flex-row sm:justify-end gap-3">
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full border border-slate-200 bg-white px-5 py-3 text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors"
              @click="closeSignOutConfirm"
            >
              Cancelar
            </button>
            <button
              type="button"
              class="inline-flex items-center justify-center rounded-full bg-red-600 px-5 py-3 text-sm font-semibold text-white hover:bg-red-700 transition-colors"
              @click="signOut"
            >
              Sí, cerrar sesión
            </button>
          </div>
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="mobileSearchOpen" class="fixed inset-0 z-70 flex items-start justify-center p-4">
        <div class="absolute inset-0 bg-black/30" @click="closeMobileSearch"></div>
        <div class="relative w-full max-w-lg">
          <div class="bg-white rounded-xl p-4">
            <div class="flex items-center gap-3">
              <input v-model="query" @keyup.enter="onSearchMobile" placeholder="Busca artículos" class="w-full rounded-full py-3 pl-4 pr-4 bg-gray-50 border" />
              <button class="ml-2 bg-emerald-600 text-white px-4 py-2 rounded-full" @click="onSearchMobile">Buscar</button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </header>

  <CategoryNav v-if="showCategoryNav" :categories="categories" :show-subcategories="false" />
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import logoAsset from '~/recursos/photos/closely.png'
import CategoryNav from '~/componentes/estructura/NavegacionCategorias.vue'

const { sessionUser, loadSessionUser, clearSessionUser, storageEventName } = useSessionUser()
const { unreadChatCount, refreshUnreadChatCount, startUnreadChatPolling, stopUnreadChatPolling } = useUnreadChatCount()
const route = useRoute()

const emit = defineEmits<{
  (e: 'sell'): void
  (e: 'search', q: string): void
  (e: 'select-category', c: string): void
}>()

const logo = logoAsset
const user = computed(() => sessionUser.value)
const userInitial = computed(() => user.value?.username?.charAt(0).toUpperCase() || 'U')
const userAvatar = computed(() => user.value?.avatarUrl || '')
const query = ref('')
const dropdownOpen = ref(false)
const signOutConfirmOpen = ref(false)
const mobileOpen = ref(false)
const mobileSearchOpen = ref(false)
const avatarRef = ref<HTMLElement | null>(null)
const favoriteCount = ref(0)
const favoritesStorageKey = 'closely:favorites'
const navLinks = [
  { label: 'Inicio', to: '/inicio' },
  { label: 'Mensajes', to: '/chat' },
  { label: 'Vender', to: '/vender' }
]
const showCategoryNav = computed(() => true)

function isActivePath(path: string) {
  return route.path === path || route.path.startsWith(`${path}/`)
}

function onSearch() {
  const trimmed = query.value.trim()
  navigateTo({ path: '/inicio', query: trimmed ? { q: trimmed } : {} })
  emit('search', query.value)
}

function onSearchMobile() {
  onSearch()
  closeMobileSearch()
}

function toggleDropdown() {
  dropdownOpen.value = !dropdownOpen.value
}

function closeDropdown() {
  dropdownOpen.value = false
}

function openSignOutConfirm() {
  dropdownOpen.value = false
  mobileOpen.value = false
  signOutConfirmOpen.value = true
}

function closeSignOutConfirm() {
  signOutConfirmOpen.value = false
}

function toggleMobile() {
  mobileOpen.value = !mobileOpen.value
}

function closeMobileMenus() {
  mobileOpen.value = false
  mobileSearchOpen.value = false
}

function openMobileSearch() {
  mobileSearchOpen.value = true
}

function closeMobileSearch() {
  mobileSearchOpen.value = false
}

function handleSell() {
  closeMobileMenus()
  emit('sell')

  if (!user.value?.token) {
    navigateTo({ path: '/autenticacion', query: { mode: 'login', redirect: '/vender' } })
    return
  }

  navigateTo('/vender')
}

function signOut() {
  clearSessionUser()
  dropdownOpen.value = false
  closeMobileMenus()
  signOutConfirmOpen.value = false
  navigateTo('/')
}

function onOutsideClick(e: MouseEvent) {
  const el = avatarRef.value
  if (!el) {
    return
  }
  if (!(el as HTMLElement).contains(e.target as Node)) {
    dropdownOpen.value = false
  }
}

function onKeyDown(e: KeyboardEvent) {
  if (e.key === 'Escape') {
    if (signOutConfirmOpen.value) {
      closeSignOutConfirm()
      return
    }
    closeDropdown()
  }
}

function syncSession() {
  loadSessionUser()
  void refreshUnreadChatCount()
}

function syncFavorites() {
  if (!import.meta.client) {
    return
  }

  try {
    const raw = localStorage.getItem(favoritesStorageKey)
    const ids = raw ? JSON.parse(raw) : []
    favoriteCount.value = Array.isArray(ids) ? ids.length : 0
  } catch {
    favoriteCount.value = 0
  }
}

onMounted(() => {
  loadSessionUser()
  syncFavorites()
  void refreshUnreadChatCount()
  startUnreadChatPolling()
  const q = route.query.q
  query.value = typeof q === 'string' ? q : ''
  document.addEventListener('click', onOutsideClick)
  document.addEventListener('keydown', onKeyDown)
  window.addEventListener(storageEventName, syncSession)
  window.addEventListener('storage', syncSession)
  window.addEventListener('closely:favs:updated', syncFavorites)
  window.addEventListener('storage', syncFavorites)
})

watch(
  () => route.query.q,
  (value) => {
    query.value = typeof value === 'string' ? value : ''
  }
)

watch(
  () => route.fullPath,
  () => {
    closeDropdown()
    closeMobileMenus()
  }
)

onBeforeUnmount(() => {
  document.removeEventListener('click', onOutsideClick)
  document.removeEventListener('keydown', onKeyDown)
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
  window.removeEventListener('closely:favs:updated', syncFavorites)
  window.removeEventListener('storage', syncFavorites)
  stopUnreadChatPolling()
})

const categories = [
  { key: 'new', label: 'Inicio' },
  { key: 'abrigos', label: 'Abrigos' },
  { key: 'chaquetas', label: 'Chaquetas' },
  { key: 'jerseis', label: 'Jerséis & Sudaderas' },
  { key: 'vestidos', label: 'Vestidos' },
  { key: 'camisas', label: 'Camisas & Camisetas' },
  { key: 'pantalones', label: 'Pantalones' },
  { key: 'vaqueros', label: 'Vaqueros' },
  { key: 'calzado', label: 'Calzado' },
  { key: 'bolsos', label: 'Bolsos' }
]
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-fade-enter-active {
  transition: transform 0.18s ease, opacity 0.18s ease;
}

.slide-fade-enter-from {
  transform: translateX(8px);
  opacity: 0;
}

#header-search {
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8);
}

#header-search::placeholder {
  font-weight: 500;
}

header img {
  height: auto;
  max-height: 56px;
}

.avatar-circle {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #ebfffb 0%, #d9faf2 100%);
  border: 1px solid #8ce8d8;
  color: #1fb981;
  font-weight: 700;
  box-shadow: 0 6px 16px rgba(15, 118, 110, 0.12);
}

.avatar-circle__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.action-pill {
  position: relative;
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: 1px solid #e2e8f0;
  background: #ffffff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s ease, border-color 0.15s ease, transform 0.15s ease;
}

.action-pill:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
  transform: translateY(-1px);
}

.action-pill-count {
  position: absolute;
  top: -4px;
  right: -5px;
  min-width: 17px;
  height: 17px;
  border-radius: 999px;
  padding: 0 4px;
  background: #ef4444;
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  line-height: 17px;
  text-align: center;
  border: 2px solid #fff;
}

.z-60 {
  z-index: 60;
}

.z-70 {
  z-index: 70;
}

.nav-link {
  display: inline-flex;
  align-items: center;
  height: 34px;
  padding: 0 12px;
  border-radius: 999px;
  color: #334155;
  font-size: 14px;
  font-weight: 600;
  transition: background .15s ease, color .15s ease;
}

.nav-link:hover {
  background: #f1f5f9;
  color: #0f172a;
}

.nav-link.active {
  background: #dff8f2;
  color: #1fb981;
}
</style>
