<template>
  <div class="rm-page sitemap-page">
    <div class="sitemap-container">
      <h1>Mapa del Sitio</h1>
      <p class="intro">
        A continuación encontrarás una estructura completa de todas las páginas y secciones disponibles en Closely.
      </p>

      <nav class="sitemap-nav">
        <section class="sitemap-section">
          <h2>🏠 Principales</h2>
          <ul>
            <li><NuxtLink to="/">Inicio</NuxtLink></li>
            <li><NuxtLink to="/explorar">Explorar</NuxtLink></li>
            <li><NuxtLink to="/favoritos">Favoritos</NuxtLink></li>
            <li><NuxtLink to="/vender">Vender</NuxtLink></li>
            <li><NuxtLink to="/chat">Chat</NuxtLink></li>
          </ul>
        </section>

        <section class="sitemap-section">
          <h2>👤 Perfil</h2>
          <ul>
            <li><NuxtLink to="/perfil">Mi Perfil</NuxtLink></li>
            <li><NuxtLink to="/editprofile">Editar Perfil</NuxtLink></li>
            <li><NuxtLink to="/auth">Autenticación</NuxtLink></li>
          </ul>
        </section>

        <section class="sitemap-section">
          <h2>⚖️ Legal</h2>
          <ul>
            <li><NuxtLink to="/legal/aviso-legal">Aviso Legal</NuxtLink></li>
            <li><NuxtLink to="/legal/terminos-condiciones">Términos y Condiciones</NuxtLink></li>
            <li><NuxtLink to="/legal/privacidad">Política de Privacidad</NuxtLink></li>
            <li><NuxtLink to="/legal/cookies">Política de Cookies</NuxtLink></li>
            <li><NuxtLink to="/legal/envios">Política de Envíos</NuxtLink></li>
            <li><NuxtLink to="/legal/devoluciones">Políticas de Devoluciones</NuxtLink></li>
            <li><NuxtLink to="/legal/proteccion-comprador">Protección del Comprador</NuxtLink></li>
            <li><NuxtLink to="/legal/terminos-vendedores">Términos para Vendedores</NuxtLink></li>
            <li><NuxtLink to="/legal/moderacion">Política de Moderación</NuxtLink></li>
            <li><NuxtLink to="/legal/disputas">Resolución de Disputas</NuxtLink></li>
            <li><NuxtLink to="/legal/gdpr">Protección de Datos (GDPR)</NuxtLink></li>
          </ul>
        </section>

        <section class="sitemap-section">
          <h2>ℹ️ Información</h2>
          <ul>
            <li><NuxtLink to="/acerca-de">Acerca de Nosotros</NuxtLink></li>
            <li><NuxtLink to="/sitemap">Mapa del Sitio</NuxtLink></li>
          </ul>
        </section>

        <section class="sitemap-section">
          <h2>📱 Dinámicas</h2>
          <ul>
            <li><NuxtLink to="/item/[id]">Detalle de Producto (dinámico)</NuxtLink></li>
            <li><NuxtLink to="/vendedor/[id]">Perfil de Vendedor (dinámico)</NuxtLink></li>
          </ul>
        </section>
      </nav>

      <section class="sitemap-info">
        <h2>Estructura del Sitio</h2>
        <p>
          Closely está organizado en las siguientes áreas principales:
        </p>
        <ul>
          <li><strong>Área Pública:</strong> Accesible a todos los visitantes (Inicio, Explorar, Detalle de Producto)</li>
          <li><strong>Área de Cuenta:</strong> Requiere autenticación (Perfil, Chat, Favoritos, Vender)</li>
          <li><strong>Área Legal:</strong> Documentos informativos y políticas de términos</li>
          <li><strong>Área de Información:</strong> Acerca de la plataforma y navegación</li>
        </ul>
      </section>

      <section class="sitemap-info">
        <h2>Enlaces de Interés</h2>
        <ul>
          <li>
            <strong>Soporte:</strong> 
            <a href="mailto:soporte@closely.com">soporte@closely.com</a>
          </li>
          <li>
            <strong>Privacidad:</strong> 
            <NuxtLink to="/legal/privacidad">Política de Privacidad</NuxtLink>
          </li>
          <li>
            <strong>Condiciones:</strong> 
            <NuxtLink to="/legal/terminos-condiciones">Términos y Condiciones</NuxtLink>
          </li>
          <li>
            <strong>Seguridad:</strong> 
            <NuxtLink to="/legal/proteccion-comprador">Protección del Comprador</NuxtLink>
          </li>
        </ul>
      </section>

      <div class="legal-footer">
        <p><strong>Última actualización:</strong> 22 de abril de 2026</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
definePageMeta({
  layout: 'default'
})
</script>

<style scoped>
.sitemap-page {
  background: var(--rm-page-bg);
  min-height: 100vh;
  padding: 40px 20px;
}

.sitemap-container {
  max-width: 900px;
  margin: 0 auto;
  background: white;
  border-radius: var(--rm-radius);
  padding: 40px;
  line-height: 1.8;
  color: var(--rm-text);
}

h1 {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: var(--rm-text);
}

.intro {
  color: #666;
  margin-bottom: 40px;
  font-size: 1.05rem;
}

.sitemap-nav {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
  margin-bottom: 40px;
}

.sitemap-section {
  padding: 24px;
  background: #f9fafb;
  border-radius: 12px;
  border-left: 4px solid #1fb981;
}

.sitemap-section h2 {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--rm-text);
  margin-bottom: 16px;
  margin-top: 0;
}

.sitemap-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sitemap-section li {
  margin-bottom: 12px;
}

.sitemap-section a {
  color: #1fb981;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.sitemap-section a:hover {
  color: #17a371;
  text-decoration: underline;
}

.sitemap-info {
  margin-bottom: 40px;
  padding: 24px;
  background: #f0fdf4;
  border-radius: 12px;
}

.sitemap-info h2 {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--rm-text);
  margin-top: 0;
  margin-bottom: 16px;
}

.sitemap-info ul {
  margin-left: 24px;
}

.sitemap-info li {
  margin-bottom: 12px;
  color: #555;
}

.sitemap-info a {
  color: #1fb981;
  text-decoration: none;
}

.sitemap-info a:hover {
  text-decoration: underline;
}

.legal-footer {
  margin-top: 50px;
  padding-top: 24px;
  border-top: 1px solid var(--rm-border);
  text-align: center;
  color: var(--rm-muted);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .sitemap-container {
    padding: 24px;
  }

  h1 {
    font-size: 1.8rem;
  }

  .sitemap-nav {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
