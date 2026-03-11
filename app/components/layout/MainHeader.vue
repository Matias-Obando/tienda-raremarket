<template>
  <header class="rm-header">
    <div class="rm-container rm-header__inner">
      <NuxtLink to="/" class="rm-header__logo" aria-label="Closely">
        Closely
      </NuxtLink>

      <form class="rm-header__search" role="search" @submit.prevent="submitSearch">
        <input
          v-model.trim="q"
          class="rm-input rm-header__searchInput"
          type="search"
          placeholder="Busca artículos"
          aria-label="Buscar artículos"
        />
      </form>

      <div class="rm-header__actions">
        <button class="rm-btn rm-btn--ghost rm-header__iconBtn" type="button" aria-label="Favoritos">
          ♥
        </button>
        <button class="rm-btn rm-btn--ghost rm-header__iconBtn" type="button" aria-label="Notificaciones">
          🔔
        </button>

        <NuxtLink to="/perfil" class="rm-btn rm-btn--ghost rm-header__iconBtn" aria-label="Mi perfil">
          👤
        </NuxtLink>

        <NuxtLink to="/vender" class="rm-btn rm-btn--primary rm-header__sell">
          Vender
        </NuxtLink>

        <button class="rm-btn rm-btn--ghost rm-header__lang" type="button" aria-label="Idioma">
          ES
        </button>
      </div>
    </div>

   
    <div class="rm-header__mobileSearch">
      <div class="rm-container">
        <form role="search" @submit.prevent="submitSearch">
          <input
            v-model.trim="q"
            class="rm-input rm-header__searchInput"
            type="search"
            placeholder="Busca artículos"
            aria-label="Buscar artículos"
          />
        </form>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
const route = useRoute()

const q = ref('')

const syncingFromRoute = ref(false)

let t: ReturnType<typeof setTimeout> | null = null

function buildNextQuery(input: string) {
  const nextQuery: Record<string, any> = {}
  if (route.path === '/explorar') Object.assign(nextQuery, route.query)
  const val = input.trim()
  if (val) nextQuery.q = val
  else delete nextQuery.q

  return nextQuery
}


watchEffect(() => {
  if (route.path !== '/explorar') return
  syncingFromRoute.value = true
  const v = route.query.q
  q.value = typeof v === 'string' ? v : ''
  syncingFromRoute.value = false
})

watch(
  () => q.value,
  (val) => {
    if (syncingFromRoute.value) return

    if (t) clearTimeout(t)
    t = setTimeout(() => {
      const nextQuery = buildNextQuery(val)  
      navigateTo({ path: '/explorar', query: nextQuery })
    }, 300)
  },
)


function submitSearch() {
  if (t) {
    clearTimeout(t)
    t = null
  }
  const nextQuery = buildNextQuery(q.value)
  navigateTo({ path: '/explorar', query: nextQuery })
}
</script>

<style scoped>
.rm-header {
  position: sticky;
  top: 0;
  z-index: 50;
  background: #fff;
  border-bottom: 1px solid var(--rm-border);
}

.rm-header__inner {
  height: 64px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 16px;
  align-items: center;
}

.rm-header__logo {
  font-weight: 900;
  letter-spacing: -0.02em;
  font-size: 22px;
}

.rm-header__search {
  display: none;
}

.rm-header__searchInput {
  max-width: 680px;
}

.rm-header__actions {
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.rm-btn--ghost {
  background: #fff;
}

.rm-header__iconBtn {
  width: 40px;
  padding: 0;
}

.rm-header__sell {
  display: inline-flex;
}

.rm-header__lang {
  display: inline-flex;
  
}

.rm-header__mobileSearch {
  display: block;
  padding: 10px 0 12px;
  background: #fff;
}

@media (min-width: 900px) {
  .rm-header__inner {
    height: 72px;
  }

  .rm-header__search {
    display: block;
  }

  .rm-header__mobileSearch {
    display: none;
  }

  .rm-header__sell,
  .rm-header__lang {
    display: inline-flex;
  }
}
</style>
