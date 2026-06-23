export type ShopBrand = 'shopee' | 'momo' | 'coupang' | 'default'

export function detectShopBrand(shopName: string, shopUrl?: string): ShopBrand {
  const hint = `${shopName} ${shopUrl ?? ''}`.toLowerCase()
  if (hint.includes('shopee') || hint.includes('蝦皮')) {
    return 'shopee'
  }
  if (hint.includes('momo')) {
    return 'momo'
  }
  if (hint.includes('coupa') || hint.includes('coupang')) {
    return 'coupang'
  }
  return 'default'
}

export function shopBuyLinkClass(shopName: string, shopUrl?: string): string {
  const brand = detectShopBrand(shopName, shopUrl)
  if (brand === 'default') {
    return 'buy-link'
  }
  return `buy-link ${brand}`
}
