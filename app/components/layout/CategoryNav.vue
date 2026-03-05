<template>
  <nav class="cats">
    <div class="inner">
      <NuxtLink class="cat" to="/explorar">Recién llegados</NuxtLink>
      <NuxtLink class="cat" to="/explorar?cat=pantalones">Pantalones</NuxtLink>
      <NuxtLink class="cat" to="/explorar?cat=camisetas">Camisetas</NuxtLink>
      <NuxtLink class="cat" to="/explorar?cat=gorras">Gorras</NuxtLink>
      <NuxtLink class="cat" to="/explorar?cat=accesorios">Accesorios</NuxtLink>
    </div>
  </nav>
</template>

<style scoped>
.cats{ background:#fff; border-bottom:1px solid #e9e9e9; }
.inner{
  max-width:1100px;
  margin:0 auto;
  padding:10px 16px;
  display:flex;
  gap:22px;
  overflow:auto;
}
.cat{
  text-decoration:none;
  color:#999;
  font-weight:800;
  white-space:nowrap;
  font-size:14px;
}
.cat:hover{ color:#111; }
</style>