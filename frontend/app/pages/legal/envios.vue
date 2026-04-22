<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Política de Envíos</h1>
      
      <section>
        <h2>1. Responsabilidades del Vendedor</h2>
        <p>
          El vendedor es responsable de:
        </p>
        <ul>
          <li>Empacar el producto de forma segura y profesional</li>
          <li>Usar materiales adecuados para proteger el artículo</li>
          <li>Enviar dentro de 2 días hábiles de la compra</li>
          <li>Usar un servicio de envío confiable y rastreable</li>
          <li>Proporcionar número de seguimiento al comprador</li>
        </ul>
      </section>

      <section>
        <h2>2. Tiempos de Entrega Estimados</h2>
        <p>
          Los tiempos estimados son:
        </p>
        <ul>
          <li><strong>Mismo país:</strong> 3-7 días hábiles</li>
          <li><strong>Transporte urgente:</strong> 1-2 días hábiles (costo adicional)</li>
          <li><strong>Internacional:</strong> 7-20 días (dependiendo del país)</li>
        </ul>
        <p>
          <em>Nota: Estos son estimados. Factors externos (aduanas, clima) pueden causar retrasos.</em>
        </p>
      </section>

      <section>
        <h2>3. Rastreo</h2>
        <p>
          Todos los envíos deben tener rastreo. El vendedor debe:
        </p>
        <ul>
          <li>Proporcionar número de seguimiento dentro de 24 horas</li>
          <li>Actualizar el estado en Closely</li>
          <li>Comunicar retrasos al comprador</li>
        </ul>
      </section>

      <section>
        <h2>4. Artículos No Entregados</h2>
        <p>
          Si el artículo no se entrega en los tiempos estimados:
        </p>
        <ul>
          <li>Verifica el rastreo</li>
          <li>Contacta al vendedor</li>
          <li>El vendedor puede contactar al servicio de envío</li>
          <li>Tras 30 días sin entrega, solicita reembolso</li>
        </ul>
      </section>

      <section>
        <h2>5. Artículos Dañados en Tránsito</h2>
        <p>
          Si recibes un artículo dañado:
        </p>
        <ul>
          <li>Toma fotos detalladas del daño</li>
          <li>Abre una disputa dentro de 48 horas de recepción</li>
          <li>El vendedor puede solicitar devolución del artículo dañado</li>
          <li>Se emitirá un reembolso o un nuevo envío</li>
        </ul>
      </section>

      <section>
        <h2>6. Costos de Envío</h2>
        <p>
          Los costos de envío los define el vendedor. Están incluidos en el precio final de venta. 
          No se pueden cobrar costos adicionales al comprador.
        </p>
      </section>

      <section>
        <h2>7. Devoluciones y Reenvíos</h2>
        <p>
          Ver <NuxtLink to="/legal/devoluciones">Políticas de Devoluciones</NuxtLink> para información 
          sobre cómo devolver artículos y costos asociados.
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
