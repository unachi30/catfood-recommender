import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserRole } from '@/types/userRole'
import { LAST_ACTIVITY_KEY } from '@/constants/session'

const ACCESS_TOKEN_KEY = 'access_token'
const USER_NAME_KEY = 'userName'
const USER_ROLE_KEY = 'userRole'

function readInitialAccessToken(): string {
  return localStorage.getItem(ACCESS_TOKEN_KEY) ?? ''
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(readInitialAccessToken())
  const userName = ref(localStorage.getItem(USER_NAME_KEY) ?? '')
  const role = ref<UserRole | ''>((localStorage.getItem(USER_ROLE_KEY) as UserRole) ?? '')

  const isLoggedIn = computed(() => !!accessToken.value)
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isUser = computed(() => role.value === 'USER')

  function setSession(token: string, name: string, userRole: UserRole) {
    accessToken.value = token
    userName.value = name
    role.value = userRole
    localStorage.setItem(ACCESS_TOKEN_KEY, token)
    localStorage.setItem(USER_NAME_KEY, name)
    localStorage.setItem(USER_ROLE_KEY, userRole)
    localStorage.setItem(LAST_ACTIVITY_KEY, String(Date.now()))
  }

  function logout() {
    accessToken.value = ''
    userName.value = ''
    role.value = ''
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(USER_NAME_KEY)
    localStorage.removeItem(USER_ROLE_KEY)
    localStorage.removeItem(LAST_ACTIVITY_KEY)
  }

  return {
    accessToken,
    userName,
    role,
    isLoggedIn,
    isAdmin,
    isUser,
    setSession,
    logout,
  }
})
