/** Removes all whitespace from brand or name so duplicates don't split in the UI. */
export function normalizeBrandOrName(value: string): string {
  return value.replace(/\s/g, '')
}
