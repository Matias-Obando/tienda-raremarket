<template>
  <div>
    <MainHeader />
    <CategoryNav />

    <main class="main">
      <NuxtPage />
    </main>

    <MainFooter />
  </div>
</template>

<script setup lang="ts">
import MainHeader from './components/layout/MainHeader.vue'
import CategoryNav from './components/layout/CategoryNav.vue'
import MainFooter from './components/layout/MainFooter.vue'
</script>

<style>
html, body { margin:0; padding:0; }
* { box-sizing: border-box; }
body {
  font-family: system-ui, -apple-system, Segoe UI, Roboto, Arial, sans-serif;
  background:#fff;
  color:#111;
}
img { max-width: 100%; }
.main { min-height: 60vh; }
</style>