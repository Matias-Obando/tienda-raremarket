<template>
  <div class="profile-page">
    <div v-if="sessionReady && sessionUser" class="profile-card">
      <p class="eyebrow">Cuenta</p>
      <h1>Mi perfil</h1>
      <p class="profile-name">{{ sessionUser.username }}</p>
      <p class="profile-email">{{ sessionUser.email }}</p>
      <NuxtLink to="/chat" class="profile-link">Ir a mensajes</NuxtLink>
    </div>

    <div v-else-if="sessionReady" class="profile-card">
      <h1>Mi perfil</h1>
      <p class="profile-email">Todavía no has iniciado sesión.</p>
      <NuxtLink :to="{ path: '/auth', query: { mode: 'login', redirect: '/perfil' } }" class="profile-link">
        Iniciar sesión
      </NuxtLink>
    </div>
  </div>
</template>

<script setup lang="ts">
const { sessionUser, loadSessionUser, storageEventName } = useSessionUser()
const sessionReady = ref(false)

function syncSession() {
  loadSessionUser()
}

onMounted(() => {
  loadSessionUser()
  sessionReady.value = true
  window.addEventListener(storageEventName, syncSession)
  window.addEventListener('storage', syncSession)
})

onBeforeUnmount(() => {
  window.removeEventListener(storageEventName, syncSession)
  window.removeEventListener('storage', syncSession)
})
</script>

<style scoped>
.profile-page {
  padding: 28px 16px 48px;
}

.profile-card {
  max-width: 720px;
  margin: 0 auto;
  background: #fff;
  border: 1px solid var(--rm-border);
  border-radius: 24px;
  box-shadow: var(--rm-shadow);
  padding: 28px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0f766e;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

.profile-card h1,
.profile-name,
.profile-email {
  margin: 0 0 12px;
}

.profile-name {
  font-size: 24px;
  font-weight: 700;
}

.profile-email {
  color: #64748b;
}

.profile-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  padding: 10px 16px;
  background: #0f766e;
  color: #fff;
  text-decoration: none;
  font-weight: 700;
}
</style>
