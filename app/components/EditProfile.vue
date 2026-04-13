<template>
  <div class="edit-profile-container">
    <div class="profile-photo-section">
      <div class="profile-photo-wrapper">
        <img :src="profilePhotoUrl" alt="Foto de perfil" class="profile-photo" />
        <button class="edit-photo-btn" @click="onEditPhoto" aria-label="Cambiar foto de perfil">
          <span class="heroicon-wrapper">
            <PhotoIcon class="w-7 h-7 text-white" />
          </span>
        </button>
      </div>
    </div>
    <div class="photo-text-container">
      <p class="edit-photo-text">Haz clic en el icono para cambiar tu foto de perfil</p>
    </div>
    <form class="profile-form" @submit.prevent="onSave">
      <div class="form-row">
        <div class="form-group">
          <label>Nombre completo *</label>
          <input v-model="form.nombre" type="text" required />
        </div>
        <div class="form-group">
          <label>Ubicación *</label>
          <input v-model="form.ubicacion" type="text" required />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label>Correo electrónico *</label>
          <input v-model="form.email" type="email" required />
        </div>
        <div class="form-group">
          <label>Teléfono</label>
          <input v-model="form.telefono" type="tel" />
        </div>
      </div>
      <div class="form-row">
        <div class="form-group full-width">
          <label>Biografía</label>
          <textarea v-model="form.bio" maxlength="500" placeholder="Cuéntanos algo sobre ti..."></textarea>
          <small>Máximo 500 caracteres</small>
        </div>
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
        <button type="submit" class="save-btn">
          <span class="icon-save"></span> Guardar cambios
        </button>
      </div>
    </form>
  </div>
</template>


<script setup lang="ts">
import { ref } from 'vue'
import { PhotoIcon } from '@heroicons/vue/24/solid'

const profilePhotoUrl = ref('https://images.dog.ceo/breeds/husky/n02110185_1469.jpg')

const form = ref({
  nombre: 'Matias Obando',
  ubicacion: 'Barcelona,España',
  email: 'matias@gmail.com',
  telefono: '+34 600 000 000',
  bio: '',
})

function onEditPhoto() {
  alert('Funcionalidad de cambiar foto próximamente')
}

function onCancel() {
  window.history.back()
}

function onSave() {
  alert('Cambios guardados (simulado)')
}
</script>

<style scoped>
.edit-profile-container {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  max-width: 900px;
  margin: 80px auto 40px auto;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.profile-photo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  text-align: center;
  margin-bottom: 0;
  margin-top: 32px;
}
.photo-text-container {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  margin-bottom: 24px;
}
.profile-photo-wrapper {
  position: relative;
  display: inline-block;
}
.profile-photo {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e0e0e0;
}
.edit-photo-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: #2ec4a6;
  border: none;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,0.10);
  transition: background 0.2s;
}
.edit-photo-btn:hover {
  background: #26a68a;
}
.heroicon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}
.edit-photo-text {
  color: #888;
  font-size: 15px;
  margin: 0;
  max-width: 340px;
  text-align: center;
  line-height: 1.4;
}
.profile-form {
  margin-top: 16px;
}
.form-row {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}
.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.form-group.full-width {
  flex: 2;
}
input, textarea {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 10px 14px;
  font-size: 16px;
  margin-top: 6px;
  margin-bottom: 2px;
}
textarea {
  min-height: 70px;
  resize: vertical;
}
.account-stats {
  display: flex;
  gap: 24px;
  margin: 24px 0 16px 0;
}
.stat {
  background: #f6f6f6;
  border-radius: 10px;
  padding: 16px 20px;
  flex: 1;
  text-align: center;
}
.stat-label {
  color: #888;
  font-size: 13px;
}
.stat-value {
  font-weight: bold;
  font-size: 17px;
  margin-top: 4px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 18px;
}
.cancel-btn {
  background: #fff;
  border: 1.5px solid #2ec4a6;
  color: #2ec4a6;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}
.cancel-btn:hover {
  background: #f6f6f6;
}
.save-btn {
  background: #2ec4a6;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background 0.2s;
}
.save-btn:hover {
  background: #26a68a;
}
.icon-camera::before {
  display: none;
}
.icon-save::before {
  content: '\1F4BE';
  font-size: 20px;
}
</style>
