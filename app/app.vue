<template>
  <div class="min-h-screen bg-white text-slate-900">
    <header class="border-b">
      <div class="mx-auto flex max-w-5xl items-center justify-between px-4 py-3">
        <NuxtLink to="/" class="text-lg font-bold">RareMarket</NuxtLink>

        <nav class="flex items-center gap-4 text-sm">
          <NuxtLink to="/explorar" class="hover:underline">Explorar</NuxtLink>
          <NuxtLink to="/vender" class="hover:underline">Vender</NuxtLink>
          <NuxtLink to="/perfil" class="hover:underline">Yo</NuxtLink>
          <NuxtLink to="/auth/login" class="rounded border px-3 py-1 hover:bg-slate-50">
            Login
          </NuxtLink>
        </nav>
      </div>
    </header>

    <main class="mx-auto max-w-5xl px-4 py-6">
      <NuxtPage />
    </main>
  </div>
</template>