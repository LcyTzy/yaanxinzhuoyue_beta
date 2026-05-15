<template>
  <el-dialog v-model="visible" title="VIN查配件" width="700px">
    <div class="query-bar">
      <el-input
        v-model="vin"
        placeholder="请输入17位VIN码"
        maxlength="17"
        @keyup.enter="handleQuery"
      >
        <template #append>
          <el-button @click="handleQuery" :loading="loading">查询</el-button>
        </template>
      </el-input>
    </div>

    <div v-if="vehicle" class="vin-result" v-loading="loading">
      <el-card class="vehicle-card">
        <template #header>
          <div class="card-header">
            <span>车辆信息：{{ vehicle.name || vehicle.seriesName }}</span>
            <el-tag type="success" size="small">探数API</el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="品牌">{{ vehicle.brandName }}</el-descriptions-item>
          <el-descriptions-item label="厂商">{{ vehicle.manufacturer }}</el-descriptions-item>
          <el-descriptions-item label="车系">{{ vehicle.seriesName }}</el-descriptions-item>
          <el-descriptions-item label="年款">{{ vehicle.year }}</el-descriptions-item>
          <el-descriptions-item label="车型名称">{{ vehicle.name }}</el-descriptions-item>
          <el-descriptions-item label="销售版本">{{ vehicle.saleState }}</el-descriptions-item>
          <el-descriptions-item label="燃料形式">{{ vehicle.powerType }}</el-descriptions-item>
          <el-descriptions-item label="排量">{{ vehicle.displacement }}L</el-descriptions-item>
          <el-descriptions-item label="变速箱">{{ vehicle.gearbox }}</el-descriptions-item>
          <el-descriptions-item label="驱动方式">{{ vehicle.drivenType }}</el-descriptions-item>
          <el-descriptions-item label="排放标准">{{ vehicle.effluentStandard }}</el-descriptions-item>
          <el-descriptions-item label="发动机型号">{{ vehicle.engineModel }}</el-descriptions-item>
          <el-descriptions-item label="指导价">{{ vehicle.price }}</el-descriptions-item>
          <el-descriptions-item label="核定载客">{{ vehicle.zws }}</el-descriptions-item>
          <el-descriptions-item label="长×宽×高">{{ vehicle.size }}</el-descriptions-item>
          <el-descriptions-item label="轴距">{{ vehicle.wheelbase }}mm</el-descriptions-item>
          <el-descriptions-item label="上市时间">{{ vehicle.marketDate }}</el-descriptions-item>
          <el-descriptions-item label="车辆级别">{{ vehicle.scale }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="vehicle.modelList && vehicle.modelList.length > 0" style="margin-top: 12px">
          <el-tag type="info" style="margin-bottom: 6px">可选销售版本：</el-tag>
          <div v-for="m in vehicle.modelList" :key="m.cid" style="margin: 2px 0">
            <el-tag size="small" type="warning">{{ m.name }}</el-tag>
          </div>
        </div>
      </el-card>

      <el-card v-if="matchedParts && matchedParts.length > 0" class="parts-card">
        <template #header>适用配件（{{ matchedParts.length }}件）</template>
        <el-table :data="matchedParts" stripe>
          <el-table-column prop="code" label="编号" width="120" />
          <el-table-column prop="name" label="配件名称" />
          <el-table-column prop="oem" label="OE号" width="120" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button size="small" @click.stop="goToDetail(row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <div v-else-if="vehicle" class="no-parts">
        暂未找到适配配件
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
      <el-button v-if="vehicle" type="primary" @click="goToPartsPage">查看适配配件</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const visible = ref(false)
const vin = ref('')
const vehicle = ref(null)
const matchedParts = ref([])
const loading = ref(false)
const router = useRouter()

const open = () => {
  visible.value = true
  vin.value = ''
  vehicle.value = null
  matchedParts.value = []
}

const handleQuery = async () => {
  const vinCode = vin.value.trim().toUpperCase()
  if (vinCode.length !== 17) {
    ElMessage.warning('VIN码必须为17位')
    return
  }
  loading.value = true
  try {
    const res = await request.get('/vehicle/decode', {
      params: { vin: vinCode }
    })
    if (res.code === 200) {
      vehicle.value = res.data.vehicle
      matchedParts.value = res.data.matchedParts || []
      if (!res.data.vehicle) {
        ElMessage.info('未查询到该车辆信息')
      }
    } else {
      ElMessage.error(res.message || '查询失败')
      vehicle.value = null
      matchedParts.value = []
    }
  } catch (error) {
    ElMessage.error('查询失败，请稍后重试')
    vehicle.value = null
    matchedParts.value = []
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
  visible.value = false
}

const goToPartsPage = () => {
  const vinCode = vin.value.trim().toUpperCase()
  router.push(`/parts/${vinCode}`)
  visible.value = false
}

defineExpose({ open })
</script>

<style scoped>
.query-bar {
  display: flex;
  align-items: center;
}

.vin-result {
  margin-top: 20px;
}

.vehicle-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.remark {
  margin-top: 10px;
  color: #999;
  font-size: 12px;
}

.parts-card {
  margin-top: 20px;
}

.no-parts {
  margin-top: 20px;
  color: #999;
  text-align: center;
}
</style>
