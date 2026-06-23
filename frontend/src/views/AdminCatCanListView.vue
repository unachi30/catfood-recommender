<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchCatCans } from '@/services/catCanApi'
import type { CatCan } from '@/types/catCan'

const catCans = ref<CatCan[]>([])
const loading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  try {
    catCans.value = await fetchCatCans()
  } catch {
    errorMsg.value = '載入失敗'
  } finally {
    loading.value = false
  }
})

function unitPrice(can: CatCan): string {
  if (can.unitPrice != null) {
    return String(can.unitPrice)
  }
  const count = can.totalCount && can.totalCount > 0 ? can.totalCount : 1
  return String(Math.round((can.price / count) * 100) / 100)
}
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>🛠️ 管理商品</h1>
      <router-link to="/admin/cans/import" class="btn-primary">+ 新增商品</router-link>
    </div>
    <p class="hint">選擇要修改的商品，可編輯所有欄位內容。</p>

    <p v-if="loading" class="msg">載入中…</p>
    <p v-else-if="errorMsg" class="msg error">{{ errorMsg }}</p>

    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>品牌</th>
            <th>名稱</th>
            <th>口味</th>
            <th>容量</th>
            <th>商城</th>
            <th>總價</th>
            <th>單罐價</th>
            <th>庫存</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="catCans.length === 0">
            <td colspan="10" class="empty">尚無商品</td>
          </tr>
          <tr v-for="can in catCans" :key="can.id">
            <td>{{ can.id }}</td>
            <td>{{ can.brand }}</td>
            <td>{{ can.name }}</td>
            <td>{{ can.flavor || '—' }}</td>
            <td>{{ can.capacity }}</td>
            <td>{{ can.shopName }}</td>
            <td>${{ can.price }}</td>
            <td>${{ unitPrice(can) }}</td>
            <td>
              <span :class="can.inStock === false ? 'stock-no' : 'stock-yes'">
                {{ can.inStock === false ? '無' : '有' }}
              </span>
            </td>
            <td>
              <router-link :to="`/admin/cans/${can.id}/edit`" class="edit-link">編輯</router-link>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  max-width: 1100px;
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

.hint {
  color: #666;
  font-size: 0.95rem;
}

.btn-primary {
  display: inline-block;
  padding: 0.5rem 0.9rem;
  background: #42b983;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 0.9rem;
}

.table-wrap {
  margin-top: 1rem;
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
  font-size: 0.9rem;
}

th {
  background: #f4f4f4;
  white-space: nowrap;
}

.empty {
  text-align: center;
  color: #888;
  padding: 2rem;
}

.edit-link {
  color: #42b983;
  text-decoration: none;
  font-weight: 600;
}

.stock-yes {
  color: #2e7d32;
}

.stock-no {
  color: #c62828;
}

.msg {
  margin-top: 1rem;
}

.error {
  color: #c62828;
}
</style>
