import { http } from '@/services/http'
import type { ApiResponse } from '@/types/api'
import { unwrapApiData } from '@/types/api'
import type { CatCan } from '@/types/catCan'

export async function fetchCatCans(): Promise<CatCan[]> {
  const { data } = await http.get<ApiResponse<CatCan[]>>('/cans')
  return unwrapApiData(data)
}
