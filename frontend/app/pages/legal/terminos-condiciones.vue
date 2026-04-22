<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Términos y Condiciones</h1>
      
      <section>
        <h2>1. Aceptación de Términos</h2>
        <p>
          Al acceder y usar Closely, aceptas estar vinculado por estos Términos y Condiciones. Si no estás de acuerdo con 
          alguno de estos términos, no debes usar nuestro servicio.
        </p>
      </section>

      <section>
        <h2>2. Descripción del Servicio</h2>
        <p>
          Closely es una plataforma digital que facilita la compra y venta de artículos entre usuarios. No somos vendedor 
          ni propietario de los productos, sino un intermediario facilitador de transacciones.
        </p>
      </section>

      <section>
        <h2>3. Registro de Cuenta</h2>
        <p>
          Para usar ciertos servicios, debes crear una cuenta. Garantizas que:
        </p>
        <ul>
          <li>La información proporcionada es veraz y completa</li>
          <li>Tienes al menos 18 años de edad</li>
          <li>Mantendrás la confidencialidad de tu contraseña</li>
          <li>Eres responsable de todas las actividades en tu cuenta</li>
        </ul>
      </section>

      <section>
        <h2>4. Conducta de Usuarios</h2>
        <p>
          Te comprometes a no:
        </p>
        <ul>
          <li>Publicar contenido ilegal, ofensivo o discriminatorio</li>
          <li>Engañar a otros usuarios o falsificar información</li>
          <li>Usar la plataforma para actividades ilegales</li>
          <li>Vulnerar derechos de terceros (propiedad intelectual, privacidad)</li>
          <li>Interferir con el funcionamiento de la plataforma</li>
        </ul>
      </section>

      <section>
        <h2>5. Transacciones</h2>
        <p>
          Todas las transacciones entre compradores y vendedores son acuerdos bilaterales. Closely actúa como intermediario 
          y no es parte responsable de incumplimientos de las partes.
        </p>
      </section>

      <section>
        <h2>6. Limitación de Responsabilidad</h2>
        <p>
          Closely no se responsabiliza por:
        </p>
        <ul>
          <li>Pérdida de datos o información</li>
          <li>Daños indirectos o consecuentes</li>
          <li>Calidad o legalidad de productos publicados</li>
          <li>Comportamiento de otros usuarios</li>
        </ul>
      </section>

      <section>
        <h2>7. Modificación y Terminación</h2>
        <p>
          Nos reservamos el derecho de suspender o terminar tu cuenta si violas estos términos. También podemos modificar 
          estos términos en cualquier momento. El uso continuado implica aceptación de cambios.
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
