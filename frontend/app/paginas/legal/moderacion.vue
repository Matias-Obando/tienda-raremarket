<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Política de Moderación</h1>
      
      <section>
        <h2>1. Contenido Prohibido</h2>
        <p>
          Closely no permite la publicación de:
        </p>
        <ul>
          <li><strong>Artículos ilegales:</strong> armas, drogas, componentes explosivos</li>
          <li><strong>Falsificaciones:</strong> productos con marcas falsas</li>
          <li><strong>Bienes robados:</strong> artículos de origen sospechoso</li>
          <li><strong>Contenido ofensivo:</strong> discurso de odio, discriminación</li>
          <li><strong>Material sexual ilegal:</strong> contenido de menores</li>
          <li><strong>Servicios prohibidos:</strong> venta de datos, servicios no legales</li>
          <li><strong>Spam o publicidad:</strong> enlaces maliciosos, esquemas piramidales</li>
        </ul>
      </section>

      <section>
        <h2>2. Privacidad y Seguridad</h2>
        <p>
          No puedes publicar:
        </p>
        <ul>
          <li>Información personal de otros (teléfono, dirección, documento)</li>
          <li>Material que viole derechos de privacidad</li>
          <li>Links a sitios de malware o phishing</li>
        </ul>
      </section>

      <section>
        <h2>3. Propiedad Intelectual</h2>
        <p>
          Las imágenes y descripciones deben ser tuyas o tener licencia. No puedes:
        </p>
        <ul>
          <li>Usar fotos de otros sitios web</li>
          <li>Copiar descripciones sin autorización</li>
          <li>Violar marcas registradas</li>
        </ul>
      </section>

      <section>
        <h2>4. Proceso de Reporte</h2>
        <p>
          Si encuentras contenido prohibido, puedes reportarlo:
        </p>
        <ol style="margin-left: 24px;">
          <li>Ponte en contacto con nuestro equipo de soporte</li>
          <li>y proporciona detalles del contenido prohibido</li>
          <li>Nuestro equipo lo revisará en 24 horas</li>
        </ol>
      </section>

      <section>
        <h2>5. Acciones de Moderación</h2>
        <p>
          Cuando encontramos contenido prohibido:
        </p>
        <ul>
          <li>Eliminamos el anuncio de forma inmediata</li>
          <li>Notificamos al usuario</li>
          <li>Registramos la violación</li>
          <li>Múltiples violaciones resultan en suspensión</li>
          <li>Cooperamos con autoridades si es necesario</li>
        </ul>
      </section>

      <section>
        <h2>6. Apelaciones</h2>
        <p>
          Si crees que tu anuncio fue removido erróneamente, puedes apelar dentro de 7 días contactando a 
          soporte@closely.com con evidencia de por qué debería restaurarse.
        </p>
      </section>

      <div class="legal-footer">
        <p><strong>Última actualización:</strong> 14 de mayo de 2026</p>
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
.legal-page {
  background: var(--rm-page-bg);
  min-height: 100vh;
  padding: 40px 20px;
}

.legal-container {
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
  margin-bottom: 40px;
  color: var(--rm-text);
}

section {
  margin-bottom: 32px;
}

h2 {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--rm-text);
  margin-bottom: 16px;
  margin-top: 24px;
}

p {
  margin-bottom: 12px;
  color: #555;
}

ul, ol {
  margin-left: 24px;
  margin-bottom: 16px;
}

li {
  margin-bottom: 8px;
  color: #555;
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
  .legal-container {
    padding: 24px;
  }

  h1 {
    font-size: 1.8rem;
  }

  h2 {
    font-size: 1.1rem;
  }
}
</style>
