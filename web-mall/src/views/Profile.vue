<template>
  <div class="profile-page">
    <h2 class="page-title">个人中心</h2>
    <div class="profile-content">
      <div class="profile-card">
        <div class="avatar-section">
          <el-avatar :size="80" :icon="UserFilled" />
          <h3>{{ userInfo?.nickname || userInfo?.phone || '用户' }}</h3>
        </div>
        <div class="info-section">
          <div class="info-item">
            <span class="label">手机号</span>
            <span>{{ userInfo?.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">昵称</span>
            <span>{{ userInfo?.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间</span>
            <span>{{ userInfo?.createTime || '-' }}</span>
          </div>
        </div>
      </div>

      <div class="quick-links">
        <div class="link-card" @click="$router.push('/orders')">
          <el-icon :size="32" color="#ff6600"><List /></el-icon>
          <span>我的订单</span>
        </div>
        <div class="link-card" @click="$router.push('/cart')">
          <el-icon :size="32" color="#ff6600"><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
        <div class="link-card" @click="$router.push('/favorites')">
          <el-icon :size="32" color="#ff6600"><Star /></el-icon>
          <span>我的收藏</span>
        </div>
        <div class="link-card" @click="$router.push('/coupons')">
          <el-icon :size="32" color="#ff6600"><Ticket /></el-icon>
          <span>优惠券</span>
        </div>
        <div class="link-card" @click="$router.push('/points')">
          <el-icon :size="32" color="#ff6600"><Trophy /></el-icon>
          <span>我的积分</span>
        </div>
        <div class="link-card" @click="$router.push('/address')">
          <el-icon :size="32" color="#ff6600"><Location /></el-icon>
          <span>收货地址</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserFilled } from '@element-plus/icons-vue'

const userInfo = ref(null)

onMounted(() => {
  const info = localStorage.getItem('userInfo')
  if (info) {
    userInfo.value = JSON.parse(info)
  }
})
</script>

<style scoped>
.profile-page { padding-bottom: 40px; }
.page-title { font-size: 22px; font-weight: bold; margin-bottom: 20px; padding-left: 12px; border-left: 4px solid #ff6600; }
.profile-content { display: flex; gap: 24px; }
.profile-card { flex: 1; background: #fff; border-radius: 12px; padding: 30px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); text-align: center; }
.avatar-section { margin-bottom: 24px; }
.avatar-section h3 { margin-top: 12px; font-size: 18px; }
.info-section { text-align: left; max-width: 400px; margin: 0 auto; }
.info-item { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.info-item .label { color: #999; }
.quick-links { width: 280px; display: flex; flex-direction: column; gap: 16px; }
.link-card { background: #fff; border-radius: 12px; padding: 20px; display: flex; align-items: center; gap: 12px; cursor: pointer; transition: all 0.3s; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.link-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.12); }
</style>
