<template>
  <footer class="footer">
    <div class="inner">
      <div>
        <div class="brand">RareMarket</div>
        <div class="copy">© 2026 Raremarket. Todos los derechos reservados</div>
      </div>

      <div class="legal">
        <div class="title">Legal</div>
        <a class="l" href="#">Aviso legal</a>
        <a class="l" href="#">Términos y condiciones</a>
        <a class="l" href="#">Política de privacidad</a>
        <a class="l" href="#">Política de cookies</a>
        <a class="l" href="#">Política de envío</a>
        <a class="l" href="#">Políticas de devoluciones</a>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer{ border-top:1px solid #e9e9e9; background:#fff; margin-top:40px; }
.inner{
  max-width:1100px;
  margin:0 auto;
  padding:28px 16px;
  display:grid;
  grid-template-columns: 1fr 1fr;
  gap:24px;
}
.brand{ font-weight:900; font-size:18px; }
.copy{ margin-top:10px; color:#666; font-size:13px; }
.title{ font-weight:900; margin-bottom:10px; }
.l{ display:block; text-decoration:none; color:#111; margin:6px 0; font-size:14px; }
.l:hover{ text-decoration:underline; }
@media (max-width: 720px){ .inner{ grid-template-columns:1fr; } }
</style>