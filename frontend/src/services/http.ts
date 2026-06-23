import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'
import { useAuthStore } from '@/stores/auth'

export type AuthHttpConfig = InternalAxiosRequestConfig & {
  authRequired?: boolean
}

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  headers: { 'Content-Type': 'application/json' },
})

http.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status !== 401) {
      return Promise.reject(error)
    }

    const auth = useAuthStore()
    const requestConfig = error.config as AuthHttpConfig | undefined
    const hadToken = !!requestConfig?.headers?.Authorization

    if (hadToken) {
      auth.logout()
    }

    const shouldRedirect =
      requestConfig?.authRequired === true ||
      (await import('@/router/router')).default.currentRoute.value.meta.requiresAuth === true

    if (shouldRedirect) {
      const { default: router } = await import('@/router/router')
      const path = router.currentRoute.value.path
      if (path !== '/login' && path !== '/register') {
        await router.replace({
          path: '/login',
          query: { redirect: router.currentRoute.value.fullPath },
        })
      }
    }

    return Promise.reject(error)
  },
)
