import { http } from '@/services/http'
import type { ApiResponse } from '@/types/api'

export interface UserRegistrationPayload {
  username: string
  password: string
  identityCard: string
  realName?: string
  phone?: string
  email?: string
}

export async function registerUser(payload: UserRegistrationPayload): Promise<string> {
  const { data: body } = await http.post<ApiResponse<string>>('/users/register', payload)
  return body.data ?? body.message
}
