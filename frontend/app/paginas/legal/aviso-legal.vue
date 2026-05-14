<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Aviso Legal</h1>
      
      <section>
        <h2>1. Información General</h2>
        <p>
          Closely es una plataforma de marketplace de compraventa de artículos. Este aviso legal establece los términos y condiciones 
          de uso de nuestro sitio web y servicios.
        </p>
      </section>

      <section>
        <h2>2. Titularidad del Sitio</h2>
        <p>
          La titularidad del sitio web, su dominio, estructura, diseño, código fuente y contenidos está a cargo de Closely, 
          con domicilio social en Av. de l'Anselm de Riu, 10, 08924 Santa Coloma de Gramenet, Barcelona.
        </p>
      </section>

      <section>
        <h2>3. Propiedad Intelectual</h2>
        <p>
          Todos los contenidos del sitio web (textos, imágenes, gráficos, logos, iconos, botones, software) están protegidos 
          por las leyes de propiedad intelectual e industrial. Está prohibida su reproducción, distribución o modificación 
          sin autorización explícita.
        </p>
      </section>

      <section>
        <h2>4. Responsabilidad de Contenidos</h2>
        <p>
          Closely no se hace responsable de:
        </p>
        <ul>
          <li>Errores u omisiones en los contenidos publicados</li>
          <li>Daños causados por virus o code malicioso</li>
          <li>Contenidos publicados por usuarios vendedores</li>
          <li>Interrupciones o indisponibilidad del servicio</li>
        </ul>
      </section>

      <section>
        <h2>5. Modificaciones</h2>
        <p>
          Nos reservamos el derecho de modificar este aviso legal en cualquier momento. Los cambios serán efectivos desde 
          el momento de su publicación.
        </p>
      </section>

      <section>
        <h2>6. Jurisdicción</h2>
        <p>
          Este aviso legal se rige por la legislación española. Para cualquier conflicto, se somete a la jurisdicción 
          competente en España.
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
