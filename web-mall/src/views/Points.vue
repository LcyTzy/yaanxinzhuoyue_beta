<template>
  <div class="points-page">
    <h2 class="page-title">我的积分</h2>
    
    <div class="points-summary">
      <div class="points-card">
        <div class="points-icon">🎁</div>
        <div class="points-info">
          <h3>当前积分</h3>
          <div class="points-value">{{ userPoints || 0 }}</div>
          <p>积分可用于兑换优惠券、抵扣现金等</p>
        </div>
      </div>
    </div>

    <div class="points-rules">
      <h3>积分规则</h3>
      <ul>
        <li>每消费1元获得1积分</li>
        <li>积分可用于抵扣订单金额</li>
        <li>积分有效期为1年</li>
        <li>退货时相应扣除积分</li>
      </ul>
    </div>

    <div class="points-log">
      <h3>积分记录</h3>
      <el-table :data="pointsLogs" v-loading="loading" stripe>
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="points" label="积分变化" width="120">
          <template #default="{ row }">
            <span :class="row.points > 0 ? 'points-add' : 'points-minus'">
              {{ row.points > 0 ? '+' : '' }}{{ row.points }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
        style="margin-top: 20px; justify-content: center"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const userPoints = ref(0)
const pointsLogs = ref([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)

const loadUserPoints = async () => {
  try {
    const res = await request.get('/user/info')
    userPoints.value = res.data?.points || 0
  } catch (e) {
    console.error('加载用户积分失败', e)
  }
}

const loadPointsLog = async () => {
  loading.value = true
  try {
    const res = await request.get('/points/log', {
      params: {
        pageNum: currentPage.value,
        pageSize: 10
      }
    })
    pointsLogs.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载积分记录失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadPointsLog()
}

onMounted(() => {
  loadUserPoints()
  loadPointsLog()
})
</script>

<style scoped>
.points-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.points-summary {
  margin-bottom: 24px;
}

.points-card {
  background: linear-gradient(135deg, #ff6600 0%, #ff8533 100%);
  border-radius: 16px;
  padding: 32px;
  display: flex;
  align-items: center;
  gap: 24px;
  color: #fff;
}

.points-icon {
  font-size: 64px;
}

.points-info h3 {
  margin: 0 0 8px;
  font-size: 16px;
  opacity: 0.9;
}

.points-value {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 8px;
}

.points-info p {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

.points-rules {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.points-rules h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.points-rules ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.points-rules li {
  margin-bottom: 8px;
}

.points-log {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.points-log h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.points-add {
  color: #52c41a;
  font-weight: bold;
}

.points-minus {
  color: #ff4d4f;
  font-weight: bold;
}
</style>
