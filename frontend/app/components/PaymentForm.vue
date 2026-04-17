<template>
  <form @submit.prevent="submit" class="payment-form">
    <div class="pay-summary">
      <div class="pay-label">Resumen del pago</div>
      <div class="pay-row"><span class="pay-key">Producto:</span> <span class="pay-value">{{ itemId ? '#' + itemId : '-' }}</span></div>
      <div class="pay-row"><span class="pay-key">Cantidad:</span> <span class="pay-value">{{ form.amount }} €</span></div>
    </div>
    <div class="pay-methods">
      <label>Método de pago</label>
      <div class="pay-methods-list">
        <label class="pay-radio">
          <input type="radio" value="tarjeta" v-model="form.method" required />
          <span class="icon-card">💳</span> Tarjeta
        </label>
        <label class="pay-radio">
          <input type="radio" value="paypal" v-model="form.method" />
          <span class="icon-paypal">🅿️</span> PayPal
        </label>
        <label class="pay-radio">
          <input type="radio" value="stripe" v-model="form.method" />
          <span class="icon-stripe">💠</span> Stripe
        </label>
      </div>
    </div>
    <button type="submit" class="pay-btn">Pagar ahora</button>
    <div v-if="success" class="pay-success">Pago registrado correctamente</div>
    <div v-if="error" class="pay-error">Error: {{ error }}</div>
  </form>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ amount?: number; itemId?: string }>()

const form = ref({ method: 'tarjeta', amount: props.amount ?? 0, itemId: props.itemId ?? '' })
const success = ref(false)
const error = ref('')

// Si cambian los props, actualiza el formulario
watch(() => props.amount, (val) => { if (val !== undefined) form.value.amount = val })
watch(() => props.itemId, (val) => { if (val !== undefined) form.value.itemId = val })

async function submit() {
  success.value = false
  error.value = ''
  try {
    await $fetch('/api/payments', { method: 'POST', body: form.value })
    success.value = true
    // Limpiar método de pago tras éxito
    form.value.method = 'tarjeta'
    // No limpiar amount ni itemId, para mantener el resumen
  } catch (e: any) {
    error.value = e?.data?.message || e.message || 'Error desconocido'
  }
}

const itemId = props.itemId ?? ''
</script>

<style scoped>
.payment-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 18px 20px 22px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 24px rgba(0,0,0,0.07);
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.pay-summary {
  background: #f8fafc;
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 8px;
}
.pay-label {
  font-weight: 700;
  margin-bottom: 6px;
  color: #374151;
}
.pay-row {
  display: flex;
  justify-content: space-between;
  font-size: 15px;
  margin-bottom: 2px;
}
.pay-key { color: #64748b; }
.pay-value { color: #111827; font-weight: 600; }
.pay-methods label {
  font-weight: 600;
  color: #374151;
}
.pay-methods-list {
  display: flex;
  gap: 18px;
  margin-top: 6px;
}
.pay-radio {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  cursor: pointer;
}
.icon-card, .icon-paypal, .icon-stripe {
  font-size: 20px;
}
.pay-btn {
  margin-top: 10px;
  background: #16a34a;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 0;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: background .15s;
}
.pay-btn:hover {
  background: #15803d;
}
.pay-success {
  color: #16a34a;
  font-weight: 600;
  margin-top: 8px;
}
.pay-error {
  color: #dc2626;
  font-weight: 600;
  margin-top: 8px;
}
</style>
