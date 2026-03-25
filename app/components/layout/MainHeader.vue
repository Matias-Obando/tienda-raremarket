<template>
  <header class="sticky top-0 z-50 bg-white pt-3 pb-0">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
     <div class="flex items-center justify-between gap-4">

        <div class="flex items-center gap-4 flex-1 min-w-0">
          <nuxt-link to="/" class="flex items-center gap-3 shrink-0">
            <img :src="logo" alt="Closely" class="h-10 md:h-12 w-auto rounded-md" />
          </nuxt-link>


          <div class="hidden md:flex flex-1 px-2">
            <form @submit.prevent="onSearch" class="w-full max-w-2xl">
              <label for="header-search" class="sr-only">Buscar artículos</label>
              <div class="relative">
                <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400">

                  <svg class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor" aria-hidden="true">
                    <path fill-rule="evenodd" d="M12.9 14.32a8 8 0 111.414-1.414l4.387 4.387-1.414 1.414-4.387-4.387zM8.5 15a6.5 6.5 0 100-13 6.5 6.5 0 000 13z" clip-rule="evenodd"/>
                  </svg>
                </span>

                <input
                  id="header-search"
                  v-model="query"
                  type="search"
                  placeholder="Busca artículos"
                  class="w-full bg-gray-50 rounded-full py-3 pl-12 pr-4 text-sm placeholder-gray-400 border border-transparent focus:outline-none focus:ring-2 focus:ring-teal-200 focus:bg-white"
                />
              </div>
            </form>
          </div>
        </div>


        <div class="flex items-center gap-3 shrink-0">

          <div class="hidden md:flex items-center gap-3">

            <div class="inline-flex items-center rounded-full border border-teal-300 text-teal-600 overflow-hidden">
              <NuxtLink
                :to="{ path: '/auth', query: { mode: 'register' } }"
                class="px-4 py-2 text-sm font-medium bg-white hover:bg-teal-50 focus:outline-none"
                >Regístrate</NuxtLink>

              <span class="px-2 text-sm text-teal-400 select-none" aria-hidden="true">|</span>

              <NuxtLink
                :to="{ path: '/auth', query: { mode: 'login' } }"
                class="px-4 py-2 text-sm font-medium bg-white hover:bg-teal-50 focus:outline-none"
                >Inicia sesión</NuxtLink>
            </div>


            <NuxtLink to="/vender" class="ml-1 bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-full text-sm">
              Vender ahora
            </NuxtLink>

            <div v-if="user" class="flex items-center gap-2">

              <button class="p-2 rounded-full hover:bg-gray-100" aria-label="Mensajes">
                <svg class="h-5 w-5 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>
              </button>
              <button class="p-2 rounded-full hover:bg-gray-100" aria-label="Notificaciones">
                <svg class="h-5 w-5 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6 6 0 10-12 0v3.159c0 .538-.214 1.055-.595 1.436L4 17h5"/></svg>
              </button>

              <div class="relative" ref="avatarRef">
                <button @click="toggleDropdown" class="flex items-center gap-2 focus:outline-none" :aria-expanded="dropdownOpen">
                  <img :src="user.avatar || '/images/avatar-placeholder.png'" alt="avatar" class="h-9 w-9 rounded-full object-cover border" />
                </button>
                <transition name="fade">
                  <div v-if="dropdownOpen" class="absolute right-0 mt-2 w-44 bg-white border rounded-md shadow-lg py-1 z-50" role="menu" aria-label="User menu">
                    <nuxt-link to="/perfil" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" role="menuitem">Mi perfil</nuxt-link>
                    <nuxt-link to="/mis-publicaciones" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" role="menuitem">Mis publicaciones</nuxt-link>
                    <button @click="$emit('signout')" class="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-50" role="menuitem">Cerrar sesión</button>
                  </div>
                </transition>
              </div>
            </div>
          </div>


          <div class="flex items-center gap-2 md:hidden">

            <button @click="openMobileSearch" class="p-2 rounded-full hover:bg-gray-100" aria-label="Buscar">
              <svg class="h-6 w-6 text-gray-700" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M12.9 14.32a8 8 0 111.414-1.414l4.387 4.387-1.414 1.414-4.387-4.387zM8.5 15a6.5 6.5 0 100-13 6.5 6.5 0 000 13z" clip-rule="evenodd"/></svg>
            </button>

            <button @click="$emit('sell')" class="bg-teal-600 text-white px-3 py-2 rounded-full text-sm">Vender</button>

            <button @click="toggleMobile" class="p-2 rounded-md ml-1 focus:outline-none" aria-label="Abrir menú">
              <svg v-if="!mobileOpen" class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M4 6h16M4 12h16M4 18h16"/></svg>
              <svg v-else class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
          </div>


          <div class="hidden md:block text-sm text-gray-600 ml-2">ES</div>
        </div>
      </div>
    </div>


    <transition name="slide-fade">
      <div v-if="mobileOpen" class="fixed inset-0 z-60">
        <div class="absolute inset-0 bg-black/30" @click="toggleMobile" aria-hidden="true"></div>
        <aside class="absolute right-0 top-0 bottom-0 w-full sm:w-80 bg-white p-4 overflow-auto">
          <div class="flex items-center justify-between mb-4">
            <nuxt-link to="/" class="flex items-center gap-3">
              <img :src="logo" alt="Closely" class="h-10 w-auto" />
            </nuxt-link>
            <button @click="toggleMobile" aria-label="Cerrar" class="p-2 rounded-md">
              <svg class="h-6 w-6 text-gray-700" viewBox="0 0 24 24" fill="none" stroke="currentColor"><path d="M6 18L18 6M6 6l12 12"/></svg>
            </button>
          </div>

          <div class="mb-4">
            <input v-model="query" placeholder="Busca artículos" class="w-full rounded-full py-3 pl-4 pr-4 bg-gray-50 border" />
          </div>

          <div class="space-y-3 mb-4">
            <button @click="$emit('open-register')" class="w-full py-3 rounded-full border text-teal-600">Regístrate</button>
            <button @click="$emit('open-login')" class="w-full py-3 rounded-full bg-teal-600 text-white">Inicia sesión</button>
            <button @click="$emit('sell')" class="w-full py-3 rounded-full bg-teal-600 text-white">Vender ahora</button>
          </div>

          <div class="border-t pt-3">
            <div class="flex flex-col gap-2">
              <button v-for="(c,i) in categories" :key="'mob-'+i" @click="$emit('select-category', c)" class="text-left py-2 text-sm text-gray-700 hover:text-teal-600">
                {{ c }}
              </button>
            </div>
          </div>
        </aside>
      </div>
    </transition>


    <transition name="fade">
      <div v-if="mobileSearchOpen" class="fixed inset-0 z-70 flex items-start justify-center p-4">
        <div class="absolute inset-0 bg-black/30" @click="closeMobileSearch"></div>
        <div class="relative w-full max-w-lg">
          <div class="bg-white rounded-xl p-4">
            <div class="flex items-center gap-3">
              <input v-model="query" @keyup.enter="onSearchMobile" placeholder="Busca artículos" class="w-full rounded-full py-3 pl-4 pr-4 bg-gray-50 border" />
              <button @click="onSearchMobile" class="ml-2 bg-teal-600 text-white px-4 py-2 rounded-full">Buscar</button>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </header>

      <CategoryNav :categories="categories"/>

</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import logoAsset from '~/assets/photos/closely-logo.png'

import CategoryNav from '~/components/layout/CategoryNav.vue'

const props = withDefaults(defineProps<{ user?: { name?: string; avatar?: string } | null }>(), { user: null })
const user = props.user ?? null
const emit = defineEmits<{
  (e: 'open-login'): void
  (e: 'open-register'): void
  (e: 'signout'): void
  (e: 'sell'): void
  (e: 'search', q: string): void
  (e: 'select-category', c: string): void
}>()

const logo = logoAsset

const query = ref('')
function onSearch() { emit('search', query.value) }
function onSearchMobile() { onSearch(); closeMobileSearch() }

const dropdownOpen = ref(false)
const mobileOpen = ref(false)
const mobileSearchOpen = ref(false)
const avatarRef = ref<HTMLElement | null>(null)

function toggleDropdown() { dropdownOpen.value = !dropdownOpen.value }
function toggleMobile() { mobileOpen.value = !mobileOpen.value }
function openMobileSearch() { mobileSearchOpen.value = true }
function closeMobileSearch() { mobileSearchOpen.value = false }

function onOutsideClick(e: MouseEvent) {
  const el = avatarRef.value
  if (!el) return
  if (!(el as HTMLElement).contains(e.target as Node)) dropdownOpen.value = false
}
onMounted(() => document.addEventListener('click', onOutsideClick))
onBeforeUnmount(() => document.removeEventListener('click', onOutsideClick))

const categories = [
  'Recién llegados','Abrigos','Chaquetas','Jerséis & Sudaderas','Vestidos',
  'Camisas & Camisetas','Pantalones','Vaqueros','Calzado','Bolsos'
]
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity .15s ease }
.fade-enter-from, .fade-leave-to { opacity: 0 }
.slide-fade-enter-active { transition: transform .18s ease, opacity .18s ease }
.slide-fade-enter-from { transform: translateX(8px); opacity: 0 }


header img { height: auto; max-height: 56px; }


.z-60 { z-index: 60; }
.z-70 { z-index: 70; }
</style>