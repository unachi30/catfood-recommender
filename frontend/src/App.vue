<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useIdleLogout } from '@/composables/useIdleLogout'

const auth = useAuthStore()
const router = useRouter()

useIdleLogout()

function logout() {
  auth.logout()
  void router.replace('/cans')
}
</script>

<template>
  <div>
    <nav>
      <router-link to="/home">🏠 首頁</router-link>
      <router-link to="/cans">🐱 貓罐頭清單</router-link>
      <template v-if="auth.isLoggedIn">
        <router-link v-if="auth.isAdmin" to="/admin/cans">🛠️ 管理商品</router-link>
        <router-link v-if="auth.isAdmin" to="/admin/cans/import">📦 新增商品</router-link>
        <span class="user-info">
          {{ auth.userName }}
          <span class="role-badge">{{ auth.isAdmin ? '管理員' : '一般會員' }}</span>
        </span>
        <button type="button" class="logout" @click="logout">登出</button>
      </template>
      <template v-else>
        <span class="guest-hint">訪客模式 · 登入後可使用個人功能</span>
        <router-link to="/login">登入</router-link>
        <router-link to="/register">註冊</router-link>
      </template>
    </nav>
    <router-view />
  </div>
</template>

<style>
nav {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
nav a {
  text-decoration: none;
  color: #42b983;
}
nav a.router-link-exact-active {
  font-weight: bold;
}
.user-info {
  margin-left: auto;
  color: #555;
}
.guest-hint {
  margin-left: auto;
  color: #888;
  font-size: 0.9rem;
}
.role-badge {
  margin-left: 6px;
  padding: 2px 8px;
  background: #e8f5e9;
  color: #2e7d32;
  border-radius: 4px;
  font-size: 0.85rem;
}
.logout {
  padding: 4px 10px;
  border: 1px solid #ccc;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}
</style>
