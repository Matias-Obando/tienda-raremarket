<template>
  <div class="auth-page">
    <div class="auth-shell">
      <div class="auth-visual">
        <div class="auth-visual__overlay">
          <p class="auth-visual__kicker">Closely · Moda circular</p>
          <h2>Vende lo que no usas. Encuentra prendas con historia.</h2>
          <p>
            Crea tu cuenta para guardar favoritos, publicar productos y hablar con compradores
            o vendedores desde tu perfil.
          </p>
          <NuxtLink to="/explorar" class="auth-visual__link">Explorar articulos</NuxtLink>
        </div>
      </div>

      <div class="auth-panel">
        <div class="auth-brand">
          <NuxtLink to="/" class="auth-brand__logo" aria-label="Ir al inicio">
            <img :src="authLogo" alt="Closely" class="h-32 w-auto" />
          </NuxtLink>
          <NuxtLink to="/explorar" class="auth-brand__link">Volver a explorar</NuxtLink>
        </div>

        <div class="auth-panel__header">
          <h1>{{ mode === 'register' ? 'Crea tu cuenta' : 'Bienvenido de nuevo' }}</h1>
          <p>
            {{ mode === 'register' ? 'Empieza a vender en minutos.' : 'Accede para continuar en Closely.' }}
          </p>
        </div>

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

        <p class="auth-legal">
          Al continuar aceptas nuestros terminos y politica de privacidad.
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import logoAsset from '~/assets/photos/closely.png'

const route = useRoute()
const router = useRouter()
const config = useRuntimeConfig()
const { saveSessionUser } = useSessionUser()
const uiMessages = useUiMessages()
const authLogo = logoAsset
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

type AuthApiResponse = {
  token: string
  user: {
    id: string
    username: string
    email: string
    avatarUrl?: string
    location?: string
    phone?: string
    bio?: string
  }
}

function getRedirectPath() {
  const redirect = route.query.redirect
  return typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/explorar'
}

async function submitRegister() {
  errorMessage.value = ''
  isSubmitting.value = true

  try {
    const response = await $fetch<AuthApiResponse>(`${config.public.API_BASE_URL}/users/register`, {
      method: 'POST',
      body: {
        username: registerForm.value.name.trim(),
        email: registerForm.value.email.trim(),
        password: registerForm.value.password
      }
    })

    saveSessionUser({
      ...response.user,
      token: response.token
    })
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
    const response = await $fetch<AuthApiResponse>(`${config.public.API_BASE_URL}/users/login`, {
      method: 'POST',
      body: {
        email: loginForm.value.email.trim(),
        password: loginForm.value.password
      }
    })

    saveSessionUser({
      ...response.user,
      token: response.token
    })
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
.auth-page {
  min-height: 100vh;
  padding: clamp(14px, 2.4vw, 28px);
  background: var(--rm-page-bg);
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif;
}

.auth-shell {
  min-height: calc(100vh - clamp(28px, 4.8vw, 56px));
  border-radius: 30px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e7ebf0;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.1);
  display: grid;
  grid-template-columns: minmax(300px, 1.1fr) minmax(360px, 1fr);
}

.auth-visual {
  position: relative;
  isolation: isolate;
  display: flex;
  align-items: flex-end;
  background:
    linear-gradient(165deg, rgba(15, 118, 110, 0.58), rgba(13, 148, 136, 0.34) 42%, rgba(15, 23, 42, 0.28)),
    url('/bg/armario.png') center center / cover no-repeat;
}

.auth-visual__overlay {
  color: #ffffff;
  padding: clamp(28px, 3.5vw, 46px);
  max-width: 560px;
}

.auth-visual__kicker {
  margin: 0 0 14px;
  display: inline-flex;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.22);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 700;
}

.auth-visual h2 {
  margin: 0;
  font-size: clamp(1.92rem, 3vw, 2.95rem);
  line-height: 1.06;
  letter-spacing: -0.05em;
  font-weight: 700;
}

.auth-visual p {
  margin: 14px 0 0;
  font-size: 1.02rem;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.92);
}

.auth-visual__link {
  margin-top: 22px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  padding: 0 18px;
  border-radius: 999px;
  font-weight: 700;
  color: #0f172a;
  background: #ffffff;
  border: 1px solid rgba(255, 255, 255, 0.85);
}

.auth-panel {
  background: linear-gradient(180deg, #ffffff 0%, #f9fbfd 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: clamp(26px, 3.6vw, 48px);
}

.auth-brand {
  width: 100%;
  max-width: 390px;
  margin-bottom: 18px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.auth-brand__logo {
  display: inline-flex;
  align-items: center;
}

.auth-brand__logo img {
  height: 36px;
  width: auto;
  border-radius: 8px;
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
  width: 100%;
  max-width: 390px;
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

.split-auth-switch {
  width: 100%;
  max-width: 390px;
  display: flex;
  gap: 6px;
  background: #f2f7f7;
  border: 1px solid #dde8e6;
  padding: 5px;
  border-radius: 999px;
}

.split-auth-switch button {
  background: transparent;
  border: none;
  color: #475569;
  font-size: 0.96rem;
  font-weight: 600;
  letter-spacing: -0.01em;
  padding: 10px 16px;
  border-radius: 999px;
  flex: 1;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease;
}

.split-auth-switch button.active {
  background: #1fb981;
  color: #fff;
  box-shadow: 0 6px 16px rgba(15, 118, 110, 0.22);
}

.split-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  width: 100%;
  max-width: 390px;
  margin: 20px auto 0;
  color: #0f172a;
}

.split-form label {
  display: flex;
  flex-direction: column;
  gap: 7px;
  font-size: 0.94rem;
  color: #0f172a;
  font-weight: 600;
  letter-spacing: -0.01em;
}

.split-form input {
  border: 1px solid #d7dde5;
  border-radius: 16px;
  padding: 12px 14px;
  font-size: 1rem;
  line-height: 1.4;
  background: #fbfcfe;
  color: #0f172a;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.split-form input::placeholder {
  color: #94a3b8;
}

.split-form input:hover {
  border-color: #9ca3af;
}

.split-form input:focus {
  border-color: #1fb981;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(15, 118, 110, 0.16);
}

.split-form-actions {
  margin-top: 10px;
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
  box-shadow: 0 10px 20px rgba(15, 118, 110, 0.2);
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

.split-error {
  margin-top: 14px;
  max-width: 390px;
  color: #b91c1c;
  width: 100%;
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 0.92rem;
}

.auth-legal {
  margin: 12px 0 0;
  max-width: 390px;
  width: 100%;
  color: #64748b;
  font-size: 0.82rem;
  line-height: 1.45;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 1280px) {
  .auth-visual h2 {
    font-size: clamp(1.8rem, 2.9vw, 2.55rem);
  }
}

@media (max-width: 980px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .auth-visual {
    min-height: 240px;
    align-items: center;
  }
}

@media (max-width: 640px) {
  .auth-page {
    padding: 0;
  }

  .auth-shell {
    min-height: 100vh;
    border-radius: 0;
    border: none;
    box-shadow: none;
  }

  .auth-visual {
    min-height: 220px;
  }

  .auth-visual h2 {
    font-size: 1.6rem;
  }

  .auth-brand {
    margin-bottom: 12px;
  }

  .auth-brand__logo img {
    height: 36px;
  }
}
</style>
