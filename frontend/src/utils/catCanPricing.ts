import type { CatCan } from '@/types/catCan'
import { normalizeBrandOrName } from '@/utils/catCanText'

export function getUnitPrice(can: CatCan): number {
  if (can.unitPrice != null) {
    return can.unitPrice
  }
  if (can.totalCount != null && can.totalCount > 0) {
    return Math.round((can.price / can.totalCount) * 100) / 100
  }
  return can.price
}

export function formatMoney(value: number): string {
  return `$${value}`
}

export function formatUnitPrice(value: number, suffix = ''): string {
  return `$${value}${suffix}`
}

export function formatPackLabel(can: CatCan): string {
  const count = can.totalCount ?? 1
  return `${count} 罐`
}

export function formatMinPurchaseQuantity(can: CatCan): string {
  const count = can.totalCount ?? 1
  return `${count} 罐`
}

export interface FlavorOption extends CatCan {
  unitPriceValue: number
  isBestUnitPrice: boolean
}

export interface FlavorGroup {
  key: string
  title: string
  options: FlavorOption[]
}

export interface ProductGroup {
  key: string
  category: string
  brand: string
  name: string
  capacityLabel: string
  optionCount: number
  flavorGroups: FlavorGroup[]
}

function uniqueValues(values: string[]): string[] {
  return [...new Set(values.filter(Boolean))]
}

function normalizeCapacity(value: string): string {
  return value.trim().toLowerCase()
}

function formatFlavorTitle(flavor: string, capacity: string): string {
  if (flavor === '—') {
    return capacity || '—'
  }
  return `${flavor}${capacity}`
}

export function buildProductGroups(items: CatCan[]): ProductGroup[] {
  const families = new Map<string, CatCan[]>()

  for (const item of items) {
    const key = [
      item.category,
      normalizeBrandOrName(item.brand),
      normalizeBrandOrName(item.name),
    ].join('|')
    if (!families.has(key)) {
      families.set(key, [])
    }
    families.get(key)!.push(item)
  }

  return [...families.entries()].map(([key, familyItems]) => {
    const capacities = uniqueValues(familyItems.map((item) => item.capacity))

    const flavorMap = new Map<string, CatCan[]>()
    for (const item of familyItems) {
      const flavor = item.flavor?.trim() || '—'
      const capacity = normalizeCapacity(item.capacity)
      const flavorKey = `${flavor}|${capacity}`
      if (!flavorMap.has(flavorKey)) {
        flavorMap.set(flavorKey, [])
      }
      flavorMap.get(flavorKey)!.push(item)
    }

    const flavorGroups: FlavorGroup[] = [...flavorMap.entries()].map(([flavorKey, options]) => {
      const [flavor = '—', capacity = ''] = flavorKey.split('|')
      const unitPrices = options.map((option) => getUnitPrice(option))
      const minUnitPrice = Math.min(...unitPrices)
      const hasMultipleOptions = options.length > 1

      const enriched = options
        .map((option) => {
          const unitPriceValue = getUnitPrice(option)
          return {
            ...option,
            unitPriceValue,
            isBestUnitPrice: hasMultipleOptions && unitPriceValue === minUnitPrice,
          }
        })
        .sort((a, b) => a.unitPriceValue - b.unitPriceValue)

      return {
        key: flavorKey,
        title: formatFlavorTitle(flavor, capacity),
        options: enriched,
      }
    })

    flavorGroups.sort((a, b) => a.title.localeCompare(b.title, 'zh-Hant'))

    const first = familyItems[0]!

    return {
      key,
      category: first.category,
      brand: normalizeBrandOrName(first.brand),
      name: normalizeBrandOrName(first.name),
      capacityLabel: capacities.length === 1 ? (capacities[0] ?? '—') : '多規格',
      optionCount: familyItems.length,
      flavorGroups,
    }
  })
}
