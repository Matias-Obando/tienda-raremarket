<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Política de Cookies</h1>
      
      <section>
        <h2>1. ¿Qué son las Cookies?</h2>
        <p>
          Las cookies son pequeños archivos de texto que se almacenan en tu dispositivo cuando visitas nuestro sitio. 
          Se utilizan para mejorar tu experiencia, recordar preferencias y analizar el uso del sitio.
        </p>
      </section>

      <section>
        <h2>2. Tipos de Cookies que Usamos</h2>
        <ul>
          <li><strong>Cookies de sesión:</strong> Necesarias para mantener tu sesión activa</li>
          <li><strong>Cookies de autenticación:</strong> Para recordar que has iniciado sesión</li>
          <li><strong>Cookies de preferencias:</strong> Para guardar tus preferencias de idioma y tema</li>
          <li><strong>Cookies analíticas:</strong> Para entender cómo usas el sitio (Google Analytics)</li>
          <li><strong>Cookies de terceros:</strong> De redes sociales y publicidad</li>
        </ul>
      </section>

      <section>
        <h2>3. Control de Cookies</h2>
        <p>
          Puedes controlar las cookies a través de tu navegador:
        </p>
        <ul>
          <li>Aceptar todas las cookies</li>
          <li>Rechazar cookies no esenciales</li>
          <li>Personalizar qué cookies permitir</li>
          <li>Borrar cookies guardadas en cualquier momento</li>
        </ul>
      </section>

      <section>
        <h2>4. Cookies Esenciales</h2>
        <p>
          Algunas cookies son necesarias para que Closely funcione:
        </p>
        <ul>
          <li>Token de autenticación</li>
          <li>Identificador de sesión</li>
          <li>Preferencias de idioma</li>
        </ul>
        <p>
          No podemos ofrecer nuestros servicios sin estas cookies.
        </p>
      </section>

      <section>
        <h2>5. Privacidad</h2>
        <p>
          Las cookies no contienen información personal directa. Sin embargo, combinadas con otros datos, 
          pueden identificarte. Ver nuestra <NuxtLink to="/legal/privacidad">Política de Privacidad</NuxtLink> 
          para más información.
        </p>
      </section>

      <section>
        <h2>6. Cambios en Esta Política</h2>
        <p>
          Podemos actualizar esta política en cualquier momento. El uso continuado implica aceptación 
          de los cambios.
        </p>
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

ul {
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
