<template>
  <div class="rm-page legal-page">
    <div class="legal-container">
      <h1>Protección de Datos (GDPR)</h1>
      
      <section>
        <h2>1. Cumplimiento GDPR</h2>
        <p>
          Closely cumple con el Reglamento General de Protección de Datos (GDPR) de la Unión Europea 
          y con legislaciones similares en otros países. Esta política explica cómo protegemos tus derechos.
        </p>
      </section>

      <section>
        <h2>2. Base Legal para Procesar Datos</h2>
        <p>
          Procesamos tus datos personales bajo estas bases:
        </p>
        <ul>
          <li><strong>Contrato:</strong> Necesario para realizar transacciones</li>
          <li><strong>Consentimiento:</strong> Para marketing y analíticos (puedes retirar)</li>
          <li><strong>Obligación legal:</strong> Para cumplir leyes (impuestos, fraude)</li>
          <li><strong>Interés legítimo:</strong> Para mejorar servicios y seguridad</li>
        </ul>
      </section>

      <section>
        <h2>3. Tus Derechos GDPR</h2>
        <p>
          Tienes derecho a:
        </p>
        <ul>
          <li><strong>Acceso:</strong> Ver qué datos tenemos de ti</li>
          <li><strong>Rectificación:</strong> Corregir datos incorrectos</li>
          <li><strong>Supresión:</strong> Borrar tus datos ("derecho al olvido")</li>
          <li><strong>Portabilidad:</strong> Recibir tus datos en formato estándar</li>
          <li><strong>Oposición:</strong> Rechazar procesamiento para marketing</li>
          <li><strong>Restricción:</strong> Limitar cómo usamos tus datos</li>
        </ul>
      </section>

      <section>
        <h2>4. Cómo Ejercer tus Derechos</h2>
        <p>
          Para ejercer cualquier derecho, contacta a:
        </p>
        <p style="margin: 12px 0; padding: 12px; background: #f3f4f6; border-radius: 8px;">
          <strong>Email:</strong> privacy@closely.com<br>
          <strong>Asunto:</strong> "Ejercicio de derechos GDPR"<br>
          <strong>Incluye:</strong> Tu nombre, email y descripción clara de tu solicitud
        </p>
        <p>
          Responderemos en <strong>30 días</strong> (extendible a 60 días en casos complejos).
        </p>
      </section>

      <section>
        <h2>5. Transferencias Internacionales</h2>
        <p>
          Si transfiero tus datos fuera de la UE, implementamos:
        </p>
        <ul>
          <li>Cláusulas contractuales estándar de la UE</li>
          <li>Decisiones de adecuación de la Comisión Europea</li>
          <li>Consentimiento explícito cuando sea necesario</li>
        </ul>
      </section>

      <section>
        <h2>6. Cookies y Rastreo</h2>
        <p>
          Usamos cookies solo con tu consentimiento (excepto las esenciales). 
          Puedes cambiar tus preferencias en cualquier momento a través de las configuraciones de tu navegador.
        </p>
      </section>

      <section>
        <h2>7. Retención de Datos</h2>
        <p>
          Retenemos datos el tiempo mínimo necesario:
        </p>
        <ul>
          <li><strong>Datos de transacciones:</strong> 7 años (requerido por ley)</li>
          <li><strong>Datos de perfil:</strong> Mientras tu cuenta esté activa + 30 días</li>
          <li><strong>Datos de marketing:</strong> Hasta que retires consentimiento</li>
          <li><strong>Logs de seguridad:</strong> Máximo 90 días</li>
        </ul>
      </section>

      <section>
        <h2>8. Brechas de Seguridad</h2>
        <p>
          Si hay una brecha de datos que afecte tus derechos y libertades, 
          te notificaremos en <strong>72 horas</strong> de tomar conocimiento.
        </p>
      </section>

      <section>
        <h2>9. Encargado de Protección de Datos (DPO)</h2>
        <p>
          Nuestro DPO está disponible en: <strong>dpo@closely.com</strong>
        </p>
      </section>

      <section>
        <h2>10. Derecho a Presentar Reclamación</h2>
        <p>
          Si crees que violamos GDPR, puedes presentar una reclamación ante la 
          <strong>Autoridad de Protección de Datos</strong> de tu país.
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
