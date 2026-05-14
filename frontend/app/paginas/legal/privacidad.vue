<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Política de Privacidad</h1>
      
      <section>
        <h2>1. Información que Recopilamos</h2>
        <p>
          Recopilamos información personal cuando te registras en Closely, realizas transacciones o usas nuestros servicios:
        </p>
        <ul>
          <li>Nombre, email, teléfono</li>
          <li>Dirección de envío</li>
          <li>Información de pago</li>
          <li>Datos de navegación y uso</li>
          <li>Fotografías de perfil e imágenes de productos</li>
        </ul>
      </section>

      <section>
        <h2>2. Uso de Información</h2>
        <p>
          Usamos tu información para:
        </p>
        <ul>
          <li>Facilitar transacciones entre compradores y vendedores</li>
          <li>Procesar pagos y envíos</li>
          <li>Mejorar nuestros servicios</li>
          <li>Comunicaciones y soporte</li>
          <li>Cumplir obligaciones legales</li>
        </ul>
      </section>

      <section>
        <h2>3. Protección de Datos</h2>
        <p>
          Implementamos medidas técnicas y organizativas para proteger tu información personal, incluyendo cifrado SSL 
          y acceso restringido a datos sensibles.
        </p>
      </section>

      <section>
        <h2>4. Compartición de Datos</h2>
        <p>
          No compartimos tu información personal con terceros, excepto:
        </p>
        <ul>
          <li>Con operadores de pago autorizados</li>
          <li>Cuando lo requiere la ley</li>
          <li>Para prestar servicios (hosting, análisis)</li>
          <li>Con tu consentimiento explícito</li>
        </ul>
      </section>

      <section>
        <h2>5. Derechos del Usuario</h2>
        <p>
          Tienes derecho a acceder, rectificar, suprimir o exportar tus datos personales. 
          Contacta a soporte@closely.com para ejercer estos derechos.
        </p>
      </section>

      <section>
        <h2>6. Retención de Datos</h2>
        <p>
          Conservamos tus datos mientras tu cuenta esté activa y por un período adicional según lo requiera la ley. 
          Puedes solicitar la eliminación de tu cuenta en cualquier momento.
        </p>
      </section>

      <section>
        <h2>7. Cambios en la Política</h2>
        <p>
          Podemos actualizar esta política en cualquier momento. Te notificaremos de cambios significativos por email.
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
