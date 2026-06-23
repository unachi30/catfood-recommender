<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { fetchCatCans } from '@/services/catCanApi'
import { useCatCanTable, type CatCanSortKey } from '@/composables/useCatCanTable'
import type { CatCan } from '@/types/catCan'
import {
  buildProductGroups,
  formatMoney,
  formatMinPurchaseQuantity,
  formatUnitPrice,
  getUnitPrice,
} from '@/utils/catCanPricing'
import { shopBuyLinkClass } from '@/utils/shopBrand'

const catCans = ref<CatCan[]>([])
const errorMsg = ref('')
const loading = ref(true)
const highlighted = ref(false)
const expandedKeys = ref<string[]>([])

const {
  filters,
  filterOptions,
  filteredCans,
  toggleFilter,
  toggleRating,
  isChecked,
  isRatingChecked,
  setSort,
  isSortActive,
  clearFilters,
  hasActiveFilters,
} = useCatCanTable(catCans)

const columns: { key: CatCanSortKey; label: string }[] = [
  { key: 'category', label: '種類' },
  { key: 'brand', label: '品牌' },
  { key: 'name', label: '名稱' },
  { key: 'capacity', label: '容量' },
]

onMounted(async () => {
  try {
    catCans.value = await fetchCatCans()
  } catch {
    errorMsg.value = '載入失敗，請確認後端已啟動'
  } finally {
    loading.value = false
  }
})

function shortName(name: string): string {
  if (name.length <= 10) {
    return name
  }
  return `${name.slice(0, 10)}…`
}

const productGroups = computed(() => buildProductGroups(filteredCans.value))

const activeFilterChips = computed(() => {
  const chips: string[] = []
  if (filters.categories.length) chips.push(`種類 ${filters.categories.length}`)
  if (filters.brands.length) chips.push(`品牌 ${filters.brands.length}`)
  if (filters.capacities.length) chips.push(`容量 ${filters.capacities.length}`)
  if (filters.shops.length) chips.push(`商城 ${filters.shops.length}`)
  if (filters.ratings.length) chips.push(`評分 ${filters.ratings.length}`)
  if (filters.priceMin != null || filters.priceMax != null) chips.push('價格區間')
  return chips
})

function isExpanded(key: string): boolean {
  return expandedKeys.value.includes(key)
}

function toggleExpand(key: string) {
  if (isExpanded(key)) {
    expandedKeys.value = expandedKeys.value.filter((item) => item !== key)
  } else {
    expandedKeys.value.push(key)
  }
}

let flashTimer: ReturnType<typeof setTimeout> | null = null
watch(
  () => filteredCans.value.length,
  () => {
    highlighted.value = true
    if (flashTimer) {
      clearTimeout(flashTimer)
    }
    flashTimer = setTimeout(() => {
      highlighted.value = false
    }, 450)
  },
)
</script>

<template>
  <div class="cat-cans-page">
    <h1>🐱 貓罐頭清單</h1>

    <p v-if="loading" class="hint">載入中…</p>
    <p v-else-if="errorMsg" class="error">{{ errorMsg }}</p>

    <template v-else>
      <section class="filters">
        <div class="filters-header">
          <h2>篩選條件</h2>
          <button v-if="hasActiveFilters" type="button" class="btn-clear" @click="clearFilters">
            清除篩選
          </button>
        </div>

        <div class="filter-grid">
          <fieldset>
            <legend>種類</legend>
            <label v-for="value in filterOptions.categories" :key="value" class="checkbox">
              <input
                type="checkbox"
                :checked="isChecked('categories', value)"
                @change="toggleFilter('categories', value)"
              />
              {{ value }}
            </label>
          </fieldset>

          <fieldset>
            <legend>品牌</legend>
            <label v-for="value in filterOptions.brands" :key="value" class="checkbox">
              <input
                type="checkbox"
                :checked="isChecked('brands', value)"
                @change="toggleFilter('brands', value)"
              />
              {{ value }}
            </label>
          </fieldset>

          <fieldset>
            <legend>容量</legend>
            <label v-for="value in filterOptions.capacities" :key="value" class="checkbox">
              <input
                type="checkbox"
                :checked="isChecked('capacities', value)"
                @change="toggleFilter('capacities', value)"
              />
              {{ value }}
            </label>
          </fieldset>

          <fieldset>
            <legend>商城</legend>
            <label v-for="value in filterOptions.shops" :key="value" class="checkbox">
              <input
                type="checkbox"
                :checked="isChecked('shops', value)"
                @change="toggleFilter('shops', value)"
              />
              {{ value }}
            </label>
          </fieldset>

          <fieldset>
            <legend>評分</legend>
            <label v-for="value in filterOptions.ratings" :key="value" class="checkbox">
              <input
                type="checkbox"
                :checked="isRatingChecked(value)"
                @change="toggleRating(value)"
              />
              {{ value }}
            </label>
          </fieldset>

          <fieldset class="price-range">
            <legend>價格區間</legend>
            <div class="price-inputs">
              <label>
                最低
                <input
                  v-model.number="filters.priceMin"
                  type="number"
                  :min="filterOptions.priceMin"
                  :max="filterOptions.priceMax"
                  :placeholder="String(filterOptions.priceMin)"
                />
              </label>
              <span class="range-sep">～</span>
              <label>
                最高
                <input
                  v-model.number="filters.priceMax"
                  type="number"
                  :min="filterOptions.priceMin"
                  :max="filterOptions.priceMax"
                  :placeholder="String(filterOptions.priceMax)"
                />
              </label>
            </div>
            <p class="price-hint">
              資料範圍 ${{ filterOptions.priceMin }} ～ ${{ filterOptions.priceMax }}
            </p>
          </fieldset>
        </div>
      </section>

      <div class="result-banner" :class="{ highlighted }">
        <p class="result-count">
          顯示 {{ productGroups.length }} 款（共 {{ filteredCans.length }} 個購買方案） / {{ catCans.length }} 筆
        </p>
        <div v-if="activeFilterChips.length" class="chips">
          <span v-for="chip in activeFilterChips" :key="chip" class="chip">{{ chip }}</span>
        </div>
      </div>

      <p class="compare-tip">
        展開後可比較各口味在不同商城的價格；同一口味有多筆方案時，會標示「單罐最划算」。
      </p>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th v-for="col in columns" :key="col.key">
                <div class="th-content">
                  <span>{{ col.label }}</span>
                  <span class="sort-buttons">
                    <button
                      type="button"
                      class="sort-btn"
                      :class="{ active: isSortActive(col.key, 'asc') }"
                      title="升序（首字）"
                      @click="setSort(col.key, 'asc')"
                    >
                      ↑
                    </button>
                    <button
                      type="button"
                      class="sort-btn"
                      :class="{ active: isSortActive(col.key, 'desc') }"
                      title="降序（首字）"
                      @click="setSort(col.key, 'desc')"
                    >
                      ↓
                    </button>
                  </span>
                </div>
              </th>
              <th class="compare-col">展開比價</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="productGroups.length === 0">
              <td :colspan="columns.length + 1" class="empty">沒有符合篩選條件的商品</td>
            </tr>

            <template v-for="group in productGroups" :key="group.key">
              <tr>
                <td>{{ group.category }}</td>
                <td>{{ group.brand }}</td>
                <td class="name-cell">
                  <span :title="group.name">{{ shortName(group.name) }}</span>
                </td>
                <td>{{ group.capacityLabel }}</td>
                <td class="compare-col">
                  <button
                    type="button"
                    class="expand-btn"
                    @click="toggleExpand(group.key)"
                  >
                    {{
                      isExpanded(group.key)
                        ? '收合比價'
                        : group.optionCount > 1
                          ? `展開比價（${group.optionCount}）`
                          : '展開比價'
                    }}
                  </button>
                </td>
              </tr>
              <tr v-if="isExpanded(group.key)" class="flavor-row-wrap">
                <td :colspan="columns.length + 1" class="flavor-cell">
                  <div
                    v-for="flavorGroup in group.flavorGroups"
                    :key="flavorGroup.key"
                    class="flavor-block"
                  >
                    <div class="flavor-title">
                      <strong>{{ flavorGroup.title }}</strong>
                    </div>
                    <table class="flavor-table">
                      <thead>
                        <tr>
                          <th>商城</th>
                          <th>最低購買罐數</th>
                          <th>總價</th>
                          <th>單罐價</th>
                          <th>建議</th>
                          <th>購買</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr v-for="option in flavorGroup.options" :key="option.id">
                          <td>{{ option.shopName }}</td>
                          <td>{{ formatMinPurchaseQuantity(option) }}</td>
                          <td>{{ formatMoney(option.price) }}</td>
                          <td>{{ formatUnitPrice(getUnitPrice(option)) }}</td>
                          <td class="suggest-cell">
                            <span v-if="option.isBestUnitPrice" class="badge best">單罐最划算</span>
                            <span v-else class="badge neutral">—</span>
                          </td>
                          <td>
                            <a
                              v-if="option.shopUrl && option.inStock !== false"
                              :class="shopBuyLinkClass(option.shopName, option.shopUrl)"
                              :href="option.shopUrl"
                              target="_blank"
                              rel="noopener noreferrer sponsored"
                            >
                              前往
                            </a>
                            <span v-else-if="option.inStock === false" class="out-of-stock">無庫存</span>
                            <span v-else class="no-link">—</span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <p class="affiliate-note">
        本站部分連結為蝦皮等商城分潤連結，點擊購買可能為本站帶來收入；價格與評分請以商城頁面為準。
      </p>
    </template>
  </div>
</template>

<style scoped>
.cat-cans-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

.filters {
  margin-bottom: 1rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fafafa;
}

.filters-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.filters-header h2 {
  margin: 0;
  font-size: 1rem;
}

.filter-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 1rem;
}

fieldset {
  margin: 0;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: white;
}

legend {
  padding: 0 0.25rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #333;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  margin-top: 0.35rem;
  font-size: 0.9rem;
  cursor: pointer;
}

.price-range {
  grid-column: span 2;
}

.price-inputs {
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.price-inputs label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.85rem;
}

.price-inputs input {
  width: 100px;
  padding: 0.35rem 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.range-sep {
  padding-bottom: 0.4rem;
  color: #666;
}

.price-hint {
  margin: 0.5rem 0 0;
  font-size: 0.8rem;
  color: #888;
}

.btn-clear {
  padding: 0.35rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  font-size: 0.85rem;
}

.btn-clear:hover {
  border-color: #42b983;
  color: #42b983;
}

.result-banner {
  margin: 0 0 0.6rem;
  padding: 0.55rem 0.75rem;
  border: 1px solid #dcefe4;
  border-radius: 8px;
  background: #f5fbf7;
  transition: background-color 0.25s ease;
}

.result-banner.highlighted {
  background: #e6f7ee;
}

.result-count {
  margin: 0;
  font-size: 0.9rem;
  color: #2f5f46;
  font-weight: 600;
}

.chips {
  margin-top: 0.35rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
}

.chip {
  padding: 0.15rem 0.5rem;
  border-radius: 999px;
  background: #e8f5e9;
  color: #2f6f43;
  font-size: 0.78rem;
}

.compare-tip {
  margin: 0 0 0.6rem;
  padding: 0.45rem 0.7rem;
  border-left: 3px solid #42b983;
  background: #f8fcfa;
  color: #4d6b57;
  font-size: 0.85rem;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f4f4f4;
  white-space: nowrap;
}

.th-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.5rem;
}

.sort-buttons {
  display: inline-flex;
  flex-direction: column;
  gap: 1px;
}

.sort-btn {
  padding: 0;
  width: 1.25rem;
  height: 0.85rem;
  line-height: 1;
  border: 1px solid transparent;
  border-radius: 2px;
  background: transparent;
  color: #999;
  font-size: 0.7rem;
  cursor: pointer;
}

.sort-btn:hover {
  color: #42b983;
  border-color: #c8e6c9;
  background: #f1f8f4;
}

.sort-btn.active {
  color: #2e7d32;
  border-color: #42b983;
  background: #e8f5e9;
  font-weight: bold;
}

.empty {
  text-align: center;
  color: #888;
  padding: 2rem;
}

.hint {
  color: #666;
}

.error {
  color: #c62828;
}

.cell-sub {
  margin: 0.25rem 0 0;
  font-size: 0.8rem;
  color: #777;
  line-height: 1.35;
}

.compare-col {
  white-space: nowrap;
  text-align: center;
}

.expand-btn {
  border: 1px solid #c7e5d1;
  background: #f7fcf9;
  color: #2f6f43;
  border-radius: 999px;
  font-size: 0.8rem;
  padding: 0.25rem 0.65rem;
  cursor: pointer;
  white-space: nowrap;
}

.expand-btn:hover {
  background: #edf8f1;
}

.buy-link {
  display: inline-block;
  padding: 0.35rem 0.65rem;
  border-radius: 4px;
  background: #42b983;
  color: white;
  text-decoration: none;
  font-size: 0.85rem;
}

.buy-link.small,
.flavor-table .buy-link {
  padding: 0.2rem 0.5rem;
  font-size: 0.78rem;
}

.buy-link:hover {
  filter: brightness(0.92);
}

.buy-link.shopee {
  background: #f7452e;
}

.buy-link.momo {
  background: #c00053;
}

.out-of-stock {
  color: #c62828;
  font-size: 0.78rem;
}

.no-link {
  color: #bbb;
}

.flavor-row-wrap td {
  background: #fbfdfb;
}

.flavor-cell {
  padding: 0.5rem;
}

.flavor-block + .flavor-block {
  margin-top: 0.75rem;
}

.flavor-title {
  margin-bottom: 0.35rem;
  color: #2f5f46;
  font-size: 0.95rem;
}

.suggest-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.badge {
  display: inline-block;
  padding: 0.1rem 0.45rem;
  border-radius: 999px;
  font-size: 0.72rem;
  white-space: nowrap;
}

.badge.best {
  background: #e8f5e9;
  color: #2e7d32;
}

.badge.neutral {
  color: #bbb;
}

.flavor-table {
  width: 100%;
  border-collapse: collapse;
}

.flavor-table th,
.flavor-table td {
  padding: 6px 8px;
  border: 1px solid #e5eee8;
  font-size: 0.85rem;
}

.flavor-table th {
  background: #f4faf6;
  color: #335f46;
}

.affiliate-note {
  margin: 1rem 0 0;
  font-size: 0.8rem;
  color: #888;
  line-height: 1.5;
}

@media (max-width: 640px) {
  .price-range {
    grid-column: span 1;
  }
}
</style>
