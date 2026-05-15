<template>
  <div class="maintenance-page">
    <div class="page-header">
      <h2>保养档案</h2>
      <el-button type="primary" @click="showDialog = true">添加保养记录</el-button>
    </div>

    <el-card v-loading="loading">
      <el-empty v-if="records.length === 0" description="暂无保养记录" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="record in records"
          :key="record.id"
          :timestamp="record.serviceDate"
          placement="top"
        >
          <el-card>
            <h4>{{ record.serviceType }}</h4>
            <p>车辆：{{ record.vehicleInfo || record.vehicleVin }}</p>
            <p>里程：{{ record.mileage }} km</p>
            <p>门店：{{ record.storeName }}</p>
            <p v-if="record.cost">费用：¥{{ record.cost }}</p>
            <p v-if="record.description">{{ record.description }}</p>
            <p v-if="record.nextRemindDate" style="color: #ff6600">
              下次保养提醒：{{ record.nextRemindDate }}
            </p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-dialog v-model="showDialog" title="添加保养记录" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="VIN码">
          <el-input v-model="form.vehicleVin" placeholder="车辆VIN码" />
        </el-form-item>
        <el-form-item label="车辆信息">
          <el-input v-model="form.vehicleInfo" placeholder="如：宝马X3 2021款" />
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType" style="width: 100%">
            <el-option label="机油更换" value="机油更换" />
            <el-option label="刹车片更换" value="刹车片更换" />
            <el-option label="轮胎更换" value="轮胎更换" />
            <el-option label="空调滤清器更换" value="空调滤清器更换" />
            <el-option label="火花塞更换" value="火花塞更换" />
            <el-option label="变速箱油更换" value="变速箱油更换" />
            <el-option label="常规检查" value="常规检查" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="里程数">
          <el-input-number v-model="form.mileage" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务日期">
          <el-date-picker v-model="form.serviceDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="门店">
          <el-input v-model="form.storeName" placeholder="服务门店" />
        </el-form-item>
        <el-form-item label="费用">
          <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="下次保养">
          <el-date-picker v-model="form.nextRemindDate" type="date" style="width: 100%" placeholder="下次保养提醒日期" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const records = ref([])
const showDialog = ref(false)
const form = ref({
  vehicleVin: '',
  vehicleInfo: '',
  serviceType: '',
  mileage: null,
  serviceDate: null,
  storeName: '',
  cost: null,
  description: '',
  nextRemindDate: null
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/maintenance/records')
    records.value = res.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  try {
    await request.post('/maintenance/record', form.value)
    ElMessage.success('保存成功')
    showDialog.value = false
    form.value = { vehicleVin: '', vehicleInfo: '', serviceType: '', mileage: null, serviceDate: null, storeName: '', cost: null, description: '', nextRemindDate: null }
    loadData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '保存失败')
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.maintenance-page {
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
</style>