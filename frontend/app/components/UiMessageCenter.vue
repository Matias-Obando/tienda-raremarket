<template>
  <Teleport to="body">
    <section class="toast-stack" aria-live="polite" aria-atomic="true">
      <TransitionGroup name="toast">
        <article
          v-for="message in messages"
          :key="message.id"
          class="toast"
          :class="`toast--${message.kind}`"
        >
          <p class="toast__text">{{ message.text }}</p>
          <button class="toast__close" type="button" @click="remove(message.id)" aria-label="Cerrar mensaje">
            x
          </button>
        </article>
      </TransitionGroup>
    </section>
  </Teleport>
</template>

<script setup lang="ts">
const { messages, remove } = useUiMessages()
</script>

<style scoped>
.toast-stack {
  position: fixed;
  top: 22px;
  right: 18px;
  z-index: 1200;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: min(92vw, 420px);
}

.toast {
  display: grid;
  grid-template-columns: 1fr auto;
  align-items: start;
  gap: 10px;
  border-radius: 14px;
  padding: 12px 14px;
  border: 1px solid transparent;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.18);
  background: #ffffff;
}

.toast--success {
  border-color: #86efac;
  background: #f0fdf4;
}

.toast--error {
  border-color: #fca5a5;
  background: #fff1f2;
}

.toast--info {
  border-color: #99f6e4;
  background: #ecfeff;
}

.toast__text {
  margin: 0;
  font-size: 14px;
  line-height: 1.35;
  color: #0f172a;
}

.toast__close {
  border: 0;
  background: transparent;
  color: #334155;
  cursor: pointer;
  font-size: 13px;
  line-height: 1;
  font-weight: 700;
  width: 22px;
  height: 22px;
  border-radius: 999px;
}

.toast__close:hover {
  background: rgba(15, 23, 42, 0.08);
}

.toast-enter-active,
.toast-leave-active {
  transition: all .2s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

@media (max-width: 640px) {
  .toast-stack {
    left: 12px;
    right: 12px;
    top: 12px;
    max-width: none;
  }
}
</style>
