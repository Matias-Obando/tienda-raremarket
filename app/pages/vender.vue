<template>
  <div class="rm-container rm-page">
    <header class="top">
      <h1 class="title">Vender</h1>
      <p class="sub">Publicación (mock). Luego lo conectamos con base de datos.</p>
    </header>

    <form class="form" @submit.prevent="onSubmit">
      <div class="grid">
        <label class="field">
          <span class="label">Título *</span>
          <input v-model.trim="f.titulo" class="input" type="text" placeholder="Ej: Sudadera Nike vintage" required />
        </label>

        <label class="field">
          <span class="label">Precio (€) *</span>
          <input
            v-model.number="f.precioEur"
            class="input"
            type="number"
            inputmode="decimal"
            min="1"
            step="0.5"
            placeholder="Ej: 25"
            required
          />
        </label>

        <label class="field">
          <span class="label">Categoría *</span>
          <select v-model="f.categoria" class="input" required>
            <option value="" disabled>Selecciona una categoría</option>
            <option value="Camisetas">Camisetas</option>
            <option value="Pantalones">Pantalones</option>
            <option value="Zapatillas">Zapatillas</option>
            <option value="Accesorios">Accesorios</option>
          </select>
        </label>

        <label class="field">
          <span class="label">Marca *</span>
          <input v-model.trim="f.marca" class="input" type="text" placeholder="Ej: Nike" required />
        </label>

        <label class="field">
          <span class="label">Talla *</span>
          <input v-model.trim="f.talla" class="input" type="text" placeholder="Ej: M / 42 / Única" required />
        </label>

        <label class="field">
          <span class="label">Estado *</span>
          <select v-model="f.estado" class="input" required>
            <option value="" disabled>Selecciona el estado</option>
            <option value="Como nuevo">Como nuevo</option>
            <option value="Usado">Usado</option>
          </select>
        </label>

        <label class="field field--full">
          <span class="label">Descripción *</span>
          <textarea
            v-model.trim="f.descripcion"
            class="input textarea"
            rows="5"
            placeholder="Describe el producto, defectos, medidas, etc."
            required
          />
        </label>

        <label class="field field--full">
          <span class="label">Imagen (URL) *</span>
          <input v-model.trim="f.imagen" class="input" type="url" placeholder="https://..." required />
          <span class="help">Por ahora pegamos una URL. Luego lo cambiamos por subida real.</span>
        </label>

        <div class="actions field--full">
          <button class="rm-btn rm-btn--primary btn" type="submit" :disabled="!canSubmit">
            Publicar (mock)
          </button>
          <button class="rm-btn rm-btn--ghost btn" type="button" @click="reset">
            Limpiar
          </button>
        </div>
      </div>
    </form>

    <section v-if="f.imagen" class="preview">
      <h2 class="previewTitle">Vista previa</h2>
      <div class="previewCard">
        <div class="previewMedia">
          <img :src="f.imagen" alt="Preview" />
        </div>
        <div class="previewInfo">
          <div class="previewPrice">{{ priceText }}</div>
          <div class="previewName">{{ f.titulo || '—' }}</div>
          <div class="previewMeta">{{ f.marca || '—' }} · {{ f.talla || '—' }} · {{ f.estado || '—' }}</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
const f = reactive({
  titulo: '',
  precioEur: 0,
  categoria: '' as '' | 'Camisetas' | 'Pantalones' | 'Zapatillas' | 'Accesorios',
  marca: '',
  talla: '',
  estado: '' as '' | 'Como nuevo' | 'Usado',
  descripcion: '',
  imagen: '',
})

const canSubmit = computed(() => {
  return (
    f.titulo.length >= 3 &&
    Number.isFinite(f.precioEur) &&
    f.precioEur > 0 &&
    !!f.categoria &&
    f.marca.length >= 2 &&
    f.talla.length >= 1 &&
    !!f.estado &&
    f.descripcion.length >= 10 &&
    /^https?:\/\//i.test(f.imagen)
  )
})

const priceText = computed(() => {
  const n = Number(f.precioEur || 0)
  return `${n.toFixed(2)} €`
})

function reset() {
  f.titulo = ''
  f.precioEur = 0
  f.categoria = ''
  f.marca = ''
  f.talla = ''
  f.estado = ''
  f.descripcion = ''
  f.imagen = ''
}

function onSubmit() {
  if (!canSubmit.value) {
    alert('Completa los campos obligatorios correctamente.')
    return
  }

  alert(
    `Publicado (mock):\n\n` +
      `Título: ${f.titulo}\n` +
      `Precio: ${priceText.value}\n` +
      `Categoría: ${f.categoria}\n` +
      `Marca: ${f.marca}\n` +
      `Talla: ${f.talla}\n` +
      `Estado: ${f.estado}\n`
  )

  reset()
}
</script>

<style scoped>
.top {
  margin-bottom: 14px;
}

.title {
  margin: 6px 0 8px;
  letter-spacing: -0.03em;
}

.sub {
  margin: 0;
  color: var(--rm-muted);
  font-size: 14px;
}

.form {
  margin-top: 10px;
}

.grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.field {
  display: grid;
  gap: 6px;
}

.field--full {
  grid-column: 1 / -1;
}

.label {
  font-size: 13px;
  color: var(--rm-muted);
}

.input {
  width: 100%;
  border: 1px solid var(--rm-border);
  border-radius: 12px;
  padding: 10px 12px;
  background: #fff;
  color: var(--rm-text);
  outline: none;
}

.input:focus {
  border-color: rgba(0, 0, 0, 0.35);
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.06);
}

.textarea {
  resize: vertical;
}

.help {
  font-size: 12px;
  color: var(--rm-muted);
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 6px;
}

.btn {
  min-height: 44px;
}

.preview {
  margin-top: 22px;
  padding-top: 16px;
  border-top: 1px solid var(--rm-border);
}

.previewTitle {
  margin: 0 0 10px;
  font-size: 16px;
}

.previewCard {
  border: 1px solid var(--rm-border);
  border-radius: var(--rm-radius);
  overflow: hidden;
  background: #fff;
}

.previewMedia img {
  width: 100%;
  display: block;
  aspect-ratio: 4 / 3;
  object-fit: cover;
}

.previewInfo {
  padding: 12px;
}

.previewPrice {
  font-weight: 900;
  margin-bottom: 6px;
}

.previewName {
  font-weight: 800;
}

.previewMeta {
  margin-top: 4px;
  color: var(--rm-muted);
  font-size: 12px;
}

@media (min-width: 820px) {
  .grid {
    grid-template-columns: 1fr 1fr;
    gap: 14px;
  }

  .actions {
    justify-content: flex-start;
  }
}
</style>