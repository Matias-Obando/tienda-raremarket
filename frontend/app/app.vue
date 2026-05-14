<template>
  <div>
    <UiMessageCenter />
    <MainHeader v-if="!$route.path.startsWith('/autenticacion')" />

    <main class="main">
      <NuxtPage />
    </main>

    <MainFooter v-if="!$route.path.startsWith('/autenticacion')" />
  </div>
</template>

<script setup lang="ts">
import MainHeader from '~/componentes/estructura/CabeceraPrincipal.vue'
import MainFooter from '~/componentes/estructura/PiePaginaPrincipal.vue'
import UiMessageCenter from '~/componentes/CentroMensajes.vue'
</script>

<style>
html {
  overflow-y: scroll;
}

body {
  min-height: 100vh;
}
</style>

<style scoped>
.main {
  min-height: 60vh;
  position: relative;
  z-index: 1;
}
</style>
