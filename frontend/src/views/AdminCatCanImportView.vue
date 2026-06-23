<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  applyCatCansBatch,
  checkBatchConflicts,
  MAX_FLAVORS,
  parseAffiliateShare,
  type CatCanBatchApplyPayload,
  type CatCanBulkCreatePayload,
  type CatCanDraft,
  type CatCanFlavorConflict,
} from '@/services/catCanAdminApi'
import type { CatCan } from '@/types/catCan'
import { extractApiError } from '@/utils/apiError'
import { normalizeBrandOrName } from '@/utils/catCanText'

const router = useRouter()

const shareText = ref('')
const parsing = ref(false)
const saving = ref(false)
const parseHints = ref<string[]>([])
const errorMsg = ref('')
const successMsg = ref('')
const flavors = ref<string[]>([''])
const showConflictModal = ref(false)
const pendingPayload = ref<CatCanBulkCreatePayload | null>(null)
const conflicts = ref<CatCanFlavorConflict[]>([])
const conflictChoices = ref<Record<string, boolean>>({})

const form = reactive({
  category: '主食罐',
  brand: '',
  name: '',
  capacity: '',
  price: 0,
  totalCount: 1,
  unitPrice: undefined as number | undefined,
  shopName: 'Shopee',
  shopUrl: '',
  rating: undefined as number | undefined,
  reviewCount: undefined as number | undefined,
  description: '',
  inStock: true,
})

const canAddFlavor = computed(() => flavors.value.length < MAX_FLAVORS)

function resolveUnitPrice(price: number, totalCount?: number): number {
  const count = totalCount && totalCount > 0 ? totalCount : 1
  return Math.round((price / count) * 100) / 100
}

function applyDraft(draft: CatCanDraft) {
  form.category = draft.category || '主食罐'
  form.brand = draft.brand ? normalizeBrandOrName(draft.brand) : ''
  form.name = draft.name ? normalizeBrandOrName(draft.name) : ''
  form.capacity = draft.capacity || ''
  form.price = draft.price ?? 0
  form.totalCount = draft.totalCount && draft.totalCount > 0 ? draft.totalCount : 1
  form.unitPrice =
    draft.unitPrice ?? (form.price > 0 ? resolveUnitPrice(form.price, form.totalCount) : undefined)
  form.shopName = draft.shopName || 'Shopee'
  form.shopUrl = draft.shopUrl || ''
  form.rating = draft.rating ?? undefined
  form.reviewCount = draft.reviewCount ?? undefined
  form.description = draft.description || ''
  form.inStock = draft.inStock ?? true
  flavors.value = draft.flavor?.trim() ? [draft.flavor.trim()] : ['']
  parseHints.value = draft.parseHints ?? []
}

watch(
  () => [form.price, form.totalCount] as const,
  ([price, totalCount]) => {
    if (price > 0) {
      form.unitPrice = resolveUnitPrice(price, totalCount)
    }
  },
)

function addFlavorRow() {
  if (canAddFlavor.value) {
    flavors.value.push('')
  }
}

function removeFlavorRow(index: number) {
  if (flavors.value.length > 1) {
    flavors.value.splice(index, 1)
  }
}


async function onParse() {
  errorMsg.value = ''
  successMsg.value = ''
  parsing.value = true
  try {
    const draft = await parseAffiliateShare(shareText.value.trim())
    applyDraft(draft)
  } catch (e) {
    errorMsg.value = extractApiError(e)
  } finally {
    parsing.value = false
  }
}

function buildPayload(): CatCanBulkCreatePayload | null {
  const normalizedFlavors = [
    ...new Set(flavors.value.map((f) => f.trim()).filter((f) => f.length > 0)),
  ]
  if (normalizedFlavors.length === 0) {
    errorMsg.value = '請至少填寫一種口味'
    return null
  }
  if (normalizedFlavors.length > MAX_FLAVORS) {
    errorMsg.value = `口味最多 ${MAX_FLAVORS} 種`
    return null
  }

  const price = Number(form.price)
  if (!Number.isFinite(price) || price <= 0) {
    errorMsg.value = '售價須大於 0'
    return null
  }
  const brand = normalizeBrandOrName(form.brand)
  const name = normalizeBrandOrName(form.name)
  if (!brand || !name || !form.capacity.trim() || !form.shopUrl.trim()) {
    errorMsg.value = '請填寫品牌、名稱、容量與分潤連結'
    return null
  }

  const payload: CatCanBulkCreatePayload = {
    category: form.category.trim(),
    brand,
    name,
    capacity: form.capacity.trim(),
    price,
    shopName: form.shopName.trim(),
    shopUrl: form.shopUrl.trim(),
    inStock: form.inStock,
    flavors: normalizedFlavors,
    totalCount: form.totalCount && form.totalCount > 0 ? Number(form.totalCount) : 1,
    unitPrice: resolveUnitPrice(price, form.totalCount),
  }
  const description = form.description?.trim()
  if (description) payload.description = description
  if (form.rating != null && !Number.isNaN(Number(form.rating))) {
    payload.rating = Number(form.rating)
  }
  if (form.reviewCount) payload.reviewCount = Number(form.reviewCount)
  return payload
}

function unitPriceOf(item: {
  price: number
  totalCount?: number
  unitPrice?: number | null
}): number {
  if (item.unitPrice != null) {
    return item.unitPrice
  }
  const count = item.totalCount && item.totalCount > 0 ? item.totalCount : 1
  return Math.round((item.price / count) * 100) / 100
}

function defaultUseNew(conflict: CatCanFlavorConflict): boolean {
  return unitPriceOf(conflict.incoming) < unitPriceOf(conflict.existing)
}

function formatProductSummary(item: CatCan | CatCanFlavorConflict['incoming']): string {
  const count = item.totalCount && item.totalCount > 0 ? item.totalCount : 1
  const unit = unitPriceOf(item)
  return `${item.shopName} · ${count} 罐 · $${item.price} · 單罐 $${unit}`
}

function openConflictModal(payload: CatCanBulkCreatePayload, items: CatCanFlavorConflict[]) {
  pendingPayload.value = payload
  conflicts.value = items
  const choices: Record<string, boolean> = {}
  for (const conflict of items) {
    choices[conflict.flavor] = defaultUseNew(conflict)
  }
  conflictChoices.value = choices
  showConflictModal.value = true
}

function closeConflictModal() {
  showConflictModal.value = false
  pendingPayload.value = null
  conflicts.value = []
  conflictChoices.value = {}
}

async function finishSave(payload: CatCanBulkCreatePayload, useNewByFlavor: Record<string, boolean>) {
  const applyPayload: CatCanBatchApplyPayload = {
    ...payload,
    resolutions: Object.entries(useNewByFlavor).map(([flavor, useNew]) => ({
      flavor,
      useNew,
    })),
  }
  const saved = await applyCatCansBatch(applyPayload)
  successMsg.value = `已處理 ${saved.length} 筆商品，正在前往清單…`
  closeConflictModal()
  setTimeout(() => {
    void router.push('/cans')
  }, 800)
}

async function onSave() {
  errorMsg.value = ''
  successMsg.value = ''
  const payload = buildPayload()
  if (!payload) {
    return
  }

  saving.value = true
  try {
    const check = await checkBatchConflicts(payload)
    if (check.conflicts.length > 0) {
      openConflictModal(payload, check.conflicts)
      return
    }
    await finishSave(payload, {})
  } catch (e) {
    errorMsg.value = extractApiError(e)
  } finally {
    saving.value = false
  }
}

async function onConfirmConflicts() {
  if (!pendingPayload.value) {
    return
  }
  saving.value = true
  errorMsg.value = ''
  try {
    await finishSave(pendingPayload.value, conflictChoices.value)
  } catch (e) {
    errorMsg.value = extractApiError(e)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="admin-page">
    <h1>📦 新增分潤商品</h1>
    <p class="hint">
      貼上分享全文後解析，可一次新增最多 {{ MAX_FLAVORS }} 種口味（品牌、名稱、價格等欄位共用）。
    </p>

    <p v-if="errorMsg" class="msg error">{{ errorMsg }}</p>
    <p v-if="successMsg" class="msg success">{{ successMsg }}</p>

    <section class="card">
      <h2>1. 貼上分享內容</h2>
      <textarea
        v-model="shareText"
        rows="5"
        placeholder="例：『商品標題』，售價$928！分享給你 https://s.shopee.tw/..."
      />
      <button type="button" class="btn-primary" :disabled="parsing || !shareText.trim()" @click="onParse">
        {{ parsing ? '解析中…' : '解析文案' }}
      </button>
    </section>

    <section v-if="parseHints.length" class="hints">
      <p v-for="(hint, i) in parseHints" :key="i">• {{ hint }}</p>
    </section>

    <section class="card">
      <h2>2. 確認並儲存</h2>
      <form class="form-grid" @submit.prevent="onSave">
        <label>種類 *<input v-model="form.category" required /></label>
        <label>品牌 *<input v-model="form.brand" required /></label>
        <label>名稱 *<input v-model="form.name" required /></label>
        <label>容量 *<input v-model="form.capacity" required placeholder="80g" /></label>
        <label>售價 *<input v-model.number="form.price" type="number" min="0.01" step="0.01" required /></label>
        <label>罐數 *<input v-model.number="form.totalCount" type="number" min="1" required /></label>
        <label>單罐價<input v-model.number="form.unitPrice" type="number" min="0" step="0.01" /></label>
        <label>商城 *<input v-model="form.shopName" required /></label>
        <label class="full">分潤連結 *<input v-model="form.shopUrl" required /></label>
        <label>評分<input v-model.number="form.rating" type="number" min="0" max="5" step="0.1" /></label>
        <label>評論數<input v-model.number="form.reviewCount" type="number" min="0" /></label>
        <label class="full">說明<textarea v-model="form.description" rows="3" /></label>

        <div class="full flavor-section">
          <div class="flavor-header">
            <span>口味 *（最多 {{ MAX_FLAVORS }} 種，每種各存一筆）</span>
            <button type="button" class="btn-secondary" :disabled="!canAddFlavor" @click="addFlavorRow">
              + 新增口味
            </button>
          </div>
          <div v-for="(_, index) in flavors" :key="index" class="flavor-row">
            <input v-model="flavors[index]" type="text" :placeholder="`口味 ${index + 1}`" />
            <button
              v-if="flavors.length > 1"
              type="button"
              class="btn-remove"
              title="移除此口味"
              @click="removeFlavorRow(index)"
            >
              移除
            </button>
          </div>
        </div>

        <div class="stock-row full">
          <label class="stock-checkbox">
            <input v-model="form.inStock" type="checkbox" />
            <span>有庫存</span>
          </label>
        </div>
        <div class="actions full">
          <button type="submit" class="btn-primary" :disabled="saving">
            {{ saving ? '儲存中…' : '儲存到資料庫' }}
          </button>
          <router-link to="/cans" class="btn-link">返回清單</router-link>
        </div>
      </form>
    </section>

    <div v-if="showConflictModal" class="modal-overlay" @click.self="closeConflictModal">
      <div class="modal" role="dialog" aria-modal="true" aria-labelledby="conflict-title">
        <h2 id="conflict-title">發現已存在的商品</h2>
        <p class="modal-hint">
          以下口味在相同商城已有紀錄。請選擇要保留哪一版，避免同商品在同一商城出現兩個購買連結。
        </p>

        <div v-for="conflict in conflicts" :key="conflict.flavor" class="conflict-block">
          <h3>
            口味：{{ conflict.flavor }}
            <span v-if="conflict.duplicateCount > 1" class="dup-note">
              （資料庫有 {{ conflict.duplicateCount }} 筆重複，選擇新版會合併為一筆）
            </span>
          </h3>
          <div class="compare-grid">
            <label
              class="compare-card"
              :class="{ selected: conflictChoices[conflict.flavor] === false }"
            >
              <input
                v-model="conflictChoices[conflict.flavor]"
                type="radio"
                :name="`choice-${conflict.flavor}`"
                :value="false"
              />
              <div class="compare-body">
                <strong>保留資料庫</strong>
                <p>{{ formatProductSummary(conflict.existing) }}</p>
                <p class="link-line" :title="conflict.existing.shopUrl ?? ''">
                  {{ conflict.existing.shopUrl || '—' }}
                </p>
              </div>
            </label>
            <label
              class="compare-card"
              :class="{ selected: conflictChoices[conflict.flavor] === true }"
            >
              <input
                v-model="conflictChoices[conflict.flavor]"
                type="radio"
                :name="`choice-${conflict.flavor}`"
                :value="true"
              />
              <div class="compare-body">
                <strong>使用新版</strong>
                <p>{{ formatProductSummary(conflict.incoming) }}</p>
                <p class="link-line" :title="conflict.incoming.shopUrl">
                  {{ conflict.incoming.shopUrl }}
                </p>
                <p
                  v-if="unitPriceOf(conflict.incoming) < unitPriceOf(conflict.existing)"
                  class="cheaper-tag"
                >
                  單罐較便宜
                </p>
              </div>
            </label>
          </div>
        </div>

        <div class="modal-actions">
          <button type="button" class="btn-secondary" :disabled="saving" @click="closeConflictModal">
            取消
          </button>
          <button type="button" class="btn-primary" :disabled="saving" @click="onConfirmConflicts">
            {{ saving ? '儲存中…' : '確認並儲存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

.hint {
  color: #666;
  font-size: 0.95rem;
}

.card {
  margin-top: 1.25rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fafafa;
}

.card h2 {
  margin: 0 0 0.75rem;
  font-size: 1rem;
}

textarea,
input:not([type='checkbox']) {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.card > textarea {
  margin-bottom: 0.75rem;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

label {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  font-size: 0.9rem;
}

.full {
  grid-column: 1 / -1;
}

.flavor-section {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.flavor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
  font-size: 0.9rem;
}

.flavor-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.flavor-row input {
  flex: 1;
}

.stock-row {
  display: flex;
  justify-content: flex-start;
}

.stock-checkbox {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  gap: 0.5rem;
  margin: 0;
  cursor: pointer;
}

.stock-checkbox input[type='checkbox'] {
  width: auto;
  margin: 0;
  flex-shrink: 0;
}

.actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.btn-primary {
  padding: 0.55rem 1rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary {
  padding: 0.35rem 0.75rem;
  background: white;
  color: #42b983;
  border: 1px solid #42b983;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.btn-secondary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-remove {
  padding: 0.35rem 0.6rem;
  background: white;
  color: #c62828;
  border: 1px solid #ef9a9a;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-link {
  color: #42b983;
  text-decoration: none;
}

.hints {
  margin-top: 0.75rem;
  padding: 0.75rem 1rem;
  background: #fff8e1;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #795548;
}

.msg {
  padding: 0.5rem 0.75rem;
  border-radius: 4px;
  margin-top: 0.75rem;
}

.error {
  background: #ffebee;
  color: #c62828;
}

.success {
  background: #e8f5e9;
  color: #2e7d32;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background: rgba(0, 0, 0, 0.45);
}

.modal {
  width: min(720px, 100%);
  max-height: 90vh;
  overflow-y: auto;
  padding: 1.25rem;
  border-radius: 10px;
  background: white;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.modal h2 {
  margin: 0 0 0.5rem;
  font-size: 1.1rem;
}

.modal-hint {
  margin: 0 0 1rem;
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
}

.conflict-block + .conflict-block {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.conflict-block h3 {
  margin: 0 0 0.6rem;
  font-size: 0.95rem;
  color: #333;
}

.dup-note {
  font-size: 0.8rem;
  color: #8d6e00;
  font-weight: normal;
}

.compare-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.compare-card {
  display: block;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  padding: 0.75rem;
  cursor: pointer;
  transition: border-color 0.15s ease, background-color 0.15s ease;
}

.compare-card.selected {
  border-color: #42b983;
  background: #f7fcf9;
}

.compare-card input[type='radio'] {
  margin: 0 0 0.5rem;
}

.compare-body strong {
  display: block;
  margin-bottom: 0.35rem;
}

.compare-body p {
  margin: 0.2rem 0;
  font-size: 0.85rem;
  color: #444;
}

.link-line {
  word-break: break-all;
  color: #777 !important;
  font-size: 0.78rem !important;
}

.cheaper-tag {
  margin-top: 0.35rem !important;
  color: #2e7d32 !important;
  font-weight: 600;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.25rem;
}

@media (max-width: 560px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .flavor-header {
    flex-direction: column;
    align-items: stretch;
  }

  .compare-grid {
    grid-template-columns: 1fr;
  }
}
</style>
