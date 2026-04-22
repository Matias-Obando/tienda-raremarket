<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Políticas de Devoluciones</h1>
      
      <section>
        <h2>1. Derecho de Devolución</h2>
        <p>
          Los compradores tienen derecho a devolver artículos dentro de <strong>30 días</strong> de la entrega si:
        </p>
        <ul>
          <li>El artículo no coincide con la descripción</li>
          <li>El artículo tiene defectos o daños no mencionados</li>
          <li>El artículo está dañado en tránsito</li>
          <li>El artículo no fue entregado</li>
        </ul>
      </section>

      <section>
        <h2>2. Proceso de Devolución</h2>
        <p>
          Para devolver un artículo:
        </p>
        <ol style="margin-left: 24px;">
          <li>Abre una disputa/solicitud de devolución en tu perfil</li>
          <li>Proporciona motivo y evidencia (fotos, descripción)</li>
          <li>El vendedor responde en 48 horas</li>
          <li>Si ambos acuerdan, recibirás instrucciones de envío</li>
          <li>Envía el artículo en las condiciones que fue entregado</li>
          <li>El vendedor confirma recepción</li>
          <li>Se procesa el reembolso (5-10 días hábiles)</li>
        </ol>
      </section>

      <section>
        <h2>3. Condiciones para Devolución</h2>
        <p>
          El artículo debe ser devuelto:
        </p>
        <ul>
          <li>En condiciones razonables (usado es aceptable)</li>
          <li>Con todos los componentes originales si es posible</li>
          <li>Sin daños adicionales causados por negligencia</li>
          <li>En empaques seguros para evitar daños</li>
        </ul>
      </section>

      <section>
        <h2>4. Costos de Devolución</h2>
        <p>
          <strong>El vendedor paga los costos de devolución</strong> si:
        </p>
        <ul>
          <li>El artículo no coincide con la descripción</li>
          <li>Hay un defecto no revelado</li>
          <li>El artículo fue dañado por el vendedor</li>
        </ul>
        <p>
          <strong>El comprador paga los costos</strong> si:
        </p>
        <ul>
          <li>Arrepentimiento simple (cambió de idea)</li>
          <li>El artículo no te sirve personalmente</li>
        </ul>
        <p>
          <em>Nota: La mayoría de devoluciones en Closely son por culpa del vendedor, por lo que 
          generalmente el vendedor paga el retorno.</em>
        </p>
      </section>

      <section>
        <h2>5. Reembolsos</h2>
        <p>
          Una vez confirmada la devolución:
        </p>
        <ul>
          <li>Se reembolsa el precio del artículo íntegramente</li>
          <li>Se reembolsan los gastos de envío original (solo si fue culpa del vendedor)</li>
          <li>La comisión de Closely no se devuelve al vendedor</li>
          <li>El reembolso se procesa en 5-10 días hábiles</li>
        </ul>
      </section>

      <section>
        <h2>6. Excepciones (Sin Derecho a Devolución)</h2>
        <p>
          No hay derecho a devolución si:
        </p>
        <ul>
          <li>El artículo es consumible o perecedero</li>
          <li>Se trata de servicios digitales (no pueden devolverse)</li>
          <li>El comprador usa excesivamente el artículo</li>
          <li>Han pasado más de 30 días desde la entrega</li>
        </ul>
      </section>

      <section>
        <h2>7. Disputas de Devolución</h2>
        <p>
          Si hay desacuerdo sobre una devolución, consultamos nuestra 
          <NuxtLink to="/legal/disputas">Política de Resolución de Disputas</NuxtLink>.
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
