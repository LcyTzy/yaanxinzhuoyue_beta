<template>
  <div class="vin-query-page">
    <el-card class="query-card">
      <template #header>
        <span class="card-title">VIN码查配件</span>
      </template>
      <div class="query-bar">
        <el-input
          v-model="vin"
          placeholder="请输入17位VIN码"
          maxlength="17"
          style="width: 320px"
          @keyup.enter="handleQuery"
        >
          <template #append>
            <el-button @click="handleQuery" :loading="loading">查询</el-button>
          </template>
        </el-input>
      </div>
    </el-card>

    <div v-if="vehicle" v-loading="loading" class="result-area">
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
      </el-card>

      <el-card v-if="matchedParts && matchedParts.length > 0" class="parts-card">
        <template #header>适用配件（{{ matchedParts.length }}件）</template>
        <el-table :data="matchedParts" stripe>
          <el-table-column prop="code" label="编号" width="120" />
          <el-table-column prop="name" label="配件名称" />
          <el-table-column prop="oem" label="OE号" width="140" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">¥{{ row.price }}</template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
        </el-table>
      </el-card>
      <el-empty v-else-if="vehicle" description="暂未找到适配配件" />
    </div>

    <el-empty v-if="!vehicle && !loading" description="输入VIN码查询车辆及适配配件" />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const vin = ref('')
const vehicle = ref(null)
const matchedParts = ref([])
const loading = ref(false)

const handleQuery = async () => {
  if (!vin.value || vin.value.length !== 17) {
    ElMessage.warning('请输入17位VIN码')
    return
  }
  loading.value = true
  vehicle.value = null
  matchedParts.value = []
  try {
    const res = await request.get('/vehicle/decode', { params: { vin: vin.value } })
    if (res.code === 200) {
      vehicle.value = res.data.vehicle
      matchedParts.value = res.data.matchedParts || []
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (e) {
    ElMessage.error('查询失败，请检查VIN码是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.vin-query-page {
  padding: 20px;
}
.query-card {
  margin-bottom: 20px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
}
.query-bar {
  display: flex;
  align-items: center;
}
.result-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.vehicle-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>