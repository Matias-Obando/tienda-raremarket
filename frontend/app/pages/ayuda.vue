<template>
  <div class="help-page">
    <section class="help-hero">
      <div class="help-hero__content">
        <h1>Centro de asistencia</h1>

        <div class="help-hero__actions">
          <NuxtLink to="/" class="help-primary-link">Ir a inicio</NuxtLink>
          <a href="mailto:soporte@closely.com" class="help-secondary-link">Escribir a soporte</a>
        </div>
      </div>
    </section>

    <section class="help-grid" aria-label="Accesos rápidos">
      <article v-for="item in shortcuts" :key="item.title" class="help-card help-card--shortcut">
        <div class="help-card__icon">{{ item.icon }}</div>
        <h2>{{ item.title }}</h2>
        <p>{{ item.description }}</p>
        <NuxtLink :to="item.to" class="help-card__link">Abrir sección</NuxtLink>
      </article>
    </section>

    <section class="help-layout">
      <article class="help-panel">
        <div class="help-panel__head">
          <span class="help-panel__eyebrow">Preguntas frecuentes</span>
          <h2>Resuelve lo básico en pocos clics</h2>
        </div>

        <div class="faq-list">
          <details v-for="faq in faqs" :key="faq.question" class="faq-item">
            <summary>{{ faq.question }}</summary>
            <p>{{ faq.answer }}</p>
          </details>
        </div>
      </article>

      <aside class="help-panel help-panel--sticky">
        <div class="help-panel__head">
          <span class="help-panel__eyebrow">Contacto directo</span>
          <h2>Habla con una persona real</h2>
        </div>

        <div class="contact-box">
          <div>
            <strong>Email de soporte</strong>
            <p>soporte@closely.com</p>
          </div>
          <div>
            <strong>Tiempo de respuesta</strong>
            <p>Normalmente en menos de 24 horas</p>
          </div>
          <div>
            <strong>Horario</strong>
            <p>Lunes a viernes, 9:00 a 18:00</p>
          </div>
        </div>

        <div class="contact-actions">
          <a href="mailto:soporte@closely.com?subject=Ayuda%20Closely" class="help-primary-link">Abrir correo</a>
          <NuxtLink to="/sitemap" class="help-secondary-link">Ver mapa del sitio</NuxtLink>
        </div>
      </aside>
    </section>

    <section class="help-footer-note">
      <p>
        <span class="notice-asterisk">*</span> Si necesitas ayuda extra o tienes una consulta específica, no dudes en ponerte en contacto con nosotros.
      </p>
    </section>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default'
})

const shortcuts = [
  {
    icon: '1',
    title: 'Inicio',
    description: 'Moverte por la portada, buscar artículos y guardar favoritos.',
    to: '/'
  },
  {
    icon: '2',
    title: 'Vender',
    description: 'Sube un producto, añade fotos y publica tu primer anuncio.',
    to: '/vender'
  },
  {
    icon: '3',
    title: 'Comprar',
    description: 'Encuentra prendas, contacta con vendedores y revisa tu compra.',
    to: '/explorar'
  },
  {
    icon: '4',
    title: 'Cuenta',
    description: 'Recupera acceso, cambia datos y gestiona tu perfil.',
    to: '/perfil'
  }
]

const faqs = [
  {
    question: 'No puedo iniciar sesión, ¿qué hago?',
    answer:
      'Prueba a recuperar tu contraseña desde la pantalla de acceso. Si no recibes el correo, revisa spam y confirma que escribiste el email correcto.'
  },
  {
    question: '¿Cómo publico un artículo?',
    answer:
      'Entra en Vender, completa el formulario con fotos claras, una descripción realista y el precio. Cuantos más detalles des, más fácil será vender.'
  },
  {
    question: '¿Puedo cambiar mi perfil o foto?',
    answer:
      'Sí. Desde tu perfil puedes editar nombre, biografía, ubicación y foto para mantener la cuenta actualizada.'
  },
  {
    question: '¿Qué pasa si tengo un problema con una compra?',
    answer:
      'Escríbenos cuanto antes a soporte@closely.com con el mayor detalle posible. Revisaremos el caso y te indicaremos los siguientes pasos.'
  },
  {
    question: '¿Cómo protejo mejor mi cuenta?',
    answer:
      'Usa una contraseña única, revisa siempre el email de recuperación y evita compartir códigos o enlaces de acceso con terceros.'
  }
]
</script>

<style scoped>
.help-page {
  min-height: 100vh;
  padding: clamp(20px, 3vw, 40px);
  background: linear-gradient(180deg, #f8fafc 0%, #eef2f6 100%);
  color: #0f172a;
}

.help-hero {
  max-width: 1180px;
  margin: 0 auto 24px;
  padding: clamp(22px, 3.2vw, 34px);
  border: 1px solid #dbe8e6;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(10px);
  box-shadow: 0 18px 50px rgba(15, 23, 42, 0.08);
}

.help-hero__badge {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 8px 14px;
  border-radius: 999px;
  background: #ecfdf8;
  color: #0f766e;
  font-size: 0.86rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.help-hero__icon {
  display: inline-grid;
  place-items: center;
  width: 26px;
  height: 26px;
  border-radius: 999px;
  background: #1fb981;
  color: #fff;
  font-size: 1rem;
}

.help-hero__content {
  margin-top: 18px;
  display: grid;
  gap: 18px;
  grid-template-columns: minmax(0, 1.4fr) auto;
  align-items: end;
}

.help-hero h1 {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.3rem);
  line-height: 1.02;
  letter-spacing: -0.05em;
}

.help-hero p {
  margin: 12px 0 0;
  max-width: 62ch;
  color: #475569;
  font-size: 1.02rem;
  line-height: 1.7;
}

.help-hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: flex-end;
}

.contact-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  margin-top: 18px;
}

.help-primary-link,
.help-secondary-link,
.help-card__link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 0 18px;
  border-radius: 999px;
  text-decoration: none;
  font-weight: 700;
  letter-spacing: -0.01em;
  transition: transform 0.2s ease, background-color 0.2s ease, border-color 0.2s ease;
}

.help-primary-link {
  background: #1fb981;
  color: #fff;
  box-shadow: 0 10px 20px rgba(15, 118, 110, 0.16);
}

.help-primary-link:hover {
  background: #0f766e;
  transform: translateY(-1px);
}

.help-secondary-link {
  background: #fff;
  color: #0f766e;
  border: 1px solid #cde7df;
}

.help-secondary-link:hover {
  border-color: #8ed9c4;
  transform: translateY(-1px);
}

.help-grid {
  max-width: 1180px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.help-card {
  border: 1px solid #dce7e5;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
}

.help-card--shortcut {
  padding: 20px;
}

.help-card__icon {
  display: none;
}

.help-card h2 {
  margin: 16px 0 8px;
  font-size: 1.12rem;
}

.help-card p {
  margin: 0;
  color: #64748b;
  line-height: 1.6;
  font-size: 0.96rem;
}

.help-card__link {
  margin-top: 16px;
  background: #f0fdf9;
  color: #0f766e;
  border: 1px solid #c5efe4;
}

.help-card__link:hover {
  background: #d9fbf2;
}

.help-layout {
  max-width: 1180px;
  margin: 20px auto 0;
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.9fr);
  gap: 16px;
  align-items: start;
}

.help-panel {
  border: 1px solid #dce7e5;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
  padding: clamp(20px, 3vw, 28px);
}

.help-panel--sticky {
  position: sticky;
  top: 18px;
}

.help-panel__head {
  margin-bottom: 18px;
}

.help-panel__eyebrow {
  display: inline-block;
  margin-bottom: 10px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #ecfdf8;
  color: #0f766e;
  font-size: 0.78rem;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.help-panel__head h2 {
  margin: 0;
  font-size: clamp(1.4rem, 2vw, 1.8rem);
  line-height: 1.15;
}

.faq-list {
  display: grid;
  gap: 12px;
}

.faq-item {
  border: 1px solid #dbe7e4;
  border-radius: 18px;
  background: #fff;
  overflow: hidden;
}

.faq-item summary {
  cursor: pointer;
  list-style: none;
  padding: 16px 18px;
  font-weight: 700;
  color: #0f172a;
}

.faq-item summary::-webkit-details-marker {
  display: none;
}

.faq-item p {
  margin: 0;
  padding: 0 18px 16px;
  color: #475569;
  line-height: 1.7;
}

.contact-box {
  display: grid;
  gap: 14px;
}

.contact-box > div {
  padding: 14px 16px;
  border-radius: 18px;
  background: #f8fbfa;
  border: 1px solid #e0ece8;
}

.contact-box strong {
  display: block;
  margin-bottom: 4px;
  color: #0f172a;
}

.contact-box p {
  margin: 0;
  color: #475569;
}

.help-footer-note {
  max-width: 1180px;
  margin: 20px auto 0;
  padding: 14px 18px;
  border-radius: 12px;
  background: transparent;
  border: none;
  box-shadow: none;
  color: #0f172a;
  line-height: 1.7;
}

.notice-asterisk {
  color: #1fb981;
  font-weight: 900;
  margin-right: 12px;
  font-size: 1.25rem;
  vertical-align: middle;
}

.help-footer-note p {
  margin: 0;
}

@media (max-width: 1024px) {
  .help-hero__content,
  .help-layout {
    grid-template-columns: 1fr;
  }

  .help-hero__actions {
    justify-content: flex-start;
  }

  .contact-actions {
    justify-content: center;
  }

  .help-panel--sticky {
    position: static;
  }

  .help-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .help-page {
    padding: 14px;
  }

  .help-hero,
  .help-panel,
  .help-footer-note {
    border-radius: 22px;
  }

  .help-grid {
    grid-template-columns: 1fr;
  }

  .help-primary-link,
  .help-secondary-link,
  .help-card__link {
    width: 100%;
  }
}
</style>
