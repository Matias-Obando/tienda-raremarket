<template>
  <div class="rm-page about-page">
    <div class="about-container">
      <h1>Acerca de Closely</h1>
      
      <section class="hero">
        <h2>Nuestra Misión</h2>
        <p>
          Closely es una plataforma de marketplace que conecta compradores y vendedores de prendas únicas y especiales. 
          Creemos que cada objeto tiene una historia y merece encontrar a alguien que lo aprecie ,y contribuya a promover un consumo sostenible.
        </p>
      </section>

      <section>
        <h2>¿Quiénes Somos?</h2>
        <p>
          Somos un equipo apasionado por la tecnología y la sostenibilidad. Nacimos de la idea de crear un lugar seguro 
          donde las personas pudieran comprar y vender prendas sin preocupaciones, con total transparencia y protección.
        </p>
      </section>

      <section>
        <h2>Nuestros Valores</h2>
        <div class="values-grid">
          <div class="value-card">
            <h3>Seguridad</h3>
            <p>Proteges a nuestros usuarios con tecnología de encriptación de nivel bancario y procesos verificados.</p>
          </div>
          <div class="value-card">
            <h3>Confianza</h3>
            <p>Somos transparentes en nuestras políticas. Los usuarios saben exactamente cómo funciona nuestra plataforma.</p>
          </div>
          <div class="value-card">
            <h3>Sostenibilidad</h3>
            <p>Promovemos la reutilización y el consumo responsable para reducir residuos.</p>
          </div>
          <div class="value-card">
            <h3>Innovación</h3>
            <p>Mejoramos constantemente basándonos en feedback de usuarios y tendencias tecnológicas.</p>
          </div>
        </div>
      </section>

      <section>
        <h2>Cómo Funciona</h2>
        <ol style="margin-left: 24px;">
          <li><strong>Vendedor publica:</strong> Crea un anuncio con fotos y descripción detallada</li>
          <li><strong>Comprador encuentra:</strong> Busca y filtra artículos de su interés</li>
          <li><strong>Comprador compra:</strong> Realiza el pago de forma segura en nuestra plataforma</li>
          <li><strong>Vendedor envía:</strong> Empaca y envía el artículo con rastreo</li>
          <li><strong>Comprador recibe:</strong> Confirma la recepción y valida el estado</li>
          <li><strong>Reseñas:</strong> Ambas partes se califican mutuamente</li>
        </ol>
      </section>

      <section>
        <h2>Nuestro Compromiso</h2>
        <ul>
          <li>Soporte 24/7 para resolver problemas</li>
          <li>Mediación justa en disputas entre usuarios</li>
          <li>Protección contra fraude y estafas</li>
          <li>Cumplimiento de todas las leyes de protección de datos</li>
          <li>Mejora continua de nuestros servicios</li>
        </ul>
      </section>

      <section class="contact-section">
        <h2>Contacto</h2>
        <p>
          ¿Tienes preguntas o sugerencias? Nos encantaría escucharte.
        </p>
        <div class="contact-info">
          <p><strong>Email:</strong> soporte@closely.com</p>
          <p><strong>Teléfono:</strong> +34 921 21 45 89</p>
          <p><strong>Ubicación:</strong> Barcelona, España</p>
        </div>
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
.about-page {
  background: var(--rm-page-bg);
  min-height: 100vh;
  padding: 40px 20px;
}

.about-container {
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
  margin-bottom: 40px;
}

section.hero {
  padding: 32px;
  background: linear-gradient(135deg, #1fb981 0%, #17a371 100%);
  color: white;
  border-radius: 12px;
  margin-bottom: 40px;
}

section.hero h2 {
  color: white;
  margin-bottom: 16px;
}

section.hero p {
  color: rgba(255, 255, 255, 0.95);
}

h2 {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--rm-text);
  margin-bottom: 20px;
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
  margin-bottom: 12px;
  color: #555;
}

.values-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.value-card {
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
  border-left: 4px solid #1fb981;
}

.value-card h3 {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--rm-text);
  margin-bottom: 12px;
}

.value-card p {
  font-size: 0.95rem;
  color: #666;
}

.contact-section {
  background: #f3f4f6;
  padding: 32px;
  border-radius: 12px;
  margin: 40px 0;
}

.contact-info {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-top: 16px;
}

.contact-info p {
  margin-bottom: 12px;
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
  .about-container {
    padding: 24px;
  }

  h1 {
    font-size: 1.8rem;
  }

  h2 {
    font-size: 1.1rem;
  }

  .values-grid {
    grid-template-columns: 1fr;
  }
}
</style>
