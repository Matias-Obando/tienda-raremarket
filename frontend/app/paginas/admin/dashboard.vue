<template>
  <div class="admin-dashboard-page">
    <div class="admin-shell">
      <h1>Panel de Administración</h1>
      <p>Bienvenido, administrador. Aquí puedes gestionar la plataforma.</p>
      <NuxtLink to="/inicio" class="button">Volver</NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  middleware: []
})
</script>

<style scoped>
.admin-dashboard-page { padding: 24px; }
.admin-shell { max-width: 980px; margin: 0 auto; }
</style>
