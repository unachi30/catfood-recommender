<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface CatCan {
  id: number
  category: string
  brand: string
  name: string
  capacity: string
  price: number
  shop_name: string
  rating: number
}

const catCans = ref<CatCan[]>([])

onMounted(async () => {
  const res = await fetch('http://localhost:8080/api/cans')
  catCans.value = await res.json()
})
</script>

<template>
  <div>
    <h1>🐱 貓罐頭清單</h1>
    <table>
      <thead>
        <tr>
          <th>種類</th>
          <th>品牌</th>
          <th>名稱</th>
          <th>容量</th>
          <th>價格</th>
          <th>商城</th>
          <th>評分</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="can in catCans" :key="can.id">
          <td>{{ can.category }}</td>
          <td>{{ can.brand }}</td>
          <td>{{ can.name }}</td>
          <td>{{ can.capacity }}</td>
          <td>{{ can.price }}</td>
          <td>{{ can.shop_name }}</td>
          <td>{{ can.rating }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>


