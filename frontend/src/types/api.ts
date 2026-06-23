export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export function unwrapApiData<T>(body: ApiResponse<T>): T {
  if (body.code !== 200) {
    throw new Error(body.message || 'API 回應失敗')
  }
  return body.data
}
