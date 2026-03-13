<template>
  <footer class="site-footer" role="contentinfo" aria-labelledby="footer-brand">
    <div class="footer-top">
      <div class="container">
        <div class="left">
          <div id="footer-brand" class="brand">closely</div>
          <div class="copy">© 2026 Raremarket. Todos los derechos reservados</div>

          <div class="social" aria-label="Redes sociales">
            <a class="icon" href="#" aria-label="Instagram" title="Instagram">
              <img class="icon-img" src="/icons/instagram.svg" alt="Instagram" />
            </a>

            <a class="icon" href="#" aria-label="Facebook" title="Facebook">
              <img class="icon-img" src="/icons/facebook.svg" alt="Facebook" />
            </a>
          </div>
        </div>

        <nav class="legal" aria-label="Enlaces legales">
          <div class="legal-title">Legal</div>
          <ul class="legal-list">
            <li><a class="l" href="#">Aviso legal</a></li>
            <li><a class="l" href="#">Términos Y Condiciones</a></li>
            <li><a class="l" href="#">Política de privacidad</a></li>
            <li><a class="l" href="#">Política de cookies</a></li>
            <li><a class="l" href="#">Política de envío</a></li>
            <li><a class="l" href="#">Políticas de devoluciones</a></li>
          </ul>
        </nav>
      </div>
    </div>

    <div class="footer-bottom" role="contentinfo">
      <div class="payments" role="list" aria-label="Métodos de pago">
        <img class="pay-img" src="/icons/googlepay.svg" alt="Google Pay" role="listitem" />
        <img class="pay-img" src="/icons/applepay.svg" alt="Apple Pay" role="listitem" />

        <div class="pay pay-mastercard" role="listitem" aria-label="Mastercard" title="Mastercard">
          <svg viewBox="0 0 48 20" width="48" height="20" xmlns="http://www.w3.org/2000/svg" aria-hidden="false" focusable="false" role="img">
            <title>Mastercard</title>
            <circle cx="18" cy="10" r="7.5" fill="#EB001B"/>
            <circle cx="30" cy="10" r="7.5" fill="#F79E1B" />
            <path d="M24,10 a6,6 0 0,1 0,-0.1 6,6 0 0,1 0,0.1" fill="rgba(0,0,0,0.06)" opacity="0.05"/>
          </svg>
        </div>

        <img class="pay-img" src="/icons/visa.svg" alt="Visa" role="listitem" />
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style scoped>
.site-footer {
  font-family: system-ui, -apple-system, "Segoe UI", Roboto, "Helvetica Neue", Arial;
}

.footer-top {
  background: #27b79f; 
  color: #fff;
  border-bottom: 1px solid rgba(0,0,0,0.12);
}
.container {
  max-width: 1140px;
  margin: 0 auto;
  padding: 28px 20px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  align-items: start;
  box-sizing: border-box;
}

.left { display:flex; gap:14px; flex-direction:column; }
.brand {
  font-weight: 800;
  font-size: 24px;
  text-transform: none;
  letter-spacing: -0.01em;
  color: #fff;
}
.copy {
  font-size: 13px;
  color: rgba(255,255,255,0.9);
  margin-top: 6px;
}

.social { display:flex; gap:12px; margin-top: 8px; }
.icon {
  display:inline-grid;
  place-items:center;
  width:36px;
  height:36px;
  border-radius:50%;
  background: rgba(255,255,255,0.06);
  color: #fff;
  text-decoration:none;
  overflow: hidden;
}
.icon-img {
  width: 18px;
  height: 18px;
  display: block;
  filter: brightness(0) saturate(100%) invert(1);
  -webkit-filter: brightness(0) saturate(100%) invert(1);
}

.legal { text-align: left; }
.legal-title {
  font-weight: 800;
  margin-bottom: 10px;
  color: #fff;
}
.legal-list { list-style: none; padding: 0; margin: 0; }
.l {
  display:block;
  color: rgba(255,255,255,0.95);
  text-decoration: none;
  margin: 6px 0;
  font-size: 14px;
}
.l:hover { text-decoration: underline; }

.footer-bottom {
  background: #fff;
  border-top: 2px solid rgba(0,0,0,0.06);
  padding: 18px 0;
}
.payments {
  max-width: 720px;
  margin: 0 auto;
  display:flex;
  gap: 22px;
  justify-content:center;
  align-items:center;
  flex-wrap:wrap;
}

.pay-img {
  height: 28px;
  width: auto;
  display: block;
  filter: none;
  -webkit-filter: none;
}

.pay-mastercard svg {
  height: 28px;
  width: auto;
  display: block;
}

@media (max-width: 740px) {
  .container { grid-template-columns: 1fr; gap: 18px; padding: 20px 16px; }
  .legal { order: 2; }
  .payments { gap: 12px; padding: 0 8px; }
  .pay-img, .pay-mastercard svg { height: 22px; }
}
</style>