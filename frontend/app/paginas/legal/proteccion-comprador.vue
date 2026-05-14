<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Protección del Comprador</h1>
      
      <section>
        <h2>1. Garantía de Compra Segura</h2>
        <p>
          Closely se compromete a proteger todas las transacciones realizadas a través de nuestra plataforma. 
          Si encuentras problemas, tienes derechos específicos de protección.
        </p>
      </section>

      <section>
        <h2>2. Descripción Exacta del Producto</h2>
        <p>
          Los vendedores deben proporcionar descripciones precisas y honestas de sus productos, incluyendo:
        </p>
        <ul>
          <li>Condición real del artículo</li>
          <li>Fotos claras desde diferentes ángulos</li>
          <li>Defectos visibles o daños</li>
          <li>Materiales y composición</li>
          <li>Medidas o talla de la prenda</li>
        </ul>
      </section>

      <section>
        <h2>3. Reembolso Garantizado</h2>
        <p>
          Si el artículo recibido no coincide con la descripción, puedes solicitar un reembolso completo dentro de 
          30 días de la entrega. Cubrimos:
        </p>
        <ul>
          <li>Artículos no recibidos</li>
          <li>Artículos significativamente diferentes</li>
          <li>Artículos dañados en envío</li>
          <li>Artículos falsificados</li>
        </ul>
      </section>

      <section>
        <h2>4. Proceso de Reclamación</h2>
        <p>
          Para reclamar:
        </p>
        <ol style="margin-left: 24px;">
          <li>Abre una reclamación dentro de 30 días de la entrega</li>
          <li>Proporciona evidencia (fotos, descripción)</li>
          <li>Comunícate con el vendedor</li>
          <li>Si no se resuelve, solicitamos mediación de Closely</li>
          <li>Finalmente, ejecutamos el reembolso</li>
        </ol>
      </section>

      <section>
        <h2>5. Artículos Prohibidos</h2>
        <p>
          No compramos protección por artículos ilegales, falsificados o prohibidos en el país:
        </p>
        <ul>
          <li>Armas o explosivos</li>
          <li>Drogas o sustancias controladas</li>
          <li>Artículos falsificados</li>
          <li>Material sexual ilegal</li>
          <li>Bienes robados</li>
        </ul>
      </section>

      <section>
        <h2>6. Seguridad de Pago</h2>
        <p>
          Todos los pagos se procesan a través de proveedores certificados con encriptación de nivel bancario. 
          Nunca compartimos números de tarjeta con vendedores.
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
