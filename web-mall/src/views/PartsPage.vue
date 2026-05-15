<template>
  <div class="parts-page">
    <div class="vehicle-header">
      <div class="vehicle-info-bar">
        <div class="brand-logo">
          <img v-if="vehicle?.imageUrl" :src="getFirstImage(vehicle.imageUrl)" alt="brand" />
          <span v-else class="brand-icon">{{ vehicle?.brandName?.charAt(0) || '?' }}</span>
        </div>
        <div class="vehicle-details">
          <div class="vehicle-title">
            {{ vehicle?.brandName }} {{ vehicle?.manufacturer ? vehicle.manufacturer + '(' + vehicle.brandName + ')' : '' }} {{ vehicle?.seriesName }} {{ vehicle?.displacement || '' }} {{ vehicle?.powerType || '' }}
          </div>
          <div class="vin-row">
            <span class="vin-code">{{ vin }}</span>
            <el-icon class="copy-icon" @click="copyVin"><CopyDocument /></el-icon>
            <el-icon class="fav-icon"><Star /></el-icon>
          </div>
        </div>
        <div class="switch-btn">
          <el-button size="small" plain>切换</el-button>
        </div>
      </div>
    </div>

    <div class="main-layout">
      <div class="category-sidebar">
        <div
          v-for="cat in categories"
          :key="cat"
          class="category-item"
          :class="{ active: activeCategory === cat }"
          @click="activeCategory = cat"
        >
          {{ cat }}
        </div>
      </div>

      <div class="parts-content">
        <template v-if="currentParts.length > 0">
          <div class="year-selector">
            <span class="year-label">{{ vehicle?.year }}款</span>
          </div>

          <div v-for="(group, groupName) in groupedParts" :key="groupName" class="part-group">
            <div class="group-header">
              <span class="group-title">{{ groupName }}</span>
            </div>
            <div class="part-cards">
              <div v-for="part in group" :key="part.partId" class="part-card">
                <div class="part-image">
                  <el-image
                    v-if="part.imageUrl"
                    :src="part.imageUrl"
                    fit="contain"
                    class="part-img"
                  />
                  <div v-else class="part-img-placeholder">
                    <el-icon :size="40"><Box /></el-icon>
                  </div>
                </div>
                <div class="part-info">
                  <div class="part-name-row">
                    <span class="part-name">{{ part.name }}</span>
                    <div class="part-actions">
                      <el-icon><DocumentCopy /></el-icon>
                      <el-icon><MoreFilled /></el-icon>
                    </div>
                  </div>
                  <div v-if="part.subName" class="part-subname">{{ part.subName }}</div>
                  <div v-if="part.specDetails && Object.keys(part.specDetails).length > 0" class="part-specs-table">
                    <div v-for="(val, key) in part.specDetails" :key="key" class="spec-table-row">
                      <span class="spec-table-key">{{ key }}</span>
                      <span class="spec-table-val">{{ val }}</span>
                    </div>
                  </div>
                  <div v-if="part.specs && (!part.specDetails || Object.keys(part.specDetails).length === 0)" class="part-specs-simple">
                    {{ part.specs }}
                  </div>
                  <div v-if="part.maintenanceInfo && Object.keys(part.maintenanceInfo).length > 0" class="maintenance-info">
                    <div class="maintenance-title">养护指导</div>
                    <div class="maintenance-table">
                      <div v-for="(val, key) in part.maintenanceInfo" :key="key" class="maintenance-row">
                        <span class="maintenance-key">{{ key }}</span>
                        <span class="maintenance-val">{{ val }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="compatible-info">
                    <span class="compatible-label">适配车型：</span>
                    <span class="compatible-value">{{ compatibleText }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <el-empty v-else description="暂无该分类的配件" />
        <div class="disclaimer">
          <p>搜配提供IT技术支持</p>
          <p>免责声明：图片和数据仅供参考，如有不符，欢迎反馈</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CopyDocument, Star, DocumentCopy, MoreFilled, Box } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const vin = ref(route.params.vin)

const vehicle = ref(null)
const allParts = ref({})
const categories = ref([])
const activeCategory = ref('')

const currentParts = computed(() => allParts.value[activeCategory.value] || [])

const groupedParts = computed(() => {
  const groups = {}
  currentParts.value.forEach(part => {
    const key = part.subName || part.category
    if (!groups[key]) groups[key] = []
    groups[key].push(part)
  })
  return groups
})

const compatibleText = computed(() => {
  if (!vehicle.value) return ''
  const parts = []
  if (vehicle.value.year) parts.push(vehicle.value.year + '款')
  if (vehicle.value.engineModel) parts.push('发动机型号:' + vehicle.value.engineModel)
  return parts.join(' / ')
})

const copyVin = () => {
  navigator.clipboard.writeText(vin.value)
  ElMessage.success('VIN码已复制')
}

const getFirstImage = (url) => {
  if (!url) return ''
  return url.split(',')[0]
}

onMounted(async () => {
  try {
    const res = await request.get('/vehicle/decode-with-parts', {
      params: { vin: vin.value }
    })
    if (res.code === 200) {
      vehicle.value = res.data.vehicle
      allParts.value = res.data.categories || {}
      categories.value = Object.keys(res.data.categories || {}).sort()
      if (categories.value.length > 0) {
        activeCategory.value = categories.value[0]
      }
    } else {
      ElMessage.error(res.message || '加载配件数据失败')
    }
  } catch (error) {
    ElMessage.error('加载配件数据失败')
  }
})
</script>

<style scoped>
.parts-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.vehicle-header {
  background: #1a237e;
  color: #fff;
  padding: 12px 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.vehicle-info-bar {
  display: flex;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.brand-logo {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  overflow: hidden;
}

.brand-logo img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand-icon {
  font-size: 24px;
  font-weight: bold;
  color: #1a237e;
}

.vehicle-details {
  flex: 1;
}

.vehicle-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 500px;
}

.vin-row {
  display: flex;
  align-items: center;
  font-size: 13px;
  opacity: 0.9;
}

.vin-code {
  font-family: monospace;
  letter-spacing: 1px;
  font-size: 14px;
}

.copy-icon,
.fav-icon {
  margin-left: 8px;
  cursor: pointer;
  font-size: 16px;
}

.switch-btn {
  margin-left: 16px;
}

.main-layout {
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 80px);
}

.category-sidebar {
  width: 140px;
  background: #fff;
  border-right: 1px solid #e0e0e0;
  padding: 8px 0;
  flex-shrink: 0;
}

.category-item {
  padding: 12px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}

.category-item:hover {
  background: #f5f5f5;
}

.category-item.active {
  background: #e8eaf6;
  border-left-color: #1a237e;
  color: #1a237e;
  font-weight: 600;
}

.parts-content {
  flex: 1;
  padding: 16px 20px;
  background: #fff;
}

.year-selector {
  padding: 8px 0 16px;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 16px;
}

.year-label {
  font-size: 14px;
  font-weight: 600;
  color: #1a237e;
  padding-left: 8px;
  border-left: 3px solid #1a237e;
}

.part-group {
  margin-bottom: 24px;
}

.group-header {
  padding: 8px 0;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 12px;
}

.group-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a237e;
  padding-left: 8px;
  border-left: 3px solid #1a237e;
}

.part-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.part-card {
  display: flex;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}

.part-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.part-image {
  width: 100px;
  height: 100px;
  margin-right: 16px;
  flex-shrink: 0;
}

.part-img {
  width: 100%;
  height: 100%;
}

.part-img-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 4px;
  color: #999;
}

.part-info {
  flex: 1;
}

.part-name-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 4px;
}

.part-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.part-actions {
  display: flex;
  gap: 8px;
  color: #999;
  cursor: pointer;
}

.part-actions .el-icon {
  font-size: 18px;
}

.part-subname {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.part-specs-table {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px 16px;
  margin-bottom: 8px;
}

.spec-table-row {
  display: contents;
}

.spec-table-key {
  font-size: 13px;
  color: #666;
}

.spec-table-val {
  font-size: 13px;
  color: #333;
}

.part-specs-simple {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.compatible-info {
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 4px;
  margin-top: 8px;
  font-size: 13px;
}

.compatible-label {
  color: #999;
}

.compatible-value {
  color: #333;
}

.maintenance-info {
  background: #fff8e1;
  border: 1px solid #ffe082;
  border-radius: 4px;
  padding: 10px 12px;
  margin-top: 8px;
}

.maintenance-title {
  font-size: 13px;
  font-weight: 600;
  color: #f57c00;
  margin-bottom: 6px;
}

.maintenance-table {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 4px 16px;
}

.maintenance-row {
  display: contents;
}

.maintenance-key {
  font-size: 12px;
  color: #666;
}

.maintenance-val {
  font-size: 12px;
  color: #333;
}

.disclaimer {
  text-align: center;
  padding: 20px 0;
  color: #999;
  font-size: 12px;
}

.disclaimer p {
  margin: 4px 0;
}
</style>
