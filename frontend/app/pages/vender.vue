<template>
  <div class="sell-page">
    <div class="sell-shell">
      <header class="sell-header">
        <p class="sell-kicker">Closely Studio</p>
        <h1 class="sell-title">Publica tu artículo</h1>
        <p class="sell-sub">Prepara un anuncio claro, añade buenas fotos y publica cuando esté listo para destacar.</p>
      </header>

      <div class="sell-layout">
        <aside class="preview-column">
          <div class="preview-card">
            <div class="preview-card__head">
              <h2>Así se verá tu anuncio</h2>
              <span class="preview-card__badge">Vista en directo</span>
            </div>
            <SellPreviewCard :item="form" :imagenes="form.imagenes" />
          </div>
        </aside>

        <main class="form-column">
          <form @submit.prevent="onSubmit" class="sell-form">
            <section class="form-block">
              <h3>1. Lo esencial</h3>
              <p class="block-note">Define lo básico para que los compradores encuentren tu prenda rápido.</p>
              <div class="grid two">
                <label class="field">
                  <span class="label">Título*</span>
                  <input type="text" v-model="form.titulo" placeholder="Ej: Chaqueta vaquera oversize" />
                </label>
                <label class="field">
                  <span class="label">Precio final (€)*</span>
                  <input type="number" v-model.number="form.precioEur" min="0" placeholder="0" />
                </label>
              </div>

              <div class="grid two">
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
                  <input type="text" v-model="form.marca" placeholder="Ej: Zara, Nike, Mango" />
                </label>
              </div>

              <div class="grid two">
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
            </section>

            <section class="form-block">
              <h3>2. Cuéntanos más</h3>
              <p class="block-note">Cuanto más preciso seas, más confianza generará tu anuncio.</p>
              <label class="field">
                <span class="label">Descripción*</span>
                <textarea v-model="form.descripcion" rows="4" placeholder="Cuenta el estado real, medidas, detalles y cualquier defecto para vender antes."></textarea>
              </label>
            </section>

            <section class="form-block">
              <h3>3. Añade tus fotos</h3>
              <p class="block-note">Muestra frontal, trasera y detalle para mejorar la conversión.</p>
              <label class="upload-drop">
                <input class="file-input" type="file" accept="image/*" multiple @change="onImagesChange" />
                <span class="upload-title">Arrastra tus fotos o haz clic para seleccionarlas</span>
                <span class="upload-sub">Sube hasta {{ maxImages }} imágenes en JPG, PNG o WEBP. La primera será la portada.</span>
              </label>

              <div class="preview-thumbs" v-if="form.imagenes.length">
                <img v-for="(img, i) in form.imagenes" :key="i" :src="img" class="thumb" />
              </div>
              <p v-if="form.imagenes.length" class="upload-count">{{ form.imagenes.length }} / {{ maxImages }} fotos cargadas</p>
            </section>

            <div class="form-actions">
              <p class="action-note">Podrás editar tu anuncio después de publicarlo.</p>
              <button type="button" class="btn btn-ghost" @click="resetForm">Empezar de cero</button>
              <button type="submit" class="btn btn-primary" :disabled="!isFormReady">Publicar en Closely</button>
            </div>

            <section v-if="submitError.message" class="submit-error-panel" aria-live="polite">
              <p class="submit-error-panel__title">No se pudo publicar el articulo</p>
              <p class="submit-error-panel__message">{{ submitError.message }}</p>
              <ul v-if="submitError.details.length" class="submit-error-panel__list">
                <li v-for="detail in submitError.details" :key="detail">{{ detail }}</li>
              </ul>
            </section>
          </form>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import SellPreviewCard from '~/components/SellPreviewCard.vue'

const maxImages = 3
const uiMessages = useUiMessages()
const router = useRouter()
const itemsStore = useItemsStore()
const { loadSessionUser } = useSessionUser()
const submitError = reactive({
  message: '',
  details: [] as string[]
})

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
const selectedFiles = reactive<File[]>([])

function onImagesChange(e: Event) {
  const files = (e.target as HTMLInputElement)?.files
  if (!files) return
  const max = maxImages

  if (files.length > max) {
    uiMessages.info(`Solo se guardaran las primeras ${max} imagenes.`)
  }

  form.imagenes = []
  selectedFiles.splice(0, selectedFiles.length)
  const fileArr = Array.from(files).slice(0, max)
  fileArr.forEach(file => {
    selectedFiles.push(file)
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
  selectedFiles.splice(0, selectedFiles.length)
  submitError.message = ''
  submitError.details = []
}

function validateForm(): string | null {
  if (!form.titulo.trim()) return 'Añade un titulo para el articulo.'
  if (!form.categoria) return 'Selecciona una categoria.'
  if (!form.descripcion.trim()) return 'Añade una descripcion del articulo.'
  if (!form.precioEur || form.precioEur <= 0) return 'Indica un precio mayor que 0.'
  if (selectedFiles.length === 0) return 'Sube al menos una imagen para publicar.'
  return null
}

const isFormReady = computed(() => validateForm() === null)

async function onSubmit() {
  submitError.message = ''
  submitError.details = []

  const validationError = validateForm()

  if (validationError) {
    uiMessages.error(validationError)
    return
  }

  const currentUser = loadSessionUser().value
  if (!currentUser?.token) {
    uiMessages.info('Inicia sesion para publicar tu articulo.')
    await router.push('/auth?mode=login&redirect=/vender')
    return
  }

  let uploadedImageUrls: string[] = []

  try {
    uploadedImageUrls = await itemsStore.uploadImages([...selectedFiles])
    if (!uploadedImageUrls.length) {
      uiMessages.error('No se pudieron subir las imagenes. Intentalo de nuevo.')
      return
    }

    await itemsStore.createItem({
      titulo: form.titulo.trim(),
      descripcion: form.descripcion.trim(),
      precioEur: form.precioEur,
      categoria: form.categoria,
      marca: form.marca.trim(),
      talla: form.talla.trim(),
      estado: form.estado,
      imagen: uploadedImageUrls[0],
      images: uploadedImageUrls
    })

    uiMessages.success('Articulo publicado correctamente. Ya aparece en el marketplace.')
    resetForm()
    await router.push('/explorar')
  } catch (error: any) {
    if (uploadedImageUrls.length) {
      try {
        await itemsStore.cleanupUploadedImages(uploadedImageUrls)
      } catch (cleanupError) {
        console.error('No se pudieron limpiar imagenes subidas tras fallo de publicacion:', cleanupError)
      }
    }

    const fallbackMessage = 'No se pudo publicar el articulo. Revisa los datos e intentalo de nuevo.'
    const message = error?.data?.message || fallbackMessage
    const details = error?.data?.errors && typeof error.data.errors === 'object'
      ? Object.values(error.data.errors).map((value) => String(value))
      : []

    submitError.message = message
    submitError.details = [...new Set(details)]
    uiMessages.error(message)
  }
}
</script>
<style scoped>
.sell-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f8fafc 0%, #eef2f6 100%);
  padding: 24px 16px 42px;
}

.sell-shell {
  max-width: 1240px;
  margin: 0 auto;
}

.sell-header {
  text-align: center;
  margin: 18px auto 26px;
}

.sell-kicker {
  margin: 0;
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #0f766e;
  background: rgba(15, 118, 110, 0.12);
}

.sell-title {
  margin: 12px 0 0;
  font-size: clamp(2rem, 3.2vw, 3rem);
  line-height: 1.06;
  letter-spacing: -0.05em;
  color: #0f172a;
}

.sell-sub {
  margin: 12px auto 0;
  max-width: 62ch;
  color: #64748b;
  font-size: 1.02rem;
  line-height: 1.6;
}

.sell-layout {
  display: grid;
  grid-template-columns: minmax(320px, 430px) minmax(360px, 1fr);
  gap: 26px;
  align-items: start;
}

.preview-column {
  position: sticky;
  top: 128px;
}

.preview-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  box-shadow: 0 14px 40px rgba(15, 23, 42, 0.08);
  padding: 14px;
}

.preview-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.preview-card__head h2 {
  margin: 0;
  font-size: 1rem;
  color: #0f172a;
}

.preview-card__badge {
  font-size: 12px;
  font-weight: 700;
  color: #0f766e;
  background: rgba(15, 118, 110, 0.12);
  border-radius: 999px;
  padding: 6px 10px;
}

.form-column {
  min-width: 0;
}

.sell-form {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  box-shadow: 0 14px 40px rgba(15, 23, 42, 0.08);
  padding: 20px;
}

.form-block + .form-block {
  margin-top: 20px;
}

.form-block h3 {
  margin: 0 0 12px;
  font-size: 1.02rem;
  color: #0f172a;
}

.block-note {
  margin: -4px 0 12px;
  font-size: 0.88rem;
  color: #64748b;
}

.grid.two {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.label {
  font-size: 0.92rem;
  font-weight: 600;
  color: #0f172a;
}

input,
select,
textarea {
  width: 100%;
  border: 1px solid #d1d5db;
  border-radius: 14px;
  padding: 12px 14px;
  font-size: 1rem;
  color: #0f172a;
  background: #ffffff;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

input:hover,
select:hover,
textarea:hover {
  border-color: #9ca3af;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #0f766e;
  box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.16);
  background: #f8fffd;
}

textarea {
  resize: vertical;
  min-height: 112px;
}

.upload-drop {
  display: flex;
  flex-direction: column;
  gap: 5px;
  border: 2px dashed #cbd5e1;
  border-radius: 16px;
  padding: 18px 16px;
  background: #f8fafc;
  cursor: pointer;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.upload-drop:hover {
  border-color: #0f766e;
  background: #f2fbf9;
}

.upload-title {
  font-weight: 700;
  color: #0f172a;
}

.upload-sub {
  font-size: 0.88rem;
  color: #64748b;
}

.file-input {
  display: none;
}

.preview-thumbs {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.upload-count {
  margin: 8px 0 0;
  font-size: 0.86rem;
  color: #0f766e;
  font-weight: 600;
}

.thumb {
  width: 72px;
  height: 72px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid #cbd5e1;
  background: #e5e7eb;
}

.form-actions {
  position: sticky;
  bottom: 0;
  margin: 20px -20px -20px;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}

.action-note {
  margin: 0 auto 0 0;
  font-size: 0.86rem;
  color: #64748b;
}

.btn {
  min-height: 44px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  font-size: 0.95rem;
  font-weight: 700;
  cursor: pointer;
}

.btn-ghost {
  border-color: #cbd5e1;
  background: #ffffff;
  color: #334155;
}

.btn-primary {
  background: #0f766e;
  color: #ffffff;
  box-shadow: 0 10px 24px rgba(15, 118, 110, 0.26);
}

.btn-primary:hover {
  background: #0d655f;
}

.btn:disabled {
  opacity: 0.58;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.submit-error-panel {
  margin-top: 14px;
  border: 1px solid #fecdd3;
  background: linear-gradient(180deg, #fff1f2 0%, #fff7f8 100%);
  border-radius: 14px;
  padding: 12px 14px;
}

.submit-error-panel__title {
  margin: 0;
  color: #be123c;
  font-size: 0.83rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.submit-error-panel__message {
  margin: 6px 0 0;
  color: #881337;
  font-weight: 600;
}

.submit-error-panel__list {
  margin: 8px 0 0;
  padding-left: 18px;
  color: #9f1239;
}

@media (max-width: 980px) {
  .sell-layout {
    grid-template-columns: 1fr;
  }

  .preview-column {
    position: static;
  }

  .preview-card {
    max-width: 460px;
    margin: 0 auto;
  }
}

@media (max-width: 760px) {
  .grid.two {
    grid-template-columns: 1fr;
    margin-bottom: 12px;
  }

  .sell-form {
    padding: 16px;
  }

  .form-actions {
    margin: 18px -16px -16px;
    padding: 12px 16px;
    justify-content: stretch;
    flex-wrap: wrap;
  }

  .action-note {
    width: 100%;
    margin: 0;
  }

  .btn {
    flex: 1;
  }
}

</style>