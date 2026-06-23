import { isAxiosError } from 'axios'

type ErrorBody = {
  message?: string
  error?: string
  detail?: string
  code?: number
}

export function extractApiError(error: unknown, fallback = '操作失敗，請稍後再試'): string {
  if (isAxiosError(error)) {
    const status = error.response?.status
    const body = error.response?.data

    if (typeof body === 'string' && body.trim()) {
      return body.trim()
    }

    if (body && typeof body === 'object') {
      const payload = body as ErrorBody
      if (payload.message) {
        return payload.message
      }
      if (payload.detail) {
        return payload.detail
      }
      if (payload.error) {
        return payload.error
      }
    }

    if (status === 401) {
      return '登入已過期，請重新登入'
    }
    if (status === 403) {
      return '需要管理員權限'
    }
    if (status === 404) {
      return '找不到 API，請確認後端已重新啟動'
    }
    if (status === 409) {
      return '商品資料重複，請檢查是否已新增過'
    }
    if (status === 400) {
      return '欄位驗證失敗，請檢查必填項目與價格'
    }
    if (!error.response) {
      return '無法連線到後端，請確認服務是否已啟動'
    }
  }

  return fallback
}
