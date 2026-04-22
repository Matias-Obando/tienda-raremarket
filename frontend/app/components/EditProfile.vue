<template>
  <section class="edit-profile-page">
    <div class="edit-profile-shell">
      <header class="edit-profile-hero">
        <div class="edit-profile-copy">
          <p class="eyebrow">Cuenta</p>
          <h1>Editar perfil</h1>
          <p class="intro">
            Ajusta tu nombre, contacto y foto de perfil sin salir del estilo de Closely.
          </p>
        </div>

        <div class="photo-card">
          <div class="profile-photo-wrapper">
            <img v-if="photoPreview" :src="photoPreview" alt="Foto de perfil" class="profile-photo" />
            <div v-else class="profile-photo profile-photo--fallback">{{ userInitial }}</div>

            <button type="button" class="edit-photo-btn" @click="triggerPhotoPicker" aria-label="Cambiar foto de perfil">
              <svg class="photo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                <path d="M14.5 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8.5" />
                <path d="M16 3.5a2.12 2.12 0 1 1 3 3L12 13.5l-4 1 1-4 7-7Z" />
              </svg>
            </button>
          </div>

          <p class="edit-photo-text">Sube una imagen JPG o PNG para personalizar tu avatar.</p>

          <input ref="photoInput" class="hidden-input" type="file" accept="image/*" @change="onPhotoChange" />

          <div class="photo-actions">
            <button type="button" class="photo-action" @click="triggerPhotoPicker">Cambiar foto</button>
            <button v-if="photoPreview" type="button" class="photo-action photo-action--soft" @click="removePhoto">
              Quitar foto
            </button>
          </div>
        </div>
      </header>

      <form class="edit-profile-form" @submit.prevent="onSave">
        <div class="form-grid">
          <label class="field">
            <span>Nombre completo *</span>
            <input v-model="form.username" type="text" required placeholder="Tu nombre completo" />
          </label>

          <label class="field">
            <span>Correo electrónico *</span>
            <input v-model="form.email" type="email" required placeholder="Tu correo" />
          </label>

          <label class="field">
            <span>Ubicación</span>
            <input v-model="form.location" type="text" placeholder="Ciudad o zona" />
          </label>

          <label class="field">
            <span>Teléfono</span>
            <input v-model="form.phone" type="tel" placeholder="+34 600 000 000" />
          </label>

          <label class="field field--full">
            <span>Biografía</span>
            <textarea v-model="form.bio" maxlength="500" placeholder="Cuéntanos algo sobre ti..."></textarea>
            <small>Máximo 500 caracteres</small>
          </label>
        </div>

        <div class="account-stats">
          <div class="stat">
            <div class="stat-label">Miembro desde</div>
            <div class="stat-value">enero de 2025</div>
          </div>
          <div class="stat">
            <div class="stat-label">Valoración</div>
            <div class="stat-value">4.8 / 5.0</div>
          </div>
          <div class="stat">
            <div class="stat-label">Ventas totales</div>
            <div class="stat-value">12 productos</div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="onCancel">Cancelar</button>
          <button type="submit" class="save-btn" :disabled="isSaving">
            <span class="icon-save"></span> {{ isSaving ? 'Guardando...' : 'Guardar cambios' }}
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import type { SessionUser } from '~/composables/useSessionUser'

const { sessionUser, loadSessionUser, saveSessionUser } = useSessionUser()
const uiMessages = useUiMessages()
const config = useRuntimeConfig()
const MAX_AVATAR_SIZE_BYTES = 10 * 1024 * 1024
const photoInput = ref<HTMLInputElement | null>(null)
const isSaving = ref(false)
const photoPreview = ref('')
const selectedPhotoFile = ref<File | null>(null)
const currentAvatarUrl = ref('')
const avatarRemoved = ref(false)
const previewObjectUrl = ref('')

const form = ref({
  username: '',
  email: '',
  location: '',
  phone: '',
  bio: ''
})

const userInitial = computed(() => form.value.username?.charAt(0).toUpperCase() || 'U')

function populateForm(user: SessionUser) {
  form.value = {
    username: user.username || '',
    email: user.email || '',
    location: user.location || '',
    phone: user.phone || '',
    bio: user.bio || ''
  }
  currentAvatarUrl.value = user.avatarUrl || ''
  photoPreview.value = currentAvatarUrl.value
  selectedPhotoFile.value = null
  avatarRemoved.value = false
}

function triggerPhotoPicker() {
  photoInput.value?.click()
}

function clearObjectPreview() {
  if (previewObjectUrl.value) {
    URL.revokeObjectURL(previewObjectUrl.value)
    previewObjectUrl.value = ''
  }
}

function onPhotoChange(event: Event) {
  const input = event.target as HTMLInputElement | null
  const file = input?.files?.[0]

  if (!file) {
    return
  }

  if (!file.type.startsWith('image/')) {
    uiMessages.error('El archivo seleccionado no es una imagen valida.')
    if (photoInput.value) {
      photoInput.value.value = ''
    }
    return
  }

  if (file.size > MAX_AVATAR_SIZE_BYTES) {
    uiMessages.error('La foto supera el limite de 10MB. Elige una imagen mas ligera.')
    if (photoInput.value) {
      photoInput.value.value = ''
    }
    return
  }

  clearObjectPreview()
  selectedPhotoFile.value = file
  avatarRemoved.value = false
  previewObjectUrl.value = URL.createObjectURL(file)
  photoPreview.value = previewObjectUrl.value
}

function removePhoto() {
  clearObjectPreview()
  selectedPhotoFile.value = null
  avatarRemoved.value = true
  currentAvatarUrl.value = ''
  photoPreview.value = ''
  if (photoInput.value) {
    photoInput.value.value = ''
  }
}

function onCancel() {
  navigateTo('/perfil')
}

async function onSave() {
  const currentUser = sessionUser.value
  if (!currentUser) {
    uiMessages.error('Primero inicia sesión para editar tu perfil.')
    await navigateTo({ path: '/auth', query: { mode: 'login', redirect: '/editprofile' } })
    return
  }

  isSaving.value = true
  try {
    const formData = new FormData()
    formData.append('username', form.value.username.trim())
    formData.append('email', form.value.email.trim())

    if (form.value.location.trim()) {
      formData.append('location', form.value.location.trim())
    }

    if (form.value.phone.trim()) {
      formData.append('phone', form.value.phone.trim())
    }

    if (form.value.bio.trim()) {
      formData.append('bio', form.value.bio.trim())
    }

    if (selectedPhotoFile.value) {
      formData.append('avatar', selectedPhotoFile.value)
    }

    if (avatarRemoved.value) {
      formData.append('clearAvatar', 'true')
    }

    const updatedUser = await $fetch<SessionUser>(`${config.public.API_BASE_URL}/users/${currentUser.id}/profile`, {
      method: 'PUT',
      body: formData
    })

    clearObjectPreview()
    currentAvatarUrl.value = updatedUser.avatarUrl || ''
    photoPreview.value = updatedUser.avatarUrl || ''
    selectedPhotoFile.value = null
    avatarRemoved.value = false

    saveSessionUser({
      ...updatedUser
    })

    uiMessages.success('Perfil actualizado correctamente.')
    await navigateTo('/perfil')
  } catch (error: any) {
    uiMessages.error(error?.data?.message || error?.message || 'No se pudo actualizar el perfil.')
  } finally {
    isSaving.value = false
  }
}

onMounted(() => {
  const currentUser = loadSessionUser().value
  if (!currentUser) {
    navigateTo({ path: '/auth', query: { mode: 'login', redirect: '/editprofile' } })
    return
  }

  populateForm(currentUser)
})

onBeforeUnmount(() => {
  clearObjectPreview()
})
</script>

<style scoped>
.edit-profile-page {
  min-height: 100vh;
  padding: 24px 16px 56px;
  background:
    radial-gradient(circle at 15% 8%, rgba(15, 118, 110, 0.1), transparent 26%),
    radial-gradient(circle at 88% 2%, rgba(15, 23, 42, 0.08), transparent 24%),
    linear-gradient(180deg, #f8fafc 0%, #eef2f6 100%);
}

.edit-profile-shell {
  max-width: 1140px;
  margin: 0 auto;
  display: grid;
  gap: 18px;
}

.edit-profile-hero,
.edit-profile-form {
  width: 100%;
  background: #fff;
  border: 1px solid #e6ebf1;
  border-radius: 26px;
  box-shadow: 0 18px 46px rgba(15, 23, 42, 0.08);
}

.edit-profile-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 30px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fcfb 52%, #eefcf8 100%);
}

.edit-profile-copy h1 {
  margin: 0 0 10px;
  font-size: clamp(2rem, 3vw, 2.7rem);
  line-height: 1.04;
  letter-spacing: -0.04em;
}

.eyebrow {
  margin: 0 0 10px;
  color: #1fb981;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  font-size: 12px;
  font-weight: 700;
}

.intro {
  margin: 0;
  max-width: 56ch;
  color: #475569;
  line-height: 1.55;
}

.photo-card {
  min-width: min(100%, 280px);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  text-align: center;
}

.profile-photo-wrapper {
  position: relative;
  width: 148px;
  height: 148px;
  border-radius: 999px;
}

.profile-photo {
  width: 100%;
  height: 100%;
  border-radius: inherit;
  object-fit: cover;
  display: block;
  border: 1px solid #99f6e4;
  box-shadow: 0 14px 28px rgba(20, 184, 166, 0.24);
}

.profile-photo--fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #dcfce7 0%, #ccfbf1 100%);
  color: #1fb981;
  font-size: 56px;
  font-weight: 800;
}

.edit-photo-btn {
  position: absolute;
  right: 6px;
  bottom: 6px;
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 999px;
  background: #1fb981;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(15, 118, 110, 0.28);
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.edit-photo-btn:hover {
  transform: translateY(-1px);
  background: #0e6c65;
  box-shadow: 0 16px 28px rgba(15, 118, 110, 0.3);
}

.photo-icon {
  width: 22px;
  height: 22px;
}

.edit-photo-text {
  margin: 0;
  color: #64748b;
  font-size: 0.92rem;
  line-height: 1.45;
}

.hidden-input {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

.photo-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
}

.photo-action {
  border: 1px solid #99f6e4;
  background: #fff;
  color: #1fb981;
  border-radius: 999px;
  min-height: 40px;
  padding: 0 16px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.photo-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 18px rgba(15, 118, 110, 0.14);
}

.photo-action--soft {
  border-color: #fecdd3;
  color: #be123c;
  background: #fff5f7;
}

.edit-profile-form {
  padding: 28px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field span {
  font-size: 0.9rem;
  font-weight: 700;
  color: #0f172a;
}

.field input,
.field textarea {
  width: 100%;
  border: 1px solid #d7dee7;
  border-radius: 16px;
  padding: 13px 14px;
  font-size: 0.98rem;
  color: #0f172a;
  background: #fff;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.field input:focus,
.field textarea:focus {
  outline: none;
  border-color: #2ec4a6;
  box-shadow: 0 0 0 4px rgba(46, 196, 166, 0.14);
}

.field textarea {
  min-height: 124px;
  resize: vertical;
}

.field small {
  color: #64748b;
}

.field--full {
  grid-column: 1 / -1;
}

.account-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
  margin: 24px 0 16px;
}

.stat {
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  padding: 16px 18px;
  text-align: center;
}

.stat-label {
  color: #64748b;
  font-size: 13px;
}

.stat-value {
  margin-top: 6px;
  font-size: 1.05rem;
  font-weight: 800;
  color: #0f172a;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

.cancel-btn,
.save-btn {
  min-height: 44px;
  padding: 0 18px;
  border-radius: 999px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.cancel-btn {
  background: #fff;
  border: 1px solid #cbd5e1;
  color: #334155;
}

.cancel-btn:hover,
.save-btn:hover:not(:disabled) {
  transform: translateY(-1px);
}

.save-btn {
  background: #1fb981;
  border: 1px solid transparent;
  color: #fff;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  box-shadow: 0 10px 24px rgba(15, 118, 110, 0.22);
}

.save-btn:disabled {
  opacity: 0.72;
  cursor: progress;
  transform: none;
}

.icon-save::before {
  content: '\1F4BE';
  font-size: 18px;
}

@media (max-width: 960px) {
  .edit-profile-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .photo-card {
    width: 100%;
    align-items: flex-start;
    text-align: left;
  }

  .photo-actions {
    justify-content: flex-start;
  }

  .account-stats {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .edit-profile-page {
    padding-left: 10px;
    padding-right: 10px;
    padding-top: 16px;
    padding-bottom: 36px;
  }

  .edit-profile-hero,
  .edit-profile-form {
    border-radius: 18px;
  }

  .edit-profile-hero,
  .edit-profile-form {
    padding-left: 18px;
    padding-right: 18px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .profile-photo-wrapper {
    width: 132px;
    height: 132px;
  }
}
</style>
