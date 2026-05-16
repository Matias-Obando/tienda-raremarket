<template>
  <div class="admin-dashboard-page">
    <div class="admin-shell">
      <aside class="admin-sidebar">
        <div class="admin-sidebar__header">
          <p class="admin-sidebar__eyebrow">Panel interno</p>
          <h1 class="admin-sidebar__title">Dashboard</h1>
        </div>

        <nav class="admin-sidebar__nav" aria-label="Secciones del dashboard">
          <button
            v-for="section in sections"
            :key="section.id"
            type="button"
            class="admin-sidebar__item"
            :class="{ 'admin-sidebar__item--active': activeSection === section.id }"
            @click="activeSection = section.id"
          >
            <span class="admin-sidebar__item-label">{{ section.label }}</span>
            <span class="admin-sidebar__item-meta">{{ section.meta }}</span>
          </button>
        </nav>

        <NuxtLink to="/inicio" class="admin-sidebar__back">Volver a la tienda</NuxtLink>
      </aside>

      <section class="admin-content">
        <header class="admin-content__hero">
          <p class="admin-content__eyebrow">Bienvenido, administrador</p>
          <h2 class="admin-content__title">Métricas reales conectadas a la base de datos</h2>
          <p class="admin-content__subtitle"></p>
        </header>

        <div v-if="loading" class="admin-state">Cargando métricas...</div>
        <div v-else-if="errorMessage" class="admin-state admin-state--error">{{ errorMessage }}</div>

        <template v-else-if="metrics">
          <section class="admin-summary-grid">
            <article v-for="card in summaryCards" :key="card.label" class="admin-summary-card">
              <p class="admin-summary-card__label">{{ card.label }}</p>
              <strong class="admin-summary-card__value">{{ card.value }}</strong>
              <span class="admin-summary-card__note">{{ card.note }}</span>
            </article>
          </section>

          <section v-if="activeSection === 'negocio'" class="admin-charts-grid">
            <AdminMetricChart
              title="Pedidos por día"
              eyebrow="Tendencia"
              subtitle="Volumen diario de pedidos en el rango seleccionado."
              type="line"
              :labels="metrics.ordersTrend.labels"
              :series="[{ name: 'Pedidos', data: metrics.ordersTrend.values }]"
              :colors="['#0f766e']"
            />

            <AdminMetricChart
              title="Ingresos por día"
              eyebrow="Tendencia"
              subtitle="Facturación diaria en euros, extraída de la base de datos."
              type="line"
              :labels="metrics.revenueTrend.labels"
              :series="[{ name: 'Ingresos (€)', data: metrics.revenueTrend.values }]"
              :colors="['#2563eb']"
            />

            <AdminMetricChart
              title="Categorías más activas"
              eyebrow="Catálogo"
              subtitle="Top de categorías con más artículos publicados."
              type="donut"
              :labels="metrics.topCategories.labels"
              :series="metrics.topCategories.values.map(value => Number(value))"
              :colors="topCategoryColors"
            />
          </section>

          <section v-else-if="activeSection === 'tecnico'" class="admin-charts-grid">
            <AdminMetricChart
              title="Estado de pedidos"
              eyebrow="Distribución"
              subtitle="Cómo se reparte el flujo comercial por estado."
              type="donut"
              :labels="metrics.orderStatuses.labels"
              :series="metrics.orderStatuses.values"
              :colors="['#0f766e', '#2563eb', '#f59e0b', '#7c3aed', '#ef4444']"
            />

            <AdminMetricChart
              title="Usuarios por rol"
              eyebrow="Accesos"
              subtitle="Distribución real entre usuarios y administradores."
              type="donut"
              :labels="metrics.userRoles.labels"
              :series="metrics.userRoles.values"
              :colors="['#0f766e', '#7c3aed', '#2563eb']"
            />
            <AdminMetricChart
              title="Tiempo de respuesta (ms)"
              eyebrow="Rendimiento"
              subtitle="Promedio diario del tiempo de respuesta de la página (ms)."
              type="line"
              :labels="metrics.responseTimeTrend.labels"
              :series="[{ name: 'RT (ms)', data: metrics.responseTimeTrend.values }]"
              :colors="['#ef4444']"
            />
          </section>

          <section v-else class="admin-grid">
            <div class="kpis-accordion">
              <details v-for="goal in kpiGoals" :key="goal.id" class="kpi-item">
                <summary class="kpi-summary">{{ goal.title }}</summary>
                <div class="kpi-body">
                  <p>{{ goal.description }}</p>
                </div>
              </details>
            </div>
          </section>
        </template>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import AdminMetricChart from '~/componentes/admin/AdminMetricChart.vue'

definePageMeta({
  middleware: []
})

type SectionId = 'negocio' | 'tecnico' | 'kpis'

type AdminMetrics = {
  summary: {
    totalUsers: number
    adminUsers: number
    totalItems: number
    availableItems: number
    totalOrders: number
    totalRevenue: number
    ordersLast30Days: number
    revenueLast30Days: number
    activeBuyersLast30Days: number
    activeSellersLast30Days: number
  }
  ordersTrend: { labels: string[]; values: number[] }
  revenueTrend: { labels: string[]; values: number[] }
  responseTimeTrend: { labels: string[]; values: number[] }
  orderStatuses: { labels: string[]; values: number[] }
  topCategories: { labels: string[]; values: number[] }
  userRoles: { labels: string[]; values: number[] }
}

const sections: Array<{ id: SectionId; label: string; meta: string }> = [
  { id: 'negocio', label: 'Negocio', meta: 'Clientes, productos y ventas' },
  { id: 'tecnico', label: 'Técnico', meta: 'Estado y soporte' },
  { id: 'kpis', label: 'KPIs', meta: 'Gráficos reales' }
]

const activeSection = ref<SectionId>('kpis')
const metrics = ref<AdminMetrics | null>(null)
const loading = ref(true)
const errorMessage = ref('')

const { public: runtimePublic } = useRuntimeConfig()
const { sessionUser, loadSessionUser } = useSessionUser()

const moneyFormatter = new Intl.NumberFormat('es-ES', {
  style: 'currency',
  currency: 'EUR',
  maximumFractionDigits: 0
})

const summaryCards = computed(() => {
  if (!metrics.value) {
    return []
  }

  const summary = metrics.value.summary

  return [
    { label: 'Usuarios', value: summary.totalUsers.toLocaleString('es-ES'), note: `${summary.adminUsers} administradores` },
    { label: 'Artículos', value: summary.totalItems.toLocaleString('es-ES'), note: `${summary.availableItems} disponibles` },
    { label: 'Pedidos', value: summary.totalOrders.toLocaleString('es-ES'), note: `${summary.ordersLast30Days} en 30 días` },
    { label: 'Ingresos', value: moneyFormatter.format(summary.totalRevenue), note: `${moneyFormatter.format(summary.revenueLast30Days)} en 30 días` },
    { label: 'Compradores activos', value: summary.activeBuyersLast30Days.toLocaleString('es-ES'), note: 'Últimos 30 días' },
    { label: 'Vendedores activos', value: summary.activeSellersLast30Days.toLocaleString('es-ES'), note: 'Últimos 30 días' }
  ]
})

const businessPanels = computed(() => {
  if (!metrics.value) {
    return []
  }

  const summary = metrics.value.summary

  return [
    {
      kicker: 'Negocio',
      title: 'Clientes',
      description: 'Usuarios reales registrados y actividad reciente de compradores.',
      stats: [
        { label: 'Usuarios', value: summary.totalUsers.toString() },
        { label: 'Compradores activos', value: summary.activeBuyersLast30Days.toString() }
      ]
    },
    {
      kicker: 'Negocio',
      title: 'Productos',
      description: 'Inventario publicado y artículos disponibles para venta.',
      stats: [
        { label: 'Publicados', value: summary.totalItems.toString() },
        { label: 'Disponibles', value: summary.availableItems.toString() }
      ]
    },
    {
      kicker: 'Negocio',
      title: 'Unidad de negocio',
      description: 'Ventas acumuladas y volumen comercial del sistema.',
      stats: [
        { label: 'Pedidos', value: summary.totalOrders.toString() },
        { label: 'Ingresos', value: moneyFormatter.format(summary.totalRevenue) }
      ]
    }
  ]
})

const technicalPanels = computed(() => {
  if (!metrics.value) {
    return []
  }

  return [
    {
      kicker: 'Estado técnico',
      title: 'Salud de la plataforma',
      description: 'Métricas operativas para controlar actividad, distribución y carga del sistema.',
      stats: [
        { label: 'Pedidos 30d', value: metrics.value.summary.ordersLast30Days.toString() },
        { label: 'Ingresos 30d', value: moneyFormatter.format(metrics.value.summary.revenueLast30Days) }
      ]
    },
    {
      kicker: 'Estado técnico',
      title: 'Roles y permisos',
      description: 'Distribución real de usuarios y administradores cargada desde la base de datos.',
      stats: [
        { label: 'Usuarios', value: metrics.value.summary.totalUsers.toString() },
        { label: 'Administradores', value: metrics.value.summary.adminUsers.toString() }
      ]
    }
  ]
})

const kpiGoals = [
  {
    id: 'kpi-growth',
    title: 'Crecimiento de ingresos',
    description: 'Aumentar los ingresos mensuales en un 20% durante los próximos 6 meses mediante promociones y optimización de las fichas de producto.'
  },
  {
    id: 'kpi-activation',
    title: 'Activación de compradores',
    description: 'Incrementar la tasa de compradores activos en 30 días en un 15% mediante mejoras en la experiencia y campañas segmentadas.'
  },
  {
    id: 'kpi-uptime',
    title: 'Disponibilidad técnica',
    description: 'Mantener la disponibilidad de la plataforma por encima del 99.9% y reducir tiempos de latencia promedio en picos de tráfico.'
  },
  {
    id: 'kpi-listings',
    title: 'Calidad de catálogo',
    description: 'Aumentar el porcentaje de artículos con fotos y descripciones completas al 90% en 3 meses.'
  }
]

const topCategoryColors = computed(() => {
  if (!metrics.value) {
    return ['#f59e0b', '#f97316', '#fb923c', '#facc15', '#f43f5e']
  }

  const n = Math.max(1, metrics.value.topCategories.labels.length)
  return Array.from({ length: n }).map((_, i) => `hsl(${Math.round((i * 360) / n)} 85% 50%)`)
})

const loadMetrics = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    loadSessionUser()
    const token = sessionUser.value?.token

    if (!token) {
      throw new Error('No se encontró una sesión válida de administrador.')
    }

    const response = await $fetch<AdminMetrics>(`${runtimePublic.API_BASE_URL}/admin/metrics?days=30`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })

    metrics.value = response
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : 'No se pudieron cargar las métricas.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  void loadMetrics()
})
</script>

<style scoped>
.admin-dashboard-page {
  padding: 24px;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
}

.admin-shell {
  max-width: 1240px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 280px minmax(0, 1fr);
  gap: 24px;
}

.admin-sidebar,
.admin-content__hero,
.admin-card,
.admin-summary-card,
.admin-state {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
}

.admin-sidebar {
  padding: 20px;
  position: sticky;
  top: 112px;
  height: fit-content;
}

.admin-sidebar__header {
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.admin-sidebar__eyebrow,
.admin-content__eyebrow,
.admin-card__eyebrow,
.admin-summary-card__label {
  margin: 0 0 6px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-size: 0.74rem;
  color: #6b7280;
}

.admin-sidebar__title,
.admin-content__title,
.admin-card__title {
  margin: 0;
  color: #0f172a;
}

.admin-sidebar__title {
  font-size: 1.4rem;
}

.admin-sidebar__nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.admin-sidebar__item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  width: 100%;
  text-align: left;
  border: 1px solid #e5e7eb;
  background: #fff;
  border-radius: 18px;
  padding: 14px 16px;
  cursor: pointer;
  transition: border-color 0.18s ease, background-color 0.18s ease, transform 0.18s ease;
}

.admin-sidebar__item:hover,
.admin-sidebar__item--active {
  border-color: #a7f3d0;
  background: #ecfdf5;
  transform: translateY(-1px);
}

.admin-sidebar__item-label {
  font-size: 0.98rem;
  font-weight: 700;
  color: #0f172a;
}

.admin-sidebar__item-meta {
  font-size: 0.86rem;
  color: #64748b;
}

.admin-sidebar__back {
  display: inline-flex;
  margin-top: 18px;
  width: 100%;
  justify-content: center;
  align-items: center;
  min-height: 44px;
  border-radius: 999px;
  border: 1px solid #d1d5db;
  color: #475569;
  text-decoration: none;
  font-weight: 600;
  background: #fff;
}

.admin-content {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.admin-content__hero {
  padding: 24px;
}

.admin-content__title {
  font-size: clamp(1.5rem, 2vw, 2.1rem);
  margin-bottom: 8px;
}

.admin-content__subtitle {
  margin: 0;
  max-width: 720px;
  color: #475569;
}

.admin-state {
  padding: 20px;
  color: #475569;
}

.admin-state--error {
  color: #b91c1c;
  background: #fef2f2;
  border-color: #fecaca;
}

.admin-summary-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
  gap: 12px;
}

.admin-summary-card {
  padding: 12px;
}

.admin-summary-card__value {
  display: block;
  margin-bottom: 6px;
  font-size: 1rem;
  color: #0f172a;
}

.admin-summary-card__note {
  color: #64748b;
  font-size: 0.78rem;
}

.admin-grid,
.admin-charts-grid {
  display: grid;
  gap: 20px;
}

.admin-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.admin-charts-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr));
  align-items: start;
  gap: 12px;
}

.admin-card--wide {
  grid-column: 1 / -1;
}

.admin-card {
  padding: 22px;
}

.admin-card__title {
  font-size: 1.12rem;
  margin-bottom: 10px;
}

.admin-card__text {
  margin: 0;
  color: #475569;
  line-height: 1.65;
}

.admin-card__stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(110px, 1fr));
  gap: 12px;
  margin-top: 18px;
}

.admin-stat {
  padding: 14px;
  border-radius: 18px;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  border: 1px solid #e5e7eb;
}

.admin-stat__value {
  display: block;
  font-size: 1.2rem;
  font-weight: 800;
  color: #0f172a;
}

.admin-stat__label {
  display: block;
  margin-top: 4px;
  font-size: 0.85rem;
  color: #64748b;
}

.kpis-accordion {
  display: grid;
  gap: 12px;
}

.kpi-item {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  background: #fff;
}

.kpi-summary {
  cursor: pointer;
  font-weight: 700;
  color: #0f172a;
}

.kpi-body {
  margin-top: 8px;
  color: #475569;
  line-height: 1.6;
}

@media (max-width: 1100px) {
  .admin-summary-grid,
  .admin-charts-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-summary-grid,
  .admin-charts-grid,
  .admin-grid {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .admin-shell {
    padding: 0 12px;
    gap: 12px;
  }

  .admin-content__hero {
    padding: 16px;
  }

  .admin-summary-card__value {
    font-size: 1.1rem;
  }

  .kpi-summary {
    font-size: 1rem;
  }
}

@media (max-width: 1024px) {
  .admin-shell {
    grid-template-columns: 1fr;
  }

  .admin-sidebar {
    position: static;
  }

  .admin-grid,
  .admin-charts-grid,
  .admin-summary-grid {
    grid-template-columns: 1fr;
  }

  .admin-card--wide {
    grid-column: auto;
  }
}

@media (max-width: 640px) {
  .admin-dashboard-page {
    padding: 16px;
  }

  .admin-content__hero,
  .admin-card,
  .admin-sidebar,
  .admin-summary-card,
  .admin-state {
    border-radius: 20px;
  }

  .admin-content__hero,
  .admin-card,
  .admin-sidebar {
    padding: 18px;
  }
}
</style>