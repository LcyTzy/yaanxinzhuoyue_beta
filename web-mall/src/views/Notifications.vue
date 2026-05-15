<template>
  <div class="notifications-page">
    <div class="page-header">
      <h2>消息通知</h2>
      <el-button type="primary" link @click="markAllRead" v-if="unreadCount > 0">全部已读</el-button>
    </div>

    <el-card v-loading="loading">
      <div v-if="list.length === 0" class="empty-state">
        <el-empty description="暂无通知" />
      </div>
      <div v-else class="notification-list">
        <div 
          v-for="item in list" 
          :key="item.id" 
          class="notification-item"
          :class="{ unread: item.isRead === 0 }"
          @click="handleClick(item)"
        >
          <div class="notif-icon">
            <el-icon v-if="item.type === 1" :size="20"><Goods /></el-icon>
            <el-icon v-else-if="item.type === 2" :size="20"><Money /></el-icon>
            <el-icon v-else-if="item.type === 4" :size="20"><Present /></el-icon>
            <el-icon v-else :size="20"><Bell /></el-icon>
          </div>
          <div class="notif-content">
            <div class="notif-title">
              {{ item.title }}
              <el-badge v-if="item.isRead === 0" is-dot class="unread-dot" />
            </div>
            <div class="notif-text">{{ item.content }}</div>
            <div class="notif-time">{{ formatTime(item.createTime) }}</div>
          </div>
        </div>
      </div>

      <el-pagination
        v-if="total > 0"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20]"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const unreadCount = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/notification/page', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    list.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await request.get('/notification/unread-count')
    unreadCount.value = res.data.count
  } catch (e) {
    // ignore
  }
}

const handleClick = async (item) => {
  if (item.isRead === 0) {
    try {
      await request.put(`/notification/${item.id}/read`)
      item.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
    } catch (e) {
      // ignore
    }
  }
  if (item.type === 1 || item.type === 2) {
    router.push('/orders')
  }
}

const markAllRead = async () => {
  try {
    await request.put('/notification/read-all')
    list.value.forEach(item => item.isRead = 1)
    unreadCount.value = 0
    ElMessage.success('已全部标记为已读')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString().slice(0, 5)
}

onMounted(() => {
  loadData()
  loadUnreadCount()
})
</script>

<style scoped>
.notifications-page {
  max-width: 800px;
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

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #ecf5ff;
}

.notif-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409EFF;
  flex-shrink: 0;
}

.notif-content {
  flex: 1;
  min-width: 0;
}

.notif-title {
  font-weight: 600;
  font-size: 15px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.unread-dot {
  flex-shrink: 0;
}

.notif-text {
  color: #606266;
  font-size: 14px;
  margin-top: 4px;
}

.notif-time {
  color: #909399;
  font-size: 12px;
  margin-top: 6px;
}

.empty-state {
  padding: 60px 0;
}
</style>