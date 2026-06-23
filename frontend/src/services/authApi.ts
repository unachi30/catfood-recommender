import { http } from '@/services/http'
import type { ApiResponse } from '@/types/api'
import { unwrapApiData } from '@/types/api'
import type { UserRole } from '@/types/userRole'

export interface LoginResult {
  token: string
  username: string
  displayName: string
  role: UserRole
}

export async function login(account: string, password: string): Promise<LoginResult> {
  const { data } = await http.post<ApiResponse<LoginResult>>('/auth/login', {
    username: account.trim(),
    password: password.trim(),
  })
  return unwrapApiData(data)
}
