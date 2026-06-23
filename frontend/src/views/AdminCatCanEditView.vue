<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchCatCanById, updateCatCan, type CatCanUpdatePayload } from '@/services/catCanAdminApi'
import { extractApiError } from '@/utils/apiError'
import { normalizeBrandOrName } from '@/utils/catCanText'

const route = useRoute()
const router = useRouter()

const canId = computed(() => Number(route.params.id))
const loading = ref(true)
const saving = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

const form = reactive({
  category: '主食罐',
  brand: '',
  name: '',
  flavor: '',
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

function resolveUnitPrice(price: number, totalCount?: number): number {
  const count = totalCount && totalCount > 0 ? totalCount : 1
  return Math.round((price / count) * 100) / 100
}

watch(
  () => [form.price, form.totalCount] as const,
  ([price, totalCount]) => {
    if (price > 0) {
      form.unitPrice = resolveUnitPrice(price, totalCount)
    }
  },
)

onMounted(async () => {
  if (!Number.isFinite(canId.value)) {
    errorMsg.value = '無效的商品 ID'
    loading.value = false
    return
  }
  try {
    const can = await fetchCatCanById(canId.value)
    form.category = can.category
    form.brand = can.brand
    form.name = can.name
    form.flavor = can.flavor ?? ''
    form.capacity = can.capacity
    form.price = can.price
    form.totalCount = can.totalCount && can.totalCount > 0 ? can.totalCount : 1
    form.unitPrice = can.unitPrice ?? resolveUnitPrice(can.price, form.totalCount)
    form.shopName = can.shopName
    form.shopUrl = can.shopUrl ?? ''
    form.rating = can.rating ?? undefined
    form.reviewCount = can.reviewCount ?? undefined
    form.description = can.description ?? ''
    form.inStock = can.inStock !== false
  } catch (e) {
    errorMsg.value = extractApiError(e)
  } finally {
    loading.value = false
  }
})

function buildPayload(): CatCanUpdatePayload | null {
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

  const payload: CatCanUpdatePayload = {
    category: form.category.trim(),
    brand,
    name,
    capacity: form.capacity.trim(),
    price,
    shopName: form.shopName.trim(),
    shopUrl: form.shopUrl.trim(),
    inStock: form.inStock,
    totalCount: form.totalCount && form.totalCount > 0 ? Number(form.totalCount) : 1,
    unitPrice: resolveUnitPrice(price, form.totalCount),
  }
  const flavor = form.flavor.trim()
  if (flavor) {
    payload.flavor = flavor
  }
  const description = form.description?.trim()
  if (description) {
    payload.description = description
  }
  if (form.rating != null && !Number.isNaN(Number(form.rating))) {
    payload.rating = Number(form.rating)
  }
  if (form.reviewCount) {
    payload.reviewCount = Number(form.reviewCount)
  }
  return payload
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
    await updateCatCan(canId.value, payload)
    successMsg.value = '已儲存，正在返回列表…'
    setTimeout(() => {
      void router.push('/admin/cans')
    }, 600)
  } catch (e) {
    errorMsg.value = extractApiError(e)
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>✏️ 編輯商品 #{{ canId }}</h1>
      <router-link to="/admin/cans" class="back-link">← 返回列表</router-link>
    </div>

    <p v-if="loading" class="msg">載入中…</p>
    <p v-else-if="errorMsg && !form.brand" class="msg error">{{ errorMsg }}</p>

    <template v-else>
      <p v-if="errorMsg" class="msg error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="msg success">{{ successMsg }}</p>

      <form class="form-grid card" @submit.prevent="onSave">
        <label>種類 *<input v-model="form.category" required /></label>
        <label>品牌 *<input v-model="form.brand" required /></label>
        <label>名稱 *<input v-model="form.name" required /></label>
        <label>口味<input v-model="form.flavor" placeholder="例：鮪魚慕斯" /></label>
        <label>容量 *<input v-model="form.capacity" required placeholder="80g" /></label>
        <label>售價 *<input v-model.number="form.price" type="number" min="0.01" step="0.01" required /></label>
        <label>罐數 *<input v-model.number="form.totalCount" type="number" min="1" required /></label>
        <label>單罐價<input v-model.number="form.unitPrice" type="number" min="0" step="0.01" /></label>
        <label>商城 *<input v-model="form.shopName" required /></label>
        <label class="full">分潤連結 *<input v-model="form.shopUrl" required /></label>
        <label>評分<input v-model.number="form.rating" type="number" min="0" max="5" step="0.1" /></label>
        <label>評論數<input v-model.number="form.reviewCount" type="number" min="0" /></label>
        <label class="full">說明<textarea v-model="form.description" rows="3" /></label>

        <div class="stock-row full">
          <label class="stock-checkbox">
            <input v-model="form.inStock" type="checkbox" />
            <span>有庫存</span>
          </label>
        </div>

        <div class="actions full">
          <button type="submit" class="btn-primary" :disabled="saving">
            {{ saving ? '儲存中…' : '儲存變更' }}
          </button>
          <router-link to="/admin/cans" class="btn-link">取消</router-link>
        </div>
      </form>
    </template>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 1rem 2rem;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  flex-wrap: wrap;
}

.page-header h1 {
  margin: 0;
}

.back-link {
  color: #42b983;
  text-decoration: none;
}

.card {
  margin-top: 1rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fafafa;
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

textarea,
input:not([type='checkbox']) {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
}

.stock-row {
  display: flex;
  justify-content: flex-start;
}

.stock-checkbox {
  display: inline-flex;
  flex-direction: row;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  cursor: pointer;
}

.stock-checkbox input[type='checkbox'] {
  width: auto;
  margin: 0;
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

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-link {
  color: #42b983;
  text-decoration: none;
}

.msg {
  margin-top: 1rem;
}

.error {
  color: #c62828;
}

.success {
  color: #2e7d32;
}

@media (max-width: 560px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
