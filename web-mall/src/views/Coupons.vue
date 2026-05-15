<template>
  <div class="coupons-page">
    <h2 class="page-title">优惠券中心</h2>
    
    <div class="section-title">可领取优惠券</div>
    <div class="coupon-list" v-loading="loading">
      <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-card">
        <div class="coupon-left">
          <div class="coupon-amount">
            <template v-if="coupon.type === 1">
              <span class="symbol">¥</span>
              <span class="value">{{ coupon.discountAmount }}</span>
            </template>
            <template v-else>
              <span class="value">{{ coupon.discountRate * 10 }}折</span>
            </template>
          </div>
          <div class="coupon-condition">满{{ coupon.minAmount }}元可用</div>
        </div>
        <div class="coupon-right">
          <div class="coupon-name">{{ coupon.name }}</div>
          <div class="coupon-time">{{ coupon.startTime?.split(' ')[0] }} ~ {{ coupon.endTime?.split(' ')[0] }}</div>
          <div class="coupon-stock">已领 {{ coupon.receiveCount }}/{{ coupon.totalCount }}</div>
          <el-button type="primary" size="small" @click="handleReceive(coupon.id)" :disabled="coupon.receiveCount >= coupon.totalCount">
            {{ coupon.receiveCount >= coupon.totalCount ? '已领完' : '立即领取' }}
          </el-button>
        </div>
      </div>
    </div>

    <div class="section-title" style="margin-top: 30px">我的优惠券</div>
    <el-tabs v-model="activeTab" @tab-click="loadMyCoupons">
      <el-tab-pane label="全部" :name="null" />
      <el-tab-pane label="未使用" :name="0" />
      <el-tab-pane label="已使用" :name="1" />
      <el-tab-pane label="已过期" :name="2" />
    </el-tabs>
    <div class="my-coupon-list" v-loading="myLoading">
      <div v-for="uc in myCoupons" :key="uc.id" class="my-coupon-card" :class="{ 'used': uc.status !== 0 }">
        <div class="my-coupon-info">
          <div class="my-coupon-name">{{ uc.coupon?.name || '优惠券' }}</div>
          <div class="my-coupon-time">有效期至：{{ uc.coupon?.endTime?.split(' ')[0] || '-' }}</div>
        </div>
        <div class="my-coupon-status">
          <el-tag v-if="uc.status === 0" type="success">未使用</el-tag>
          <el-tag v-else-if="uc.status === 1" type="info">已使用</el-tag>
          <el-tag v-else type="warning">已过期</el-tag>
        </div>
      </div>
      <el-empty v-if="!myLoading && myCoupons.length === 0" description="暂无优惠券" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAvailableCoupons, receiveCoupon, getMyCoupons } from '@/api/coupon'
import { ElMessage } from 'element-plus'

const availableCoupons = ref([])
const myCoupons = ref([])
const loading = ref(false)
const myLoading = ref(false)
const activeTab = ref(null)

const loadAvailableCoupons = async () => {
  loading.value = true
  try {
    const res = await getAvailableCoupons()
    availableCoupons.value = res.data || []
  } catch (e) {
    console.error('加载优惠券失败', e)
  } finally {
    loading.value = false
  }
}

const loadMyCoupons = async () => {
  myLoading.value = true
  try {
    const res = await getMyCoupons({ status: activeTab.value, pageNum: 1, pageSize: 20 })
    myCoupons.value = res.data?.records || []
  } catch (e) {
    console.error('加载我的优惠券失败', e)
  } finally {
    myLoading.value = false
  }
}

const handleReceive = async (couponId) => {
  try {
    await receiveCoupon(couponId)
    ElMessage.success('领取成功')
    loadAvailableCoupons()
    loadMyCoupons()
  } catch (e) {
    ElMessage.error(e.message || '领取失败')
  }
}

onMounted(() => {
  loadAvailableCoupons()
  loadMyCoupons()
})
</script>

<style scoped>
.coupons-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 16px;
  color: #333;
}

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.coupon-card {
  display: flex;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.coupon-left {
  background: linear-gradient(135deg, #ff6600 0%, #ff8533 100%);
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 120px;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
}

.coupon-amount .symbol {
  font-size: 18px;
}

.coupon-amount .value {
  font-size: 32px;
  font-weight: bold;
}

.coupon-condition {
  font-size: 12px;
  margin-top: 4px;
  opacity: 0.9;
}

.coupon-right {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 4px;
}

.coupon-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.coupon-stock {
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
}

.my-coupon-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.my-coupon-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.my-coupon-card.used {
  opacity: 0.6;
}

.my-coupon-info {
  flex: 1;
}

.my-coupon-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
}

.my-coupon-time {
  font-size: 12px;
  color: #999;
}
</style>
