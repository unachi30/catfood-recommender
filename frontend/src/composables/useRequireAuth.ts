import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

export function useRequireAuth() {
  const auth = useAuthStore()
  const router = useRouter()
  const route = useRoute()

  function requireAuth(onAuthed: () => void) {
    if (auth.isLoggedIn) {
      onAuthed()
      return
    }

    void router.push({
      path: '/login',
      query: { redirect: route.fullPath },
    })
  }

  return { requireAuth, isLoggedIn: auth.isLoggedIn }
}
