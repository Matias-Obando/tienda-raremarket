<template>
  <div class="vender-bg">
    <div class="vender-center">
      <header class="vender-header">
        <h1 class="page-title">Subir artículo</h1>
        <p class="page-sub">Rellena los datos y revisa la vista previa en tiempo real</p>
      </header>
      <div class="vender-flex">
        <aside class="preview-area">
          <SellPreviewCard :item="form" :imagenes="form.imagenes" />
        </aside>
        <main class="form-area">
          <form @submit.prevent="onSubmit" class="vender-form">
            <div class="row two">
              <label class="field">
                <span class="label">Título*</span>
                <input type="text" v-model="form.titulo" placeholder="Título del producto" />
              </label>
              <label class="field">
                <span class="label">Precio (€)*</span>
                <input type="number" v-model.number="form.precioEur" min="0" placeholder="" />
              </label>
            </div>
            <div class="row two">
              <label class="field">
                <span class="label">Categoría*</span>
                <select v-model="form.categoria" required>
                  <option value="" disabled selected>Selecciona una categoría</option>
                  <option value="Camisetas">Camisetas</option>
                  <option value="Abrigos">Abrigos</option>
                  <option value="Chaquetas">Chaquetas</option>
                  <option value="Jerséis & Sudaderas">Jerséis & Sudaderas</option>
                  <option value="Vestidos">Vestidos</option>
                  <option value="Camisas & Camisetas">Camisas & Camisetas</option>
                  <option value="Pantalones">Pantalones</option>
                  <option value="Vaqueros">Vaqueros</option>
                  <option value="Calzado">Calzado</option>
                  <option value="Bolsos">Bolsos</option>
                  <option value="Otros">Otros</option>
                </select>
              </label>
              <label class="field">
                <span class="label">Marca</span>
                <input type="text" v-model="form.marca" placeholder="Ej: Nike" />
              </label>
            </div>
            <div class="row two">
              <label class="field">
                <span class="label">Talla</span>
                <input type="text" v-model="form.talla" placeholder="Ej: M / 42 / Única" />
              </label>
              <label class="field">
                <span class="label">Estado*</span>
                <select v-model="form.estado">
                  <option value="Usado">Usado</option>
                  <option value="Como nuevo">Como nuevo</option>
                  <option value="Nuevo">Nuevo</option>
                </select>
              </label>
            </div>
            <label class="field">
              <span class="label">Descripción*</span>
              <textarea v-model="form.descripcion" rows="4" placeholder="Describe el producto, defectos, medidas, etc."></textarea>
            </label>
            <label class="field">
              <span class="label">Imagen*</span>
              <input type="file" accept="image/*" multiple @change="onImagesChange" />
              <small class="hint">Puedes subir hasta 3 imágenes</small>
              <div class="preview-thumbs" v-if="form.imagenes.length">
                <img v-for="(img, i) in form.imagenes" :key="i" :src="img" class="thumb" />
              </div>
            </label>
            <div class="form-actions">
              <button type="button" class="btn btn-ghost" @click="resetForm">Limpiar</button>
              <button type="submit" class="btn btn-primary">Subir</button>
            </div>
          </form>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import SellPreviewCard from '~/components/SellPreviewCard.vue'

const form = reactive({
  titulo: '',
  precioEur: 0,
  categoria: '',
  marca: '',
  talla: '',
  estado: 'Usado',
  descripcion: '',
  imagenes: [] as string[]
})

function onImagesChange(e: Event) {
  const files = (e.target as HTMLInputElement)?.files
  if (!files) return
  const max = 3
  form.imagenes = []
  const fileArr = Array.from(files).slice(0, max)
  fileArr.forEach(file => {
    const reader = new FileReader()
    reader.onload = (ev) => {
      if (typeof ev.target?.result === 'string') {
        form.imagenes.push(ev.target.result)
      }
    }
    reader.readAsDataURL(file)
  })
}

function resetForm() {
  form.titulo = ''
  form.precioEur = 0
  form.categoria = ''
  form.marca = ''
  form.talla = ''
  form.estado = 'Usado'
  form.descripcion = ''
  form.imagenes = []
}

function onSubmit() {
  alert('Publicar (mock): datos listos en consola')
  resetForm()
}
</script>
<style scoped>
:root {
  --preview-w: 340px;
  --form-w: 420px;
  --page-bg: #d6d6d6;
}

.vender-bg {
  min-height: 100vh;
  background: var(--page-bg);
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: center;
  opacity: 1 !important;
  color: #000 !important;
  filter: none !important;
  position: relative;
  z-index: 10;
}

.vender-center {
  max-width: 980px;
  margin: 0 auto;
  padding: 40px 0 0 0;
  position: relative;
  z-index: 11;
  opacity: 1 !important;
  color: #000 !important;
  filter: none !important;
}

.vender-header {
  text-align: center;
  margin-bottom: 32px;
  margin-top: 32px;
  opacity: 1 !important;
}
.page-title {
  font-size: 32px;
  font-weight: 900;
  color: #000 !important;
  margin-bottom: 8px;
  background: transparent !important;
  box-shadow: none !important;
  opacity: 1 !important;
  text-shadow: none !important;
}
.page-sub {
  margin-top: 8px;
  color: #888 !important;
  font-size: 17px;
  background: transparent !important;
  box-shadow: none !important;
  opacity: 1 !important;
  font-weight: 500;
  text-shadow: none !important;
}

.vender-flex {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: center;
  gap: 64px;
}

.preview-area {
  min-width: var(--preview-w);
  max-width: var(--preview-w);
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-top: 8px;
  gap: 12px;
  box-sizing: border-box;
}
.preview-area .sell-card {
  width: 100%;
  max-width: var(--preview-w);
  box-sizing: border-box;
}
.preview-area .sell-card__media {
  height: 320px;
  max-height: 420px;
  overflow: hidden;
}
.help-note {
  font-size: 13px;
  color: #6b7280;
  width: 100%;
  text-align: center;
}

.form-area {
  width: 100%;
  max-width: var(--form-w);
  box-sizing: border-box;
  position: relative;
  z-index: 12;
  opacity: 1 !important;
  color: #181818 !important;
  filter: none !important;
}
.vender-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 6px 0;
  opacity: 1 !important;
  color: #181818 !important;
  filter: none !important;
}
.row.two {
  display: flex;
  gap: 18px;
}
.row.two .field { flex: 1; }
@media (max-width: 880px) {
  .vender-flex { flex-direction: column; align-items: center; }
  .row.two { flex-direction: column; gap: 8px; }
}
.field { display: flex; flex-direction: column; gap: 8px; }
        .label {
  font-size: 17px;
  color: #181818 !important;
  font-weight: 700;
  letter-spacing: 0.01em;
  margin-bottom: 2px;
  opacity: 1 !important;
        }
input, select, textarea {
  background: #fff !important;
  border: 1.5px solid #e6e6e8 !important;
  border-radius: 12px !important;
  padding: 14px 16px !important;
  font-size: 16px !important;
  color: #181818 !important;
  outline: none !important;
  box-shadow: none !important;
  transition: box-shadow .12s !important;
  opacity: 1 !important;
}
input:focus, select:focus, textarea:focus {
  box-shadow: 0 8px 20px rgba(16,24,40,0.06);
}
textarea { resize: none; }
.hint { font-size: 13px; color: #a0a0a0; margin-top: 6px; }
.vender-form .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  margin-top: 32px;
  margin-bottom: 48px;
}
.btn {
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  padding: 7px 22px;
  border: none;
  cursor: pointer;
  opacity: 1 !important;
  color: #181818 !important;
}
.btn-primary {
  background: #22b89a !important;
  color: #fff !important;
  opacity: 1 !important;
}
.btn-ghost {
  background: #fff !important;
  color: #181818 !important;
  border: 1.5px solid #e6e6e8 !important;
  opacity: 1 !important;
}
.preview-thumbs {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.thumb {
  width: 56px;
  height: 56px;
  object-fit: cover;
  border-radius: 8px;
  border: 1.5px solid #e6e6e8;
  background: #f3f4f6;
}
@media (min-width: 1025px) {
  .preview-area { align-items:flex-end; }
}
.vender-header, .vender-grid { transform: none !important; }


.row.two {
  display:flex;
  gap:12px;
}
.row.two .field { flex:1; }


@media (max-width: 880px) {
  .row.two { flex-direction:column; }
}


.field { display:flex; flex-direction:column; gap:8px; }
  .label { font-size: 14px; color: var(--rm-muted, #6b7280); font-weight: 600; }


input, select, textarea {
  background: #fff;
  border-radius: 12px 12px 12px 12px;
  border: 2.5px solid #e6e6e8;
  box-shadow: 0 10px 30px rgba(16,24,40,0.06);
  font-size:14px;
.sell-card__body {
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
  color:var(--rm-text,#111827);
  outline:none;
  box-shadow:none;
  transition: border-color .12s ease, box-shadow .12s ease;
}
input:focus, select:focus, textarea:focus {
  border-color: color-mix(in srgb, var(--rm-primary,#10b981) 70%, black 0%);
  box-shadow: 0 8px 20px rgba(16,24,40,0.06);
}
textarea { resize:none; }


.hint { font-size:12px; color:var(--rm-muted); margin-top:6px; }


.vender-form .form-actions {
  display:flex;
  justify-content:flex-end;
  gap:12px;
  margin-top: 8px;
}


.vender-header, .vender-grid { transform: none !important; }

/* keep everything boxed properly */
.vender-header, .vender-grid { transform: none !important; }

</style>