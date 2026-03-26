<template>
  <div>
    <MainHeader v-if="$route.path !== '/auth'" />
    <CategoryNav v-if="$route.path !== '/auth'" />

    <main class="main">
      <NuxtPage />
    </main>

    <MainFooter v-if="$route.path !== '/auth'" />
  </div>
</template>

<script setup lang="ts">
import MainHeader from '~/components/layout/MainHeader.vue'
import CategoryNav from '~/components/layout/CategoryNav.vue'
import MainFooter from '~/components/layout/MainFooter.vue'
</script>

<style scoped>
.main {
  min-height: 60vh;
}
</style>