import { http, type AuthHttpConfig } from '@/services/http'
import type { ApiResponse } from '@/types/api'
import { unwrapApiData } from '@/types/api'
import type { CatCan } from '@/types/catCan'

export interface CatCanDraft {
  category: string
  brand: string
  name: string
  flavor?: string
  capacity: string
  price: number
  totalCount?: number
  unitPrice?: number
  shopName: string
  shopUrl: string
  rating?: number | null
  reviewCount?: number | null
  description?: string
  inStock?: boolean
  parseHints?: string[]
}

export type CatCanCreatePayload = Omit<CatCanDraft, 'parseHints' | 'flavor'>

export interface CatCanBulkCreatePayload extends CatCanCreatePayload {
  flavors: string[]
}

export interface CatCanIncoming extends CatCanCreatePayload {
  flavor: string
}

export interface CatCanFlavorConflict {
  flavor: string
  existing: CatCan
  incoming: CatCanIncoming
  duplicateCount: number
}

export interface CatCanBatchCheckResult {
  conflicts: CatCanFlavorConflict[]
  newFlavors: string[]
}

export interface CatCanConflictResolution {
  flavor: string
  useNew: boolean
}

export interface CatCanBatchApplyPayload extends CatCanBulkCreatePayload {
  resolutions: CatCanConflictResolution[]
}

export type CatCanUpdatePayload = CatCanCreatePayload & {
  flavor?: string
}

export const MAX_FLAVORS = 20

export async function parseAffiliateShare(shareText: string): Promise<CatCanDraft> {
  const { data: body } = await http.post<ApiResponse<CatCanDraft>>(
    '/admin/cans/parse',
    { shareText },
    { authRequired: true } as AuthHttpConfig,
  )
  return unwrapApiData(body)
}

export async function createCatCansBatch(payload: CatCanBulkCreatePayload): Promise<CatCan[]> {
  const { data: body } = await http.post<ApiResponse<CatCan[]>>('/admin/cans/batch', payload, {
    authRequired: true,
  } as AuthHttpConfig)
  return unwrapApiData(body)
}

export async function checkBatchConflicts(
  payload: CatCanBulkCreatePayload,
): Promise<CatCanBatchCheckResult> {
  const { data: body } = await http.post<ApiResponse<CatCanBatchCheckResult>>(
    '/admin/cans/batch/check-conflicts',
    payload,
    { authRequired: true } as AuthHttpConfig,
  )
  return unwrapApiData(body)
}

export async function applyCatCansBatch(payload: CatCanBatchApplyPayload): Promise<CatCan[]> {
  const { data: body } = await http.post<ApiResponse<CatCan[]>>('/admin/cans/batch/apply', payload, {
    authRequired: true,
  } as AuthHttpConfig)
  return unwrapApiData(body)
}

export async function fetchCatCanById(id: number): Promise<CatCan> {
  const { data: body } = await http.get<ApiResponse<CatCan>>(`/admin/cans/${id}`, {
    authRequired: true,
  } as AuthHttpConfig)
  return unwrapApiData(body)
}

export async function updateCatCan(id: number, payload: CatCanUpdatePayload): Promise<CatCan> {
  const { data: body } = await http.put<ApiResponse<CatCan>>(`/admin/cans/${id}`, payload, {
    authRequired: true,
  } as AuthHttpConfig)
  return unwrapApiData(body)
}
