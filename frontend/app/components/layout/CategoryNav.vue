<template>
  <div>
    <nav class="rm-catnav" aria-label="Categorías">
      <div class="rm-container rm-catnav__inner">
        <ul class="rm-catnav__scroller" role="tablist">
          <li
            v-for="c in effectiveCategories"
            :key="c.key"
            class="rm-catnav__cell"
          >
            <NuxtLink
              :to="linkFor(c.key)"
              class="rm-catnav__item"
              :aria-current="isActive(c.key) ? 'true' : 'false'"
              role="tab"
            >
              {{ c.label }}
            </NuxtLink>
          </li>
        </ul>
        <ul
          v-if="activeSubcategories.length"
          class="rm-subcatnav__scroller"
          role="tablist"
          aria-label="Subcategorías"
        >
          <li class="rm-subcatnav__cell">
            <NuxtLink
              :to="subcatLinkFor(null)"
              class="rm-subcatnav__item"
              :aria-current="!activeSubcategory ? 'true' : 'false'"
              role="tab"
            >
              Todas
            </NuxtLink>
          </li>
          <li
            v-for="sub in activeSubcategories"
            :key="sub"
            class="rm-subcatnav__cell"
          >
            <NuxtLink
              :to="subcatLinkFor(sub)"
              class="rm-subcatnav__item"
              :aria-current="isSubcatActive(sub) ? 'true' : 'false'"
              role="tab"
            >
              {{ sub }}
            </NuxtLink>
          </li>
        </ul>
      </div>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CATEGORY_TREE, getSubcategoriesByKey } from '~/constants/categories'
const route = useRoute()

const props = defineProps({
  categories: {
    type: Array as () => Array<{ key: string; label: string }>,
    default: () => ([
      { key: 'new', label: 'Inicio' },
      ...CATEGORY_TREE.map((node) => ({ key: node.key, label: node.label }))
    ])
  }
})

const effectiveCategories = computed(() => props.categories)
const activeCategoryKey = computed(() => {
  const cat = route.query.cat
  return typeof cat === 'string' && cat.length ? cat : null
})

const activeSubcategory = computed(() => {
  const subcat = route.query.subcat
  return typeof subcat === 'string' && subcat.length ? subcat : null
})

const activeSubcategories = computed(() => getSubcategoriesByKey(activeCategoryKey.value))

function linkFor(key: string) {
  const next: Record<string, any> = { ...route.query }
  const currentCat = typeof route.query.cat === 'string' ? route.query.cat : null

  if (key === 'new') {
    delete next.cat
    delete next.subcat
    return { path: '/explorar', query: next }
  }

  if (currentCat === key) {
    delete next.cat
    delete next.subcat
    return { path: '/explorar', query: next }
  }

  next.cat = key
  delete next.subcat
  return { path: '/explorar', query: next }
}

function subcatLinkFor(subcategory: string | null) {
  const next: Record<string, any> = { ...route.query }
  if (!activeCategoryKey.value) {
    return { path: '/explorar', query: next }
  }

  if (!subcategory || activeSubcategory.value === subcategory) {
    delete next.subcat
    return { path: '/explorar', query: next }
  }

  next.subcat = subcategory
  return { path: '/explorar', query: next }
}

function isActive(key: string) {
  const cat = route.query.cat
  if (!cat) return key === 'new'
  return String(cat) === key
}

function isSubcatActive(subcategory: string) {
  return activeSubcategory.value === subcategory
}


</script>

<style scoped>
.rm-catnav {
  position: relative;
  z-index: 40;
  background: #fff;
  border-bottom: 1px solid #d1d5db;
  box-sizing: border-box;
  overflow: visible;
}


.rm-catnav__inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 10px 16px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.rm-catnav__scroller {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  padding: 0;
  margin: 0;
  list-style: none;
  overflow: hidden;
  width: 100%;
  align-items: center;
  justify-content: center;
  white-space: normal;
}
.rm-catnav__cell{ display:inline-flex; align-items:center; height:100%; }
.rm-catnav__item{ display:inline-block; color:var(--rm-muted); font-weight:500; font-size:14px; padding:8px 6px; position:relative; text-decoration:none; transition:color .12s ease; white-space:nowrap; }
.rm-catnav__item:hover,.rm-catnav__item:focus{ color:var(--rm-text); outline:none; }
.rm-catnav__item[aria-current="true"]{ color:var(--rm-text); font-weight:600; }
.rm-catnav__item[aria-current="true"]::after{ content:""; position:absolute; left:6px; right:6px; bottom:0; height:2px; background:var(--rm-primary); border-radius:2px; }

.rm-subcatnav__scroller {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 0;
  margin: 0;
  list-style: none;
  width: 100%;
  align-items: center;
  justify-content: center;
}

.rm-subcatnav__cell {
  display: inline-flex;
  align-items: center;
}

.rm-subcatnav__item {
  display: inline-flex;
  align-items: center;
  min-height: 28px;
  padding: 0 10px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  color: var(--rm-muted);
  font-size: 12px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.12s ease;
}

.rm-subcatnav__item[aria-current="true"] {
  border-color: #0f766e;
  color: #0f766e;
  background: #ecfeff;
}

@media (max-width:640px){ .rm-catnav__scroller{ gap:8px 12px } .rm-catnav__item{ padding:6px 4px; font-size:13px } }

@media (max-width: 768px) {
  .rm-catnav {
    display: none;
  }
}
</style>
