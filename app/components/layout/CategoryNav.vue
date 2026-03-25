<template>
  <div>

    <div class="rm-catnav-placeholder" aria-hidden="true"></div>


    <nav v-if="!isMobile" class="rm-catnav" aria-label="Categorías">
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
      </div>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'
const route = useRoute()

const props = defineProps({
  categories: {
    type: Array as () => Array<{ key: string; label: string }>,
    default: () => ([
      { key: 'new', label: 'Inicio' },
      { key: 'abrigos', label: 'Abrigos' },
      { key: 'chaquetas', label: 'Chaquetas' },
      { key: 'jerseis', label: 'Jerséis & Sudaderas' },
      { key: 'vestidos', label: 'Vestidos' },
      { key: 'camisas', label: 'Camisas & Camisetas' },
      { key: 'pantalones', label: 'Pantalones' },
      { key: 'vaqueros', label: 'Vaqueros' },
      { key: 'calzado', label: 'Calzado' },
      { key: 'bolsos', label: 'Bolsos' }
    ])
  }
})

const effectiveCategories = computed(() => props.categories)

function linkFor(key: string) {
  const next: Record<string, any> = { ...route.query }
  const currentCat = typeof route.query.cat === 'string' ? route.query.cat : null

  if (key === 'new') {
    delete next.cat
    return { path: '/explorar', query: next }
  }

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


const isMobile = ref(false)
function checkIsMobile() {
  isMobile.value = typeof window !== 'undefined' ? window.innerWidth <= 768 : false
}


function updateCatnavVars() {
  const headerEl = document.querySelector('header')
  const navEl = document.querySelector('.rm-catnav')

  const headerH = headerEl ? Math.round(headerEl.getBoundingClientRect().height) : 72

  const navH = navEl ? Math.round(navEl.getBoundingClientRect().height) : 0

  document.documentElement.style.setProperty('--catnav-top', `${headerH}px`)
  document.documentElement.style.setProperty('--catnav-height', `${navH}px`)
}

let rafId: number | null = null
function scheduleMeasurements() {

  rafId = requestAnimationFrame(() => updateCatnavVars())
  window.addEventListener('load', () => {
    setTimeout(updateCatnavVars, 50)
    setTimeout(updateCatnavVars, 250)
  }, { once: true })
  setTimeout(updateCatnavVars, 120)
}

onMounted(() => {
  checkIsMobile()
  scheduleMeasurements()
  window.addEventListener('resize', () => {
    checkIsMobile()

    updateCatnavVars()
  })
})

onBeforeUnmount(() => {
  if (rafId) cancelAnimationFrame(rafId)
  window.removeEventListener('resize', updateCatnavVars)
})
</script>

<style scoped>

.rm-catnav-placeholder {
  display: none;
}


.rm-catnav {
  position: fixed;
  top: var(--catnav-top, 72px);
  left: 0;
  right: 0;
  z-index: 60;
  background: #fff;
  border-bottom: 1px solid var(--rm-border);
  box-sizing: border-box;
  overflow: visible;
}


.rm-catnav::before {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  transform: translateY(1px);
  height: 1px;
  background: var(--rm-border, #e5e7eb);
  pointer-events: none;
  z-index: 61;
}


.rm-catnav__inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 10px 16px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
}
.rm-catnav__scroller {
  display:flex; gap:16px; padding:0; margin:0; list-style:none; overflow-x:auto; overflow-y:hidden; -webkit-overflow-scrolling:touch;
  scrollbar-width:none; width:100%; align-items:center; white-space:nowrap; justify-content:flex-start;
}
.rm-catnav__scroller::-webkit-scrollbar{ display:none; }
@media (min-width:768px){ .rm-catnav__scroller{ justify-content:center } }
.rm-catnav__cell{ display:inline-flex; align-items:center; height:100%; }
.rm-catnav__item{ display:inline-block; color:var(--rm-muted); font-weight:500; font-size:14px; padding:8px 6px; position:relative; text-decoration:none; transition:color .12s ease; white-space:nowrap; }
.rm-catnav__item:hover,.rm-catnav__item:focus{ color:var(--rm-text); outline:none; }
.rm-catnav__item[aria-current="true"]{ color:var(--rm-text); font-weight:600; }
.rm-catnav__item[aria-current="true"]::after{ content:""; position:absolute; left:6px; right:6px; bottom:0; height:2px; background:var(--rm-primary); border-radius:2px; }

@media (max-width:640px){ .rm-catnav__scroller{ gap:12px } .rm-catnav__item{ padding:6px 4px; font-size:13px } }
</style>