import { computed, reactive, ref } from 'vue'
import type { CatCan } from '@/types/catCan'
import { getUnitPrice } from '@/utils/catCanPricing'

export type CatCanSortKey =
  | 'category'
  | 'brand'
  | 'name'
  | 'capacity'
  | 'price'
  | 'unitPrice'
  | 'shopName'
  | 'rating'

export type SortDirection = 'asc' | 'desc'

export interface CatCanFilters {
  categories: string[]
  brands: string[]
  capacities: string[]
  shops: string[]
  ratings: number[]
  priceMin: number | null
  priceMax: number | null
}

function uniqueSorted(values: string[]): string[] {
  return [...new Set(values.filter(Boolean))].sort((a, b) => a.localeCompare(b, 'zh-Hant'))
}

function compareByFirstChar(a: string, b: string, direction: SortDirection): number {
  const result = a.localeCompare(b, 'zh-Hant')
  return direction === 'asc' ? result : -result
}

function getSortValue(can: CatCan, key: CatCanSortKey): string | number {
  switch (key) {
    case 'category':
      return can.category ?? ''
    case 'brand':
      return can.brand ?? ''
    case 'name':
      return can.name ?? ''
    case 'capacity':
      return can.capacity ?? ''
    case 'shopName':
      return can.shopName ?? ''
    case 'price':
      return can.price ?? 0
    case 'unitPrice':
      return getUnitPrice(can)
    case 'rating':
      return can.rating ?? 0
  }
}

export function useCatCanTable(source: { value: CatCan[] }) {
  const filters = reactive<CatCanFilters>({
    categories: [],
    brands: [],
    capacities: [],
    shops: [],
    ratings: [],
    priceMin: null,
    priceMax: null,
  })

  const sortKey = ref<CatCanSortKey | null>(null)
  const sortDirection = ref<SortDirection>('asc')

  const filterOptions = computed(() => {
    const items = source.value
    return {
      categories: uniqueSorted(items.map((c) => c.category)),
      brands: uniqueSorted(items.map((c) => c.brand)),
      capacities: uniqueSorted(items.map((c) => c.capacity)),
      shops: uniqueSorted(items.map((c) => c.shopName)),
      ratings: [...new Set(items.map((c) => c.rating).filter((r): r is number => r != null))].sort(
        (a, b) => a - b,
      ),
      priceMin: items.length ? Math.min(...items.map((c) => c.price)) : 0,
      priceMax: items.length ? Math.max(...items.map((c) => c.price)) : 0,
    }
  })

  const filteredCans = computed(() => {
    let items = source.value

    if (filters.categories.length > 0) {
      items = items.filter((c) => filters.categories.includes(c.category))
    }
    if (filters.brands.length > 0) {
      items = items.filter((c) => filters.brands.includes(c.brand))
    }
    if (filters.capacities.length > 0) {
      items = items.filter((c) => filters.capacities.includes(c.capacity))
    }
    if (filters.shops.length > 0) {
      items = items.filter((c) => filters.shops.includes(c.shopName))
    }
    if (filters.ratings.length > 0) {
      items = items.filter((c) => c.rating != null && filters.ratings.includes(c.rating))
    }
    if (filters.priceMin != null) {
      items = items.filter((c) => c.price >= filters.priceMin!)
    }
    if (filters.priceMax != null) {
      items = items.filter((c) => c.price <= filters.priceMax!)
    }

    if (sortKey.value) {
      const key = sortKey.value
      const direction = sortDirection.value
      items = [...items].sort((a, b) => {
        const aVal = getSortValue(a, key)
        const bVal = getSortValue(b, key)
        if (typeof aVal === 'number' && typeof bVal === 'number') {
          return direction === 'asc' ? aVal - bVal : bVal - aVal
        }
        return compareByFirstChar(String(aVal), String(bVal), direction)
      })
    }

    return items
  })

  function toggleFilter<K extends keyof Pick<CatCanFilters, 'categories' | 'brands' | 'capacities' | 'shops'>>(
    field: K,
    value: string,
  ) {
    const list = filters[field] as string[]
    const index = list.indexOf(value)
    if (index >= 0) {
      list.splice(index, 1)
    } else {
      list.push(value)
    }
  }

  function toggleRating(value: number) {
    const index = filters.ratings.indexOf(value)
    if (index >= 0) {
      filters.ratings.splice(index, 1)
    } else {
      filters.ratings.push(value)
    }
  }

  function isChecked(field: 'categories' | 'brands' | 'capacities' | 'shops', value: string): boolean {
    return filters[field].includes(value)
  }

  function isRatingChecked(value: number): boolean {
    return filters.ratings.includes(value)
  }

  function setSort(key: CatCanSortKey, direction: SortDirection) {
    if (sortKey.value === key && sortDirection.value === direction) {
      sortKey.value = null
      return
    }
    sortKey.value = key
    sortDirection.value = direction
  }

  function isSortActive(key: CatCanSortKey, direction: SortDirection): boolean {
    return sortKey.value === key && sortDirection.value === direction
  }

  function clearFilters() {
    filters.categories = []
    filters.brands = []
    filters.capacities = []
    filters.shops = []
    filters.ratings = []
    filters.priceMin = null
    filters.priceMax = null
  }

  const hasActiveFilters = computed(
    () =>
      filters.categories.length > 0 ||
      filters.brands.length > 0 ||
      filters.capacities.length > 0 ||
      filters.shops.length > 0 ||
      filters.ratings.length > 0 ||
      filters.priceMin != null ||
      filters.priceMax != null,
  )

  return {
    filters,
    filterOptions,
    filteredCans,
    sortKey,
    sortDirection,
    toggleFilter,
    toggleRating,
    isChecked,
    isRatingChecked,
    setSort,
    isSortActive,
    clearFilters,
    hasActiveFilters,
  }
}
