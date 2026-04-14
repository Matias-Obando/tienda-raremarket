<template>
  <div class="search">
    <input
      :value="modelValue"
      class="input"
      type="search"
      placeholder="Busca artículos"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
    />
    <button v-if="modelValue" class="clear" type="button" @click="$emit('update:modelValue', '')">
      Limpiar
    </button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ modelValue: string }>()
defineEmits<{ (e: 'update:modelValue', v: string): void }>()
</script>

<style scoped>
.search {
  display: flex;
  gap: 10px;
  align-items: center;
}

.input {
  flex: 1;
  border: 1px solid var(--rm-border);
  border-radius: 999px;
  padding: 10px 14px;
  background: #fff;
  color: var(--rm-text);
  outline: none;
}

.input:focus {
  border-color: rgba(0, 0, 0, 0.35);
  box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.06);
}

.clear {
  border: 1px solid var(--rm-border);
  background: #fff;
  color: var(--rm-text);
  padding: 10px 12px;
  border-radius: 999px;
  cursor: pointer;
}

.clear:hover {
  background: var(--rm-soft);
}
</style>