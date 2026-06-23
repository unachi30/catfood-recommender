export interface CatCan {
  id: number
  category: string
  brand: string
  name: string
  flavor?: string
  capacity: string
  price: number
  totalCount?: number
  unitPrice?: number
  shopName: string
  shopUrl?: string
  inStock?: boolean
  rating?: number | null
  reviewCount?: number
  description?: string
}
