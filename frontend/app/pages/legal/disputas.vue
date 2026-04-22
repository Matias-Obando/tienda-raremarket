<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Resolución de Disputas</h1>
      
      <section>
        <h2>1. Tipos de Disputas</h2>
        <p>
          Closely media en disputas entre compradores y vendedores respecto a:
        </p>
        <ul>
          <li>Artículos no recibidos</li>
          <li>Artículos no como se describen</li>
          <li>Reembolsos no procesados</li>
          <li>Problemas de calidad o defectos</li>
        </ul>
      </section>

      <section>
        <h2>2. Cómo Abrir una Disputa</h2>
        <p>
          Si tienes un problema con una transacción:
        </p>
        <ol style="margin-left: 24px;">
          <li>Ve a tu página de órdenes</li>
          <li>Selecciona "Abrir disputa"</li>
          <li>Describe el problema con detalle</li>
          <li>Adjunta evidencia (fotos, capturas de pantalla)</li>
          <li>Espera respuesta del vendedor (48 horas)</li>
        </ol>
      </section>

      <section>
        <h2>3. Resolución Directa (Negociación)</h2>
        <p>
          Primero intentamos que tú y el vendedor resuelvan directamente:
        </p>
        <ul>
          <li>Tenéis 5 días para llegar a un acuerdo</li>
          <li>El vendedor puede proponer soluciones (reembolso parcial, devolución)</li>
          <li>Si ambos acuerdan, la disputa se cierra</li>
        </ul>
      </section>

      <section>
        <h2>4. Mediación de Closely</h2>
        <p>
          Si no hay acuerdo, nuestro equipo interviene:
        </p>
        <ul>
          <li>Revisamos toda la evidencia proporcionada</li>
          <li>Entrevistamos a ambas partes si es necesario</li>
          <li>Emitimos una resolución justa</li>
          <li>El proceso toma máximo 10 días</li>
        </ul>
      </section>

      <section>
        <h2>5. Decisión Final</h2>
        <p>
          Nuestras decisiones son finales y vinculantes. Consideramos:
        </p>
        <ul>
          <li>Descripción original del producto</li>
          <li>Evidencia fotográfica</li>
          <li>Historial de transacciones</li>
          <li>Reputación de ambas partes</li>
          <li>Políticas de comprador y vendedor</li>
        </ul>
      </section>

      <section>
        <h2>6. Apelaciones</h2>
        <p>
          Solo en casos excepcionales aceptamos apelaciones dentro de 3 días. 
          Una apelación será considerada si hay nueva evidencia material o error procedural claro.
        </p>
      </section>

      <section>
        <h2>7. Arbitraje Externo</h2>
        <p>
          Si no estás satisfecho con la resolución, puedes solicitar arbitraje externo según las leyes de tu país. 
          Los costos de arbitraje son responsabilidad de la parte que pierde.
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
