<template>
  <div>
    <UiMessageCenter />
    <MainHeader v-if="$route.path !== '/auth'" />

    <main class="main">
      <NuxtPage />
    </main>

    <MainFooter v-if="$route.path !== '/auth'" />
  </div>
</template>

<script setup lang="ts">
import MainHeader from '~/components/layout/MainHeader.vue'
import MainFooter from '~/components/layout/MainFooter.vue'
import UiMessageCenter from '~/components/UiMessageCenter.vue'
</script>

<style scoped>
.main {
  min-height: 60vh;
  position: relative;
  z-index: 1;
}
</style>
