<template>
  <article class="metric-chart-card">
    <header class="metric-chart-card__header">
      <div>
        <p class="metric-chart-card__eyebrow">{{ eyebrow }}</p>
        <h3 class="metric-chart-card__title">{{ title }}</h3>
      </div>
      <p v-if="subtitle" class="metric-chart-card__subtitle">{{ subtitle }}</p>
    </header>

    <ClientOnly>
      <ApexChart v-if="hasData" :type="type" :height="height" :options="chartOptions" :series="chartSeries" />
      <div v-else class="metric-chart-card__empty">Sin datos para mostrar.</div>
    </ClientOnly>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ApexOptions } from 'apexcharts'

type ChartType = 'line' | 'bar' | 'donut'

const props = defineProps<{
  title: string
  eyebrow?: string
  subtitle?: string
  type: ChartType
  labels: string[]
  series: Array<{ name: string; data: number[] }> | number[]
  height?: number
  colors?: string[]
}>()

const hasData = computed(() => props.labels.length > 0 && props.series.length > 0)
const chartSeries = computed(() => props.series)

const chartOptions = computed<ApexOptions>(() => {
  const shared: ApexOptions = {
    chart: {
      toolbar: { show: false },
      zoom: { enabled: false },
      fontFamily: 'Inter, ui-sans-serif, system-ui, sans-serif'
    },
    dataLabels: { enabled: false },
    legend: {
      position: 'bottom',
      fontSize: '13px',
      markers: { width: 10, height: 10, radius: 999 }
    },
    colors: props.colors ?? ['#0f766e', '#2563eb', '#f59e0b', '#7c3aed'],
    tooltip: { theme: 'light' },
    grid: {
      borderColor: '#e5e7eb',
      strokeDashArray: 4
    }
  }

  if (props.type === 'donut') {
    return {
      ...shared,
      labels: props.labels,
      plotOptions: {
        pie: {
          donut: {
            size: '72%'
          }
        }
      },
      stroke: { width: 0 }
    }
  }

  return {
    ...shared,
    xaxis: {
      categories: props.labels,
      labels: {
        style: { colors: '#64748b' }
      }
    },
    yaxis: {
      labels: {
        style: { colors: '#64748b' }
      }
    },
    stroke: props.type === 'line' ? { curve: 'smooth', width: 3 } : { width: 0 },
    plotOptions: props.type === 'bar'
      ? {
          bar: {
            borderRadius: 10,
            columnWidth: '52%'
          }
        }
      : undefined
  }
})
</script>

<style scoped>
.metric-chart-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 24px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.05);
  padding: 20px;
}

.metric-chart-card__header {
  display: flex;
  gap: 12px;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.metric-chart-card__eyebrow {
  margin: 0 0 6px;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-size: 0.74rem;
  color: #6b7280;
}

.metric-chart-card__title {
  margin: 0;
  font-size: 1.05rem;
  color: #0f172a;
}

.metric-chart-card__subtitle {
  margin: 0;
  color: #64748b;
  font-size: 0.92rem;
  max-width: 240px;
  text-align: right;
}

.metric-chart-card__empty {
  min-height: 240px;
  display: grid;
  place-items: center;
  color: #64748b;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  border-radius: 18px;
  border: 1px dashed #cbd5e1;
}

@media (max-width: 640px) {
  .metric-chart-card__empty {
    min-height: 200px;
  }
}

@media (max-width: 640px) {
  .metric-chart-card {
    padding: 16px;
    border-radius: 20px;
  }

  .metric-chart-card__header {
    flex-direction: column;
  }

  .metric-chart-card__subtitle {
    text-align: left;
    max-width: none;
  }
}
</style>