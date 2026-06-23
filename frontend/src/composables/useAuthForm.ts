import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/services/authApi'
import { resolvePostLoginRedirect } from '@/utils/loginRedirect'
import { useAuthStore } from '@/stores/auth'

const ACCOUNT_PATTERN = /^[A-Za-z0-9]+$/

export function useAuthForm() {
  const auth = useAuthStore()
  const router = useRouter()
  const route = useRoute()
  const form = reactive({
    account: '',
    password: '',
  })

  async function submit() {
    if (!ACCOUNT_PATTERN.test(form.account)) {
      alert('帳號只能包含英文字母和數字')
      return
    }
    if (!ACCOUNT_PATTERN.test(form.password)) {
      alert('密碼只能包含英文字母和數字')
      return
    }

    try {
      const res = await login(form.account, form.password)
      auth.setSession(res.token, res.displayName, res.role)
      void router.push(resolvePostLoginRedirect(route.query.redirect))
    } catch {
      alert('登入失敗，請檢查帳號密碼')
    }
  }

  return { form, submit }
}
