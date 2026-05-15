<template>
  <div class="membership-page">
    <div class="page-header">
      <h2>会员中心</h2>
    </div>

    <el-card class="my-level-card" v-loading="loading">
      <div class="current-level" v-if="myLevel">
        <div class="level-badge" :class="'level-' + myLevel.level">
          <span class="level-icon">{{ myLevel.icon }}</span>
          <span class="level-name">{{ myLevel.name }}</span>
        </div>
        <div class="level-info">
          <p>当前折扣：<strong>{{ (myLevel.discount * 10).toFixed(1) }}折</strong></p>
          <p>{{ myLevel.description }}</p>
        </div>
      </div>
    </el-card>

    <el-card class="levels-card" style="margin-top: 20px">
      <template #header>会员等级说明</template>
      <div class="levels-grid">
        <div 
          v-for="level in levels" 
          :key="level.id" 
          class="level-item"
          :class="{ active: myLevel && level.level === myLevel.level }"
        >
          <div class="level-icon-large">{{ level.icon }}</div>
          <div class="level-name">{{ level.name }}</div>
          <div class="level-desc">{{ level.description }}</div>
          <div class="level-requirement">积分要求：{{ level.minPoints }}</div>
          <div class="level-discount">折扣：{{ (level.discount * 10).toFixed(1) }}折</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const loading = ref(false)
const myLevel = ref(null)
const levels = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const [levelRes, levelsRes] = await Promise.all([
      request.get('/membership/my-level'),
      request.get('/membership/levels')
    ])
    myLevel.value = levelRes.data.level
    levels.value = levelsRes.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.membership-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header h2 {
  margin: 0 0 20px;
  font-size: 22px;
}

.my-level-card {
  text-align: center;
}

.current-level {
  padding: 20px;
}

.level-badge {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 16px 32px;
  border-radius: 12px;
  font-size: 24px;
  font-weight: bold;
}

.level-badge.level-1 { background: #f5f5f5; color: #666; }
.level-badge.level-2 { background: #e8f5e9; color: #388e3c; }
.level-badge.level-3 { background: #fff3e0; color: #f57c00; }
.level-badge.level-4 { background: #e3f2fd; color: #1976d2; }
.level-badge.level-5 { background: #fce4ec; color: #c62828; }

.level-info {
  margin-top: 16px;
  color: #666;
}

.level-info strong {
  color: #ff6600;
  font-size: 18px;
}

.levels-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
}

.level-item {
  text-align: center;
  padding: 20px 12px;
  border: 2px solid #eee;
  border-radius: 12px;
  transition: all 0.3s;
}

.level-item.active {
  border-color: #ff6600;
  background: #fff7f0;
}

.level-item:hover {
  border-color: #ff6600;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,102,0,0.15);
}

.level-icon-large {
  font-size: 36px;
  margin-bottom: 8px;
}

.level-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 6px;
}

.level-desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.level-requirement {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.level-discount {
  font-size: 14px;
  color: #ff6600;
  font-weight: bold;
}
</style>