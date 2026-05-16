<template>
  <header class="sticky top-0 z-[90] bg-white border-b border-gray-200 pt-0 pb-0 shadow-none">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-2 md:py-3">
      <div class="flex items-center justify-between gap-4 sm:gap-5">
        <div class="flex items-center gap-4 flex-1 min-w-0">
          <NuxtLink to="/admin/dashboard" class="flex items-center gap-3 shrink-0">
            <img :src="logo" alt="Closely admin" class="h-10 sm:h-12 md:h-16 w-auto rounded-md" />
          </NuxtLink>

          <div class="hidden md:flex flex-1 px-2">
            <div class="admin-title-wrap">
              <div>
                <p class="admin-title-wrap__eyebrow">Panel interno</p>
                <h1 class="admin-title-wrap__title">Dashboard de administración</h1>
              </div>
            </div>
          </div>
        </div>

        <div class="flex items-center gap-2 sm:gap-3 shrink-0">
          <div class="hidden md:flex items-center gap-3">
            <NuxtLink to="/admin/dashboard" class="admin-pill admin-pill--active">Inicio</NuxtLink>
            <NuxtLink to="/inicio" class="admin-pill">Ir a la tienda</NuxtLink>
            <button class="admin-logout" type="button" @click="signOut">Cerrar sesión</button>
          </div>

          <div class="flex items-center gap-1.5 md:hidden">
            <NuxtLink to="/inicio" class="admin-mini-link">Tienda</NuxtLink>
            <button class="admin-logout admin-logout--compact" type="button" @click="signOut">Salir</button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import logoAsset from '~/recursos/photos/closely.png'

const { clearSessionUser } = useSessionUser()
const logo = logoAsset

function signOut() {
  clearSessionUser()
  navigateTo('/autenticacion?mode=login')
}
</script>

<style scoped>
.admin-title-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.admin-title-wrap__eyebrow {
  margin: 0 0 2px;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.18em;
  color: #6b7280;
}

.admin-title-wrap__title {
  margin: 0;
  font-size: 1.05rem;
  font-weight: 700;
  color: #0f172a;
}

.admin-pill {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 40px;
  padding: 0 16px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  background: #fff;
  color: #475569;
  text-decoration: none;
  font-size: 0.92rem;
  font-weight: 600;
  transition: background-color 0.18s ease, border-color 0.18s ease, color 0.18s ease, transform 0.18s ease;
}

.admin-pill:hover,
.admin-pill--active {
  color: #065f46;
  border-color: #a7f3d0;
  background: #ecfdf5;
  transform: translateY(-1px);
}

.admin-logout {
  border: 1px solid #fecaca;
  border-radius: 999px;
  padding: 0 18px;
  height: 40px;
  background: #fff;
  color: #dc2626;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.18s ease, border-color 0.18s ease, transform 0.18s ease;
}

.admin-logout:hover {
  background: #fef2f2;
  border-color: #fca5a5;
  transform: translateY(-1px);
}

.admin-mini-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  padding: 0 14px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  color: #475569;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 600;
  background: #fff;
}

.admin-logout--compact {
  padding: 0 14px;
}

@media (max-width: 768px) {
  .admin-title-wrap {
    display: none;
  }

  .admin-header__inner {
    padding: 12px 0;
  }

  .admin-header__nav {
    width: 100%;
  }

  .admin-header__button {
    width: 100%;
  }
}
</style>