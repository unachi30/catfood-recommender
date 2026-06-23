import type { LocationQueryValue, RouteLocationRaw } from 'vue-router'

export function resolvePostLoginRedirect(
  redirect: LocationQueryValue | LocationQueryValue[] | undefined,
): RouteLocationRaw {
  const target = Array.isArray(redirect) ? redirect[0] : redirect
  if (typeof target === 'string' && target.startsWith('/')) {
    return target
  }
  return '/cans'
}
