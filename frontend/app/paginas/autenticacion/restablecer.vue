<template>
  <div class="auth-page">
    <div class="auth-shell auth-shell--single">
      <div class="auth-panel">
        <div class="auth-brand">
          <NuxtLink to="/autenticacion" class="auth-brand__link">Volver al acceso</NuxtLink>
        </div>

        <div class="auth-panel__header">
          <h1>Crea tu nueva contraseña</h1>
          <p>Usa el enlace que recibiste por correo para definir una contraseña nueva.</p>
        </div>

        <form class="split-form split-form--full" @submit.prevent="submitResetPassword">
          <label>
            <span>Nueva contraseña</span>
            <input v-model="password" type="password" minlength="8" required placeholder="Nueva contraseña" />
          </label>

          <label>
            <span>Confirmar contraseña</span>
            <input v-model="confirmPassword" type="password" minlength="8" required placeholder="Repite la contraseña" />
          </label>

          <div class="split-form-actions">
            <button type="submit" class="split-btn-primary" :disabled="isSubmitting || !token">
              {{ isSubmitting ? 'Guardando...' : 'Actualizar contraseña' }}
            </button>
          </div>
        </form>

        <p v-if="statusMessage" class="split-success">{{ statusMessage }}</p>
        <p v-if="errorMessage" class="split-error">{{ errorMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

definePageMeta({
  alias: ['/auth/reset']
})

const route = useRoute()
const router = useRouter()
const config = useRuntimeConfig()
const uiMessages = useUiMessages()

const password = ref('')
const confirmPassword = ref('')
const isSubmitting = ref(false)
const errorMessage = ref('')
const statusMessage = ref('')

const token = computed(() => {
  const rawToken = route.query.token
  return typeof rawToken === 'string' ? rawToken : ''
})

async function submitResetPassword() {
  errorMessage.value = ''
  statusMessage.value = ''

  if (!token.value) {
    errorMessage.value = 'El enlace de recuperación no incluye un token válido.'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = 'Las contraseñas no coinciden.'
    return
  }

  isSubmitting.value = true

  try {
    const response = await $fetch<{ message: string }>(`${config.public.API_BASE_URL}/users/reset-password`, {
      method: 'POST',
      body: {
        token: token.value,
        password: password.value
      }
    })

    statusMessage.value = response.message
    uiMessages.success(response.message)
    await router.push('/autenticacion?mode=login')
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.data || 'No se pudo actualizar la contraseña.'
    uiMessages.error(errorMessage.value)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  padding: clamp(14px, 2.4vw, 28px);
  background: var(--rm-page-bg);
}

.auth-shell {
  min-height: calc(100vh - clamp(28px, 4.8vw, 56px));
  border-radius: 30px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e7ebf0;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.1);
  display: grid;
}

.auth-shell--single {
  grid-template-columns: minmax(0, 1fr);
}

.auth-panel {
  background: linear-gradient(180deg, #ffffff 0%, #f9fbfd 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: clamp(26px, 3.6vw, 48px);
}

.auth-brand,
.auth-panel__header,
.split-form {
  width: 100%;
  max-width: 420px;
}

.auth-brand {
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.auth-brand__link {
  font-size: 0.82rem;
  font-weight: 600;
  letter-spacing: 0.02em;
  text-transform: uppercase;
  color: #4b5563;
  text-decoration: none;
}

.auth-brand__link:hover {
  color: #1fb981;
}

.auth-panel__header {
  margin-bottom: 16px;
}

.auth-panel__header h1 {
  margin: 0;
  color: #0f172a;
  font-size: clamp(1.9rem, 2.4vw, 2.45rem);
  line-height: 1.08;
  letter-spacing: -0.04em;
  font-weight: 700;
}

.auth-panel__header p {
  margin: 8px 0 0;
  color: #64748b;
  font-size: 1rem;
  line-height: 1.6;
}

.split-form {
  display: grid;
  gap: 14px;
}

.split-form label {
  display: grid;
  gap: 6px;
  color: #475569;
  font-size: 0.95rem;
}

.split-form input {
  width: 100%;
  border: 1px solid #dbe3ec;
  border-radius: 14px;
  padding: 12px 14px;
  background: #fff;
  color: #0f172a;
}

.split-form-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.split-btn-primary {
  width: 100%;
  min-height: 48px;
  background: #1fb981;
  color: #fff;
  border: none;
  border-radius: 999px;
  padding: 0 22px;
  font-size: 1.02rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.split-btn-primary:hover {
  background: #0d655f;
  transform: translateY(-1px);
}

.split-btn-primary:disabled {
  opacity: 0.7;
  cursor: wait;
}

.split-success,
.split-error {
  width: 100%;
  max-width: 420px;
  margin: 16px 0 0;
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 0.95rem;
}

.split-success {
  color: #0f766e;
  background: #ecfeff;
}

.split-error {
  color: #b91c1c;
  background: #fef2f2;
}

@media (max-width: 640px) {
  .auth-page {
    padding: 12px;
  }

  .auth-panel {
    padding: 22px 18px;
  }
}
</style>