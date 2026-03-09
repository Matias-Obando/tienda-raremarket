<template>
  <header class="header">
    <div class="inner">
      <!-- LEFT -->
      <NuxtLink to="/" class="brand">RareMarket</NuxtLink>

      <!-- CENTER (desktop) -->
      <div class="search desktopOnly">
        <button class="select" type="button">
          Artículos <span class="chev">▾</span>
        </button>

        <div class="inputWrap">
          <span class="sIcon">🔎</span>
          <input
            v-model="q"
            class="input"
            type="text"
            placeholder="Busca artículos"
            @keydown.enter="goSearch"
          />
          <button class="camera" type="button" aria-label="Buscar por imagen">📷</button>
        </div>
      </div>

      <!-- RIGHT -->
      <div class="actions">
        <button class="aIcon desktopOnly" type="button" aria-label="Mensajes">✉️</button>
        <button class="aIcon desktopOnly" type="button" aria-label="Notificaciones">🔔</button>
        <button class="aIcon desktopOnly" type="button" aria-label="Favoritos">♡</button>

        <NuxtLink class="aIcon" to="/perfil" aria-label="Perfil">👤</NuxtLink>

        <NuxtLink class="sell" to="/vender">Vender ahora</NuxtLink>

        <button class="lang desktopOnly" type="button">
          ES <span class="chev">▾</span>
        </button>

        <!-- Mobile: search icon toggles search bar -->
        <button class="aIcon mobileOnly" type="button" aria-label="Buscar" @click="mobileSearchOpen = !mobileSearchOpen">
          🔍
        </button>
      </div>
    </div>

    <!-- MOBILE SEARCH (full width) -->
    <div v-if="mobileSearchOpen" class="mobileSearch">
      <div class="mobileInner">
        <div class="inputWrap">
          <span class="sIcon">🔎</span>
          <input
            v-model="q"
            class="input"
            type="text"
            placeholder="Busca artículos"
            @keydown.enter="goSearch"
          />
          <button class="camera" type="button" aria-label="Buscar por imagen">📷</button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
const q = ref('')
const mobileSearchOpen = ref(false)

function goSearch() {
  // Mock: manda a explorar con query
  // (luego conectamos filtro real)
  return navigateTo({
    path: '/explorar',
    query: q.value ? { q: q.value } : {},
  })
}
</script>

<style scoped>
.header{
  background:#fff;
  border-bottom:1px solid #e9e9e9;
}

/* Desktop container */
.inner{
  max-width: 1320px;
  margin: 0 auto;
  padding: 12px 16px;

  display:flex;
  align-items:center;
  gap:16px;

  min-width:0;
}

.brand{
  text-decoration:none;
  color:#111;
  font-weight:900;
  font-size:22px;
  white-space:nowrap;
}

/* Search */
.search{
  flex: 1;
  display:flex;
  align-items:center;
  gap:10px;
  min-width:0;
}

.select{
  border:1px solid #e9e9e9;
  background:#f6f7f7;
  color:#111;
  padding:10px 12px;
  border-radius:10px;
  font-weight:800;
  cursor:pointer;
  white-space:nowrap;
}

.inputWrap{
  flex:1;
  display:flex;
  align-items:center;
  gap:8px;

  background:#f6f7f7;
  border:1px solid #e9e9e9;
  border-radius:10px;
  padding:0 10px;

  min-width:0;
}

.sIcon{ opacity:.7; }

.input{
  flex:1;
  border:0;
  outline:none;
  background:transparent;
  padding:10px 4px;
  font-size:14px;
  min-width:0;
}

.camera{
  border:0;
  background:transparent;
  cursor:pointer;
  padding:8px;
  border-radius:8px;
}
.camera:hover{ background:#ececec; }

.actions{
  display:flex;
  align-items:center;
  gap:10px;
  white-space:nowrap;
}

.aIcon{
  border:0;
  background:transparent;
  cursor:pointer;
  padding:8px;
  border-radius:10px;
  text-decoration:none;
  color:#111;
}
.aIcon:hover{ background:#f3f3f3; }

/* Tu CTA en rojo */
.sell{
  text-decoration:none;
  background:#d80000;
  color:#fff;
  font-weight:900;
  padding:10px 14px;
  border-radius:10px;
}
.sell:hover{ filter: brightness(.95); }

.lang{
  border:1px solid #e9e9e9;
  background:#fff;
  padding:9px 10px;
  border-radius:10px;
  font-weight:800;
  cursor:pointer;
}

.chev{ font-size:12px; opacity:.8; }

/* Mobile search area */
.mobileSearch{
  border-top:1px solid #eee;
  background:#fff;
}
.mobileInner{
  max-width: 1320px;
  margin: 0 auto;
  padding: 10px 16px;
}

/* Responsive rules */
.mobileOnly{ display:none; }
.desktopOnly{ display:flex; }

@media (max-width: 860px){
  .desktopOnly{ display:none; }
  .mobileOnly{ display:inline-flex; }

  .inner{
    justify-content:space-between;
    gap:10px;
  }

  /* en móvil el botón vender un poco más compacto */
  .sell{
  text-decoration:none;
  background:#d80000;
  color:#fff;
  font-weight:900;
  padding:10px 14px;
  border-radius:12px;
  box-shadow: 0 2px 10px rgba(216,0,0,.18);
}
}
</style>