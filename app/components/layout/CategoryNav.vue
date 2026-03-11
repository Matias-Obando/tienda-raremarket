<template>
  <nav class="rm-catnav" aria-label="Categorías">
    <div class="rm-container rm-catnav__inner">
      <div class="rm-catnav__scroller">
        <NuxtLink
          v-for="c in categories"
          :key="c.key"
          :to="linkFor(c.key)"
          class="rm-catnav__item"
          :class="{ 'is-active': isActive(c.key) }"
        >
          {{ c.label }}
        </NuxtLink>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
const route = useRoute()

const categories = [
  { key: 'new', label: 'Recién llegados' },
  { key: 'pants', label: 'Pantalones' },
  { key: 'shirts', label: 'Camisetas' },
  { key: 'sneakers', label: 'Zapatillas' },
  { key: 'acc', label: 'Accesorios' },
]


function linkFor(key: string) {
  const next: Record<string, any> = { ...route.query }

  const currentCat = typeof route.query.cat === 'string' ? route.query.cat : null

  if (key === 'new') {
    delete next.cat
    return { path: '/explorar', query: next }
  }

  // toggle si ya está activa
  if (currentCat === key) {
    delete next.cat
    return { path: '/explorar', query: next }
  }

  next.cat = key
  return { path: '/explorar', query: next }
}

function isActive(key: string) {
  const cat = route.query.cat
  if (!cat) return key === 'new'
  return String(cat) === key
}
</script>

<style scoped>
.rm-catnav {
  background: #fff;
  border-bottom: 1px solid var(--rm-border);
}

.rm-catnav__inner {
  height: 44px;
  display: flex;
  align-items: center;
}

.rm-catnav__scroller {
  display: flex;
  align-items: center;
  gap: 18px;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  padding: 0 2px;
}
.rm-catnav__scroller::-webkit-scrollbar {
  display: none;
}

.rm-catnav__item {
  position: relative;
  white-space: nowrap;
  font-weight: 700;
  font-size: 14px;
  color: var(--rm-muted);
  padding: 10px 2px;
}

.rm-catnav__item:hover {
  color: var(--rm-text);
}

.rm-catnav__item.is-active {
  color: var(--rm-text);
}

.rm-catnav__item.is-active::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: -1px;
  height: 2px;
  background: var(--rm-primary);
  border-radius: 2px;
}


@media (min-width: 900px) {
  .rm-catnav__scroller {
    gap: 22px;
  }
}
</style>