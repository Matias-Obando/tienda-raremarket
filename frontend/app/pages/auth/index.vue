<template>
  <div class="split-auth-bg">
    <div class="split-auth-left">
      <div class="split-visual-content">
        <h2>Bienvenido a Closely</h2>
        <p>Accede o crea tu cuenta para disfrutar de todas las funciones.</p>
      </div>
    </div>
    <div class="split-auth-right">
      <div class="split-auth-switch">
        <button :class="{ active: mode === 'login' }" @click="mode = 'login'">Inicia sesión</button>
        <button :class="{ active: mode === 'register' }" @click="mode = 'register'">Regístrate</button>
      </div>
      <transition name="fade" mode="out-in">
        <form v-if="mode === 'register'" key="register" class="split-form" @submit.prevent="submitRegister">
          <label>
            <span>Nombre completo</span>
            <input v-model="registerForm.name" type="text" required placeholder="Tu nombre completo" />
          </label>
          <label>
            <span>Email</span>
            <input v-model="registerForm.email" type="email" required placeholder="Tu email" />
          </label>
          <label>
            <span>Contraseña</span>
            <input v-model="registerForm.password" type="password" required placeholder="Contraseña" />
          </label>
          <div class="split-form-actions">
            <button type="submit" class="split-btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? 'Registrando...' : 'Registrarse' }}
            </button>
          </div>
        </form>
        <form v-else key="login" class="split-form" @submit.prevent="submitLogin">
          <label>
            <span>Email</span>
            <input v-model="loginForm.email" type="email" required placeholder="Tu email" />
          </label>
          <label>
            <span>Contraseña</span>
            <input v-model="loginForm.password" type="password" required placeholder="Contraseña" />
          </label>
          <div class="split-form-actions">
            <button type="submit" class="split-btn-primary" :disabled="isSubmitting">
              {{ isSubmitting ? 'Entrando...' : 'Iniciar sesión' }}
            </button>
          </div>
        </form>
      </transition>
      <p v-if="errorMessage" class="split-error">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const config = useRuntimeConfig()
const { saveSessionUser } = useSessionUser()
const uiMessages = useUiMessages()
const mode = ref<'login' | 'register'>(route.query.mode === 'login' ? 'login' : 'register')
const isSubmitting = ref(false)
const errorMessage = ref('')

watch(() => route.query.mode, (val) => {
  if (val === 'login' || val === 'register') {
    mode.value = val
  }
})

const registerForm = ref({
  name: '',
  email: '',
  password: ''
})

const loginForm = ref({
  email: '',
  password: ''
})

function getRedirectPath() {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/perfil'
}

async function submitRegister() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const user = await $fetch<{ id: string; username: string; email: string }>(`${config.public.API_BASE_URL}/users/register`, {
      method: 'POST',
      body: {
        username: registerForm.value.name.trim(),
        email: registerForm.value.email.trim(),
        password: registerForm.value.password
      }
    })

    saveSessionUser(user)
    uiMessages.success('Registro completado. Bienvenido a Closely.')
    await router.push(getRedirectPath())
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.data || 'No se pudo completar el registro.'
    uiMessages.error(errorMessage.value)
  } finally {
    isSubmitting.value = false
  }
}

async function submitLogin() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const user = await $fetch<{ id: string; username: string; email: string }>(`${config.public.API_BASE_URL}/users/login`, {
      method: 'POST',
      body: {
        email: loginForm.value.email.trim(),
        password: loginForm.value.password
      }
    })

    saveSessionUser(user)
    uiMessages.success('Sesion iniciada correctamente.')
    await router.push(getRedirectPath())
  } catch (error: any) {
    errorMessage.value = error?.data?.message || error?.data || 'Email o contraseña incorrectos.'
    uiMessages.error(errorMessage.value)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.split-auth-bg {
  min-height: 100vh;
  width: 100vw;
  display: flex;
  flex-direction: row;
}

.split-auth-left {
  flex: 1 1 0;
  background: linear-gradient(120deg, #2fbf9b 0%, #3ed6b7 100%);
  position: relative;
  overflow: hidden;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 0 5vw;
}

.split-auth-left::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  opacity: 0.18;
  background-image: url('data:image/svg+xml;utf8,<svg width="80" height="80" viewBox="0 0 80 80" fill="none" xmlns="http://www.w3.org/2000/svg"><rect width="80" height="80" fill="none"/><rect x="10" y="10" width="28" height="32" rx="4" stroke="white" stroke-width="2"/><path d="M24 10v8" stroke="white" stroke-width="2" stroke-linecap="round"/><path d="M18 18v-2a6 6 0 0 1 12 0v2" stroke="white" stroke-width="2" stroke-linecap="round"/><rect x="42" y="38" width="24" height="28" rx="4" stroke="white" stroke-width="2"/><path d="M54 38v6" stroke="white" stroke-width="2" stroke-linecap="round"/><path d="M48 44v-2a6 6 0 0 1 12 0v2" stroke="white" stroke-width="2" stroke-linecap="round"/></svg>');
  background-size: 120px 120px;
  background-repeat: repeat;
}

.split-visual-content {
  position: relative;
  z-index: 1;
  opacity: 0.98;
  text-align: center;
}

.split-visual-content h2 {
  font-size: 1.7rem;
  font-weight: 700;
  margin: 24px 0 8px 0;
}

.split-visual-content p {
  font-size: 1rem;
  opacity: 0.92;
}

.split-auth-right {
  flex: 1 1 0;
  background: #16202a;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
  padding: 0 4vw;
}

.split-auth-switch {
  position: absolute;
  top: 72px;
  right: 48px;
  display: flex;
  gap: 0;
  background: #223042;
  border-radius: 999px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.split-auth-switch button {
  background: none;
  border: none;
  color: #bfead9;
  font-size: 1rem;
  font-weight: 600;
  padding: 10px 32px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.split-auth-switch button.active {
  background: #2fbf9b;
  color: #fff;
}

.split-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
  max-width: 320px;
  margin: 48px auto 0;
  color: #fff;
}

.split-form label {
  display: flex;
  flex-direction: column;
  gap: 7px;
  font-size: 1rem;
  color: #fff;
}

.split-form input {
  border: 1.5px solid #bfead9;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 0.97rem;
  background: #223042;
  color: #fff;
  outline: none;
  transition: border 0.2s, background 0.2s;
}

.split-form input:focus {
  border-color: #2fbf9b;
  background: #1e2a3a;
}

.split-form-actions {
  margin-top: 18px;
  display: flex;
  justify-content: center;
}

.split-btn-primary {
  background: linear-gradient(120deg, #2fbf9b 0%, #3ed6b7 100%);
  color: #fff;
  border: none;
  border-radius: 999px;
  padding: 10px 28px;
  font-size: 1rem;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(47, 191, 155, 0.08);
  cursor: pointer;
  transition: background 0.2s;
}

.split-btn-primary:hover {
  background: linear-gradient(120deg, #27a685 0%, #2fbf9b 100%);
}

.split-btn-primary:disabled {
  opacity: 0.7;
  cursor: wait;
}

.split-error {
  margin-top: 18px;
  max-width: 320px;
  color: #fda4af;
  text-align: center;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
