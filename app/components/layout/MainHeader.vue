<template>
  <header class="header">
    <div class="row">
      <div class="top">
        <NuxtLink to="/" class="brand">RareMarket</NuxtLink>

        <div class="right">
          <button class="iconBtn" type="button" aria-label="Buscar">🔍</button>
          <NuxtLink class="iconBtn" to="/perfil" aria-label="Perfil">👤</NuxtLink>
          <button class="iconBtn" type="button" aria-label="Cesta">🛒</button>
          <NuxtLink class="login" to="/auth/login">Login</NuxtLink>
        </div>
      </div>

      <nav class="nav">
        <NuxtLink class="link" to="/explorar">Explorar</NuxtLink>
        <NuxtLink class="link" to="/vender">Vender</NuxtLink>
      </nav>
    </div>
  </header>
</template>

<style scoped>
.header{
  background:#fff;
  border-bottom:1px solid #e9e9e9;
}


.row{
  max-width:1100px;
  margin:0 auto;
  padding:12px 16px;
  display:flex;
  flex-direction:column;
  gap:10px;
  min-width:0;
}


.top{
  display:flex;
  align-items:center;
  justify-content:space-between;
  gap:12px;
  min-width:0;
}

.brand{
  font-weight:900;
  letter-spacing:.2px;
  text-decoration:none;
  color:#111;
  font-size:20px;
  min-width:0;
  white-space:nowrap;
}


.nav{
  display:flex;
  gap:16px;
  align-items:center;
  flex-wrap:wrap;
}
.link{
  text-decoration:none;
  color:#111;
  font-weight:800;
  font-size:14px;
}
.link:hover{ text-decoration:underline; }


.right{
  display:flex;
  align-items:center;
  gap:8px;
  flex-wrap:wrap;
  justify-content:flex-end;
  min-width:0;
}

.iconBtn{
  text-decoration:none;
  border:0;
  background:transparent;
  cursor:pointer;
  padding:6px;
  border-radius:8px;
  font-size:16px;
  color:#111;
}
.iconBtn:hover{ background:#f3f3f3; }

.login{
  text-decoration:none;
  color:#111;
  border:1px solid #e9e9e9;
  padding:7px 10px;
  border-radius:10px;
  font-weight:800;
  font-size:13px;
  white-space:nowrap;
}


@media (min-width: 760px){
  .row{
    flex-direction:row;
    align-items:center;
    justify-content:space-between;
    gap:12px;
  }

  .top{
    flex: 1;
  }

  .nav{
    flex: 1;
    justify-content:center;
  }

  .right{
    flex: 1;
  }
}
</style>