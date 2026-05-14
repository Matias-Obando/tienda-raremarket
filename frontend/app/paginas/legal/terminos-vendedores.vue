<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Términos para Vendedores</h1>
      
      <section>
        <h2>1. Requisitos para Vender</h2>
        <p>
          Para vender en Closely, debes:
        </p>
        <ul>
          <li>Ser mayor de 18 años</li>
          <li>Completar tu perfil con información veraz</li>
          <li>Tener una cuenta activa verificada</li>
          <li>Aceptar estos términos y nuestra política de moderación</li>
        </ul>
      </section>

      <section>
        <h2>2. Estándares de Productos</h2>
        <p>
          Como vendedor, garantizas que:
        </p>
        <ul>
          <li>Los artículos son legales</li>
          <li>Las descripciones son precisas y honestas</li>
          <li>Las imágenes reflejan el estado real del producto</li>
          <li>Tienes derecho legal a vender el artículo</li>
          <li>No contiene defectos ocultos</li>
        </ul>
      </section>

      <section>
        <h2>3. Comisiones y Pagos</h2>
        <p>
          Closely cobra una comisión del <strong>7.5%</strong> sobre el precio final de venta (incluyendo gastos de envío). 
          Los pagos se depositan en tu cuenta cada viernes por las ventas de la semana anterior.
        </p>
      </section>

      <section>
        <h2>4. Envío y Logística</h2>
        <p>
          Tú eres responsable de:
        </p>
        <ul>
          <li>Empacar el artículo adecuadamente</li>
          <li>Enviar dentro de 2 días de la venta</li>
          <li>Usar un servicio de envío confiable</li>
          <li>Proporcionar número de seguimiento</li>
          <li>Contactar al comprador ante cualquier problema</li>
        </ul>
      </section>

      <section>
        <h2>5. Devoluciones de Vendedor</h2>
        <p>
          Los compradores tienen 15 días para solicitar devolución si el artículo no coincide con la descripción. 
          Debes procesar reembolsos dentro de 7 días. Si el comprador tiene razón, la comisión no te será devuelta.
        </p>
      </section>

      <section>
        <h2>6. Conducta Prohibida</h2>
        <p>
          No puedes:
        </p>
        <ul>
          <li>Vender artículos falsificados o robados</li>
          <li>Evitar la plataforma para transacciones (ofender contacto directo)</li>
          <li>Estafar o engañar a compradores</li>
          <li>Vender artículos prohibidos</li>
          <li>Crear múltiples cuentas para evitar restricciones</li>
          <li>Publicar información falsa de otros vendedores</li>
        </ul>
      </section>

      <section>
        <h2>7. Suspensión de Cuenta</h2>
        <p>
          Podemos suspender tu cuenta si:
        </p>
        <ul>
          <li>Violas nuestros términos o política de moderación</li>
          <li>Tienes múltiples reclamaciones justificadas</li>
          <li>Realizas actividades fraudulentas</li>
          <li>Tu tasa de insatisfacción supera el 5%</li>
        </ul>
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
