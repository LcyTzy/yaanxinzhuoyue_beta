<template>
  <div class="dashboard">
    <div class="page-header">
      <h2 class="page-title">数据概览</h2>
      <p class="page-subtitle">欢迎回来，管理员！以下是今日数据概览</p>
    </div>
    <el-row :gutter="24">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon product">
              <el-icon :size="32"><Goods /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.productCount || 0 }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon order">
              <el-icon :size="32"><List /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon category">
              <el-icon :size="32"><Menu /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.categoryCount || 0 }}</div>
              <div class="stat-label">分类总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon revenue">
              <el-icon :size="32"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatAmount(stats.todayRevenue) }}</div>
              <div class="stat-label">今日营收</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon today-order">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card warning-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon :size="32"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
              <div class="stat-label">待发货订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card warning-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon stock">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.lowStockProducts || 0 }}</div>
              <div class="stat-label">低库存商品</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px">
      <el-col :span="16">
        <el-card>
          <template #header>近7天销售趋势</template>
          <div ref="salesChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>商品销量排行</template>
          <div ref="productRankRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px">
      <el-col :span="12">
        <el-card>
          <template #header>近7天新增用户</template>
          <div ref="userGrowthRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>待处理事项</template>
          <div class="todo-list">
            <div class="todo-item" @click="$router.push('/orders')">
              <el-icon :size="20" color="#e6a23c"><Clock /></el-icon>
              <span>待发货订单：<strong>{{ stats.pendingOrders || 0 }}</strong> 笔</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="todo-item" @click="$router.push('/refunds')">
              <el-icon :size="20" color="#f56c6c"><Money /></el-icon>
              <span>待处理退款：<strong>{{ stats.pendingRefunds || 0 }}</strong> 笔</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
            <div class="todo-item" @click="$router.push('/products')">
              <el-icon :size="20" color="#f56c6c"><Warning /></el-icon>
              <span>低库存商品：<strong>{{ stats.lowStockProducts || 0 }}</strong> 件</span>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const stats = ref({})
const salesChartRef = ref(null)
const productRankRef = ref(null)
const userGrowthRef = ref(null)
let salesChart = null
let productRankChart = null
let userGrowthChart = null

const formatAmount = (val) => {
  if (val === null || val === undefined) return '0.00'
  return Number(val).toFixed(2)
}

const initSalesChart = (data) => {
  if (!salesChartRef.value) return
  if (!salesChart) salesChart = echarts.init(salesChartRef.value)
  const dates = (data || []).map(d => d.date.slice(5))
  const revenues = (data || []).map(d => Number(d.revenue))
  const orders = (data || []).map(d => d.orders)
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: [
      { type: 'value', name: '销售额(元)' },
      { type: 'value', name: '订单数' }
    ],
    series: [
      { name: '销售额', type: 'bar', data: revenues, itemStyle: { color: '#409EFF' } },
      { name: '订单数', type: 'line', yAxisIndex: 1, data: orders, itemStyle: { color: '#67C23A' } }
    ]
  })
}

const initProductRankChart = (data) => {
  if (!productRankRef.value) return
  if (!productRankChart) productRankChart = echarts.init(productRankRef.value)
  const names = (data || []).map(d => d.name).reverse()
  const counts = (data || []).map(d => d.count).reverse()
  productRankChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: names, axisLabel: { width: 80, overflow: 'truncate' } },
    series: [{ type: 'bar', data: counts, itemStyle: { color: '#E6A23C' } }]
  })
}

const initUserGrowthChart = (data) => {
  if (!userGrowthRef.value) return
  if (!userGrowthChart) userGrowthChart = echarts.init(userGrowthRef.value)
  const dates = (data || []).map(d => d.date.slice(5))
  const counts = (data || []).map(d => d.count)
  userGrowthChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', name: '新增用户' },
    series: [{
      type: 'line', data: counts, smooth: true,
      areaStyle: { color: 'rgba(103,194,58,0.2)' },
      itemStyle: { color: '#67C23A' }
    }]
  })
}

const getStats = async () => {
  try {
    const res = await request.get('/admin/stats')
    stats.value = res.data || {}
    await nextTick()
    initSalesChart(res.data.salesTrend)
    initProductRankChart(res.data.topProducts)
    initUserGrowthChart(res.data.userGrowth)
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

onMounted(() => {
  getStats()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1e4c;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.stat-card {
  border: none;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-icon.product { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.user { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.order { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.category { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.revenue { background: linear-gradient(135deg, #f6d365 0%, #fda085 100%); }
.stat-icon.today-order { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }
.stat-icon.pending { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); }
.stat-icon.stock { background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%); }

.warning-card {
  border-left: 4px solid #e6a23c;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1d1e4c;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #8c8c8c;
  margin-top: 4px;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.todo-item:hover {
  background: #ecf5ff;
}

.todo-item span {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.todo-item strong {
  color: #303133;
}
</style>