<template>
  <div class="compare-page">
    <div class="page-header">
      <h2>商品对比</h2>
      <div>
        <el-button @click="clearCompare" :disabled="compareList.length === 0">清空对比</el-button>
        <el-button type="primary" @click="goBack">返回商品列表</el-button>
      </div>
    </div>

    <el-empty v-if="compareList.length === 0" description="暂未添加对比商品，请前往商品列表添加">
      <el-button type="primary" @click="$router.push('/products')">去逛逛</el-button>
    </el-empty>

    <div v-else class="compare-table-wrapper">
      <table class="compare-table">
        <thead>
          <tr>
            <th class="label-col">对比项</th>
            <th v-for="(item, idx) in compareList" :key="item.id" class="product-col">
              <div class="product-header">
                <img :src="item.image || '/placeholder.png'" class="product-thumb" />
                <h4>{{ item.name }}</h4>
                <p class="price">¥{{ item.price }}</p>
                <el-button size="small" type="danger" @click="removeItem(idx)">移除</el-button>
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="label-col">品牌</td>
            <td v-for="item in compareList" :key="'brand-' + item.id">{{ item.brand || '-' }}</td>
          </tr>
          <tr>
            <td class="label-col">OE号</td>
            <td v-for="item in compareList" :key="'oe-' + item.id">{{ item.oeNumber || '-' }}</td>
          </tr>
          <tr>
            <td class="label-col">适用车型</td>
            <td v-for="item in compareList" :key="'car-' + item.id">{{ item.suitableCar || '-' }}</td>
          </tr>
          <tr>
            <td class="label-col">价格</td>
            <td v-for="item in compareList" :key="'price-' + item.id" class="price-cell">¥{{ item.price }}</td>
          </tr>
          <tr>
            <td class="label-col">库存</td>
            <td v-for="item in compareList" :key="'stock-' + item.id">{{ item.stock || 0 }}</td>
          </tr>
          <tr>
            <td class="label-col">销量</td>
            <td v-for="item in compareList" :key="'sales-' + item.id">{{ item.sales || 0 }}</td>
          </tr>
          <tr>
            <td class="label-col">评分</td>
            <td v-for="item in compareList" :key="'rating-' + item.id">
              <el-rate :model-value="item.avgRating || 0" disabled show-score />
            </td>
          </tr>
          <tr>
            <td class="label-col">操作</td>
            <td v-for="item in compareList" :key="'action-' + item.id">
              <el-button type="primary" size="small" @click="$router.push('/product/' + item.id)">查看详情</el-button>
              <el-button type="success" size="small" @click="addToCart(item)">加入购物车</el-button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const compareList = ref([])

const loadCompare = () => {
  const stored = localStorage.getItem('compareList')
  if (stored) {
    compareList.value = JSON.parse(stored)
  }
}

const removeItem = (idx) => {
  compareList.value.splice(idx, 1)
  localStorage.setItem('compareList', JSON.stringify(compareList.value))
}

const clearCompare = () => {
  compareList.value = []
  localStorage.removeItem('compareList')
}

const addToCart = async (item) => {
  try {
    await request.post('/cart/add', { productId: item.id, quantity: 1 })
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const goBack = () => {
  window.history.back()
}

onMounted(() => loadCompare())
</script>

<style scoped>
.compare-page {
  max-width: 1100px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
}

.compare-table-wrapper {
  overflow-x: auto;
}

.compare-table {
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #ebeef5;
}

.compare-table th, .compare-table td {
  border: 1px solid #ebeef5;
  padding: 12px;
  text-align: center;
  vertical-align: middle;
}

.label-col {
  background: #f5f7fa;
  font-weight: bold;
  width: 120px;
  min-width: 100px;
}

.product-col {
  min-width: 200px;
}

.product-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.product-thumb {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-header h4 {
  margin: 0;
  font-size: 14px;
}

.price {
  color: #ff6600;
  font-weight: bold;
  font-size: 16px;
  margin: 0;
}

.price-cell {
  color: #ff6600;
  font-weight: bold;
}
</style>