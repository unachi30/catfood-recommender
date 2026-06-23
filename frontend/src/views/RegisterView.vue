<script setup lang="ts">
import { reactive, ref } from 'vue'
import { isAxiosError } from 'axios'
import { registerUser, type UserRegistrationPayload } from '@/services/userApi'

const form = reactive<UserRegistrationPayload>({
  username: '',
  password: '',
  identityCard: '',
  realName: '',
  phone: '',
  email: '',
})

const loading = ref(false)
const resultMsg = ref('')
const errorMsg = ref('')

async function onSubmit() {
  resultMsg.value = ''
  errorMsg.value = ''
  loading.value = true
  try {
    const body: UserRegistrationPayload = {
      username: form.username.trim(),
      password: form.password.trim(),
      identityCard: form.identityCard.trim().toUpperCase(),
    }
    const optName = form.realName?.trim()
    const optPhone = form.phone?.trim()
    const optEmail = form.email?.trim()
    if (optName) body.realName = optName
    if (optPhone) body.phone = optPhone
    if (optEmail) body.email = optEmail

    const text = await registerUser(body)
    resultMsg.value = text || '註冊成功'
  } catch (e: unknown) {
    if (isAxiosError(e)) {
      const d = e.response?.data as unknown
      if (d && typeof d === 'object' && 'message' in d) {
        errorMsg.value = String((d as { message: unknown }).message)
      } else {
        errorMsg.value = e.message || `HTTP ${e.response?.status ?? '?'}`
      }
    } else {
      errorMsg.value = e instanceof Error ? e.message : String(e)
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <h1>註冊</h1>
    <p class="hint">新帳號角色固定為一般會員（USER）</p>

    <p v-if="resultMsg" class="msg success">{{ resultMsg }}</p>
    <p v-if="errorMsg" class="msg error">{{ errorMsg }}</p>

    <form class="auth-form" @submit.prevent="onSubmit">
      <label>帳號 *<input v-model="form.username" required /></label>
      <label>密碼 *<input v-model="form.password" type="password" required /></label>
      <label>身分證字號 *<input v-model="form.identityCard" required placeholder="A123456789" /></label>
      <label>姓名<input v-model="form.realName" /></label>
      <label>手機<input v-model="form.phone" placeholder="0912345678" /></label>
      <label>Email<input v-model="form.email" type="email" /></label>
      <button type="submit" :disabled="loading">{{ loading ? '送出中…' : '註冊' }}</button>
    </form>

    <router-link to="/login">已有帳號？登入</router-link>
  </div>
</template>

<style scoped>
.auth-page {
  max-width: 420px;
  margin: 2rem auto;
  padding: 0 1rem;
}
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
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
  color: #666;
  font-size: 0.9rem;
}
.msg {
  padding: 0.5rem;
  border-radius: 4px;
}
.success {
  background: #e8f5e9;
  color: #2e7d32;
}
.error {
  background: #ffebee;
  color: #c62828;
}
</style>
