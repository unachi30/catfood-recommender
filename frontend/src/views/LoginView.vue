<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthForm } from '@/composables/useAuthForm'

const { form, submit } = useAuthForm()
const route = useRoute()

const idleNotice = computed(() => route.query.reason === 'idle')
</script>

<template>
  <div class="auth-page">
    <h1>登入</h1>
    <p v-if="idleNotice" class="idle-notice">閒置超過 30 分鐘，請重新登入。</p>
    <form class="auth-form" @submit.prevent="submit">
      <label>
        帳號
        <input v-model="form.account" type="text" required autocomplete="username" />
      </label>
      <label>
        密碼
        <input v-model="form.password" type="password" required autocomplete="current-password" />
      </label>
      <button type="submit">登入</button>
    </form>
    <p class="hint">
      測試帳號：<code>admin / admin123</code>（管理員）、<code>member01 / member123</code>（一般會員）
    </p>
    <router-link to="/register">還沒有帳號？註冊</router-link>
  </div>
</template>

<style scoped>
.auth-page {
  max-width: 400px;
  margin: 2rem auto;
  padding: 0 1rem;
}
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
button {
  padding: 0.6rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.hint {
  margin-top: 1rem;
  font-size: 0.9rem;
  color: #666;
}
.idle-notice {
  margin: 0 0 1rem;
  padding: 0.6rem 0.75rem;
  background: #fff8e1;
  color: #8d6e00;
  border-radius: 4px;
  font-size: 0.9rem;
}
</style>
