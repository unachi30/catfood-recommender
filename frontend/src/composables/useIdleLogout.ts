import { onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { IDLE_TIMEOUT_MS, LAST_ACTIVITY_KEY } from '@/constants/session'

const ACTIVITY_THROTTLE_MS = 10_000
const ACTIVITY_EVENTS = ['mousedown', 'keydown', 'scroll', 'touchstart', 'click'] as const

export function useIdleLogout(idleMs: number = IDLE_TIMEOUT_MS) {
  const auth = useAuthStore()
  const router = useRouter()

  let timer: ReturnType<typeof setTimeout> | null = null
  let lastTouchAt = 0
  let listenersBound = false

  function clearTimer() {
    if (timer !== null) {
      clearTimeout(timer)
      timer = null
    }
  }

  function touchActivity() {
    const now = Date.now()
    localStorage.setItem(LAST_ACTIVITY_KEY, String(now))
    lastTouchAt = now
    scheduleLogout()
  }

  function isIdleExpired(): boolean {
    const raw = localStorage.getItem(LAST_ACTIVITY_KEY)
    if (!raw) {
      return false
    }
    return Date.now() - Number(raw) > idleMs
  }

  function logoutForIdle() {
    if (!auth.isLoggedIn) {
      return
    }
    auth.logout()
    localStorage.removeItem(LAST_ACTIVITY_KEY)
    clearTimer()
    void router.replace({ path: '/login', query: { reason: 'idle' } })
  }

  function scheduleLogout() {
    clearTimer()
    if (!auth.isLoggedIn) {
      return
    }
    const raw = localStorage.getItem(LAST_ACTIVITY_KEY)
    const lastActivity = raw ? Number(raw) : Date.now()
    const remaining = idleMs - (Date.now() - lastActivity)
    timer = setTimeout(logoutForIdle, Math.max(remaining, 0))
  }

  function onActivity() {
    if (!auth.isLoggedIn) {
      return
    }
    const now = Date.now()
    if (now - lastTouchAt < ACTIVITY_THROTTLE_MS) {
      return
    }
    touchActivity()
  }

  function addListeners() {
    if (listenersBound) {
      return
    }
    ACTIVITY_EVENTS.forEach((event) => {
      window.addEventListener(event, onActivity, { passive: true })
    })
    listenersBound = true
  }

  function removeListeners() {
    if (!listenersBound) {
      return
    }
    ACTIVITY_EVENTS.forEach((event) => {
      window.removeEventListener(event, onActivity)
    })
    listenersBound = false
  }

  function startIdleWatch() {
    if (!auth.isLoggedIn) {
      removeListeners()
      clearTimer()
      return
    }

    if (isIdleExpired()) {
      logoutForIdle()
      return
    }

    if (!localStorage.getItem(LAST_ACTIVITY_KEY)) {
      touchActivity()
    } else {
      scheduleLogout()
    }
    addListeners()
  }

  const stopRouteWatch = router.afterEach(() => {
    if (auth.isLoggedIn) {
      onActivity()
    }
  })

  const stopAuthWatch = watch(
    () => auth.isLoggedIn,
    (loggedIn) => {
      if (!loggedIn) {
        removeListeners()
        clearTimer()
        localStorage.removeItem(LAST_ACTIVITY_KEY)
        return
      }
      startIdleWatch()
    },
    { immediate: true },
  )

  onUnmounted(() => {
    stopAuthWatch()
    stopRouteWatch()
    removeListeners()
    clearTimer()
  })
}
