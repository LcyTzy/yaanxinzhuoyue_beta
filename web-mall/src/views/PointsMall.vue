<template>
  <div class="points-mall-page">
    <div class="page-header">
      <h2>积分商城</h2>
    </div>

    <el-card class="sign-card" v-loading="loading">
      <div class="sign-area">
        <div class="sign-info">
          <div class="my-points">
            <span class="label">我的积分</span>
            <span class="value">{{ signStatus.totalPoints || 0 }}</span>
          </div>
          <div class="sign-days">
            连续签到 <strong>{{ signStatus.consecutiveDays || 0 }}</strong> 天
          </div>
        </div>
        <el-button 
          type="primary" 
          size="large" 
          :disabled="signStatus.signedToday"
          @click="handleSignIn"
          class="sign-btn"
        >
          {{ signStatus.signedToday ? '今日已签到' : '签到领积分' }}
        </el-button>
      </div>
      <div class="sign-rules">
        <span>签到规则：每日签到+5积分，连续3天+10积分，连续7天+20积分</span>
      </div>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>积分兑换</template>
      <div class="products-grid" v-loading="loading">
        <div v-for="product in products" :key="product.id" class="product-card">
          <div class="product-image">
            <el-icon :size="48"><Present /></el-icon>
          </div>
          <div class="product-name">{{ product.name }}</div>
          <div class="product-desc">{{ product.description }}</div>
          <div class="product-points">
            <span class="points-value">{{ product.points }}</span> 积分
          </div>
          <div class="product-stock">库存：{{ product.stock }}</div>
          <el-button 
            type="primary" 
            :disabled="(signStatus.totalPoints || 0) < product.points"
            @click="handleExchange(product)"
            style="width: 100%; margin-top: 10px"
          >
            {{ (signStatus.totalPoints || 0) < product.points ? '积分不足' : '立即兑换' }}
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card style="margin-top: 20px" v-if="history.length > 0">
      <template #header>兑换记录</template>
      <el-table :data="history" style="width: 100%">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="points" label="消耗积分" width="120" />
        <el-table-column label="兑换时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">
              {{ row.status === 0 ? '待处理' : '已完成' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const signStatus = ref({ signedToday: false, consecutiveDays: 0, totalPoints: 0 })
const products = ref([])
const history = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const [statusRes, productsRes, historyRes] = await Promise.all([
      request.get('/points-mall/sign-status'),
      request.get('/points-mall/products'),
      request.get('/points-mall/history')
    ])
    signStatus.value = statusRes.data
    products.value = productsRes.data
    history.value = historyRes.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSignIn = async () => {
  try {
    const res = await request.post('/points-mall/sign-in')
    ElMessage.success(`签到成功！获得 ${res.data.points} 积分`)
    loadData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '签到失败')
  }
}

const handleExchange = async (product) => {
  try {
    await ElMessageBox.confirm(
      `确认使用 ${product.points} 积分兑换「${product.name}」？`,
      '确认兑换',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await request.post(`/points-mall/exchange/${product.id}`)
    ElMessage.success('兑换成功！')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.response?.data?.message || '兑换失败')
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString() + ' ' + new Date(time).toLocaleTimeString().slice(0, 5)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.points-mall-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header h2 {
  margin: 0 0 20px;
  font-size: 22px;
}

.sign-card {
  background: linear-gradient(135deg, #ff6600, #ff8c42);
  color: #fff;
}

.sign-card :deep(.el-card__body) {
  padding: 24px;
}

.sign-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.my-points .label {
  font-size: 14px;
  opacity: 0.9;
}

.my-points .value {
  font-size: 36px;
  font-weight: bold;
  margin-left: 12px;
}

.sign-days {
  margin-top: 8px;
  font-size: 14px;
  opacity: 0.9;
}

.sign-btn {
  background: #fff;
  color: #ff6600;
  border: none;
  font-size: 16px;
  padding: 12px 32px;
}

.sign-btn:hover {
  background: #fff5f0;
  color: #ff6600;
}

.sign-rules {
  margin-top: 16px;
  font-size: 12px;
  opacity: 0.8;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
}

.product-card:hover {
  border-color: #ff6600;
  box-shadow: 0 4px 12px rgba(255,102,0,0.15);
  transform: translateY(-2px);
}

.product-image {
  width: 80px;
  height: 80px;
  margin: 0 auto 12px;
  background: #f5f5f5;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ff6600;
}

.product-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.product-points {
  font-size: 14px;
  color: #666;
}

.points-value {
  font-size: 22px;
  font-weight: bold;
  color: #ff6600;
}

.product-stock {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>