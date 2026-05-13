<template>
  <div>
    <UiMessageCenter />
    <MainHeader v-if="!$route.path.startsWith('/auth')" />

    <main class="main">
      <NuxtPage />
    </main>

    <MainFooter v-if="!$route.path.startsWith('/auth')" />
  </div>
</template>

<script setup lang="ts">
import MainHeader from '~/components/layout/MainHeader.vue'
import MainFooter from '~/components/layout/MainFooter.vue'
import UiMessageCenter from '~/components/UiMessageCenter.vue'
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
