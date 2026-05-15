<template>
  <div class="insurance-page">
    <div class="page-header">
      <h2>保险/年检服务</h2>
      <el-button type="primary" @click="showDialog = true">申请服务</el-button>
    </div>

    <el-card v-loading="loading">
      <el-empty v-if="services.length === 0" description="暂无服务记录" />
      <el-table v-else :data="services" style="width: 100%">
        <el-table-column prop="vehicleInfo" label="车辆信息" />
        <el-table-column label="服务类型" width="120">
          <template #default="{ row }">
            {{ row.serviceType === 1 ? '车险报价' : row.serviceType === 2 ? '年检代办' : '违章查询' }}
          </template>
        </el-table-column>
        <el-table-column label="报价" width="120">
          <template #default="{ row }">
            <span v-if="row.quotedPrice" style="color: #ff6600; font-weight: bold">¥{{ row.quotedPrice }}</span>
            <span v-else style="color: #999">待报价</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已处理' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="申请服务" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType" style="width: 100%">
            <el-option label="车险报价" :value="1" />
            <el-option label="年检代办" :value="2" />
            <el-option label="违章查询" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="VIN码">
          <el-input v-model="form.vehicleVin" placeholder="车辆VIN码" />
        </el-form-item>
        <el-form-item label="车辆信息">
          <el-input v-model="form.vehicleInfo" placeholder="如：宝马X3 2021款" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const services = ref([])
const showDialog = ref(false)
const form = ref({ serviceType: 1, vehicleVin: '', vehicleInfo: '', contactName: '', contactPhone: '', remark: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/insurance/list')
    services.value = res.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleApply = async () => {
  try {
    await request.post('/insurance/apply', form.value)
    ElMessage.success('申请已提交')
    showDialog.value = false
    form.value = { serviceType: 1, vehicleVin: '', vehicleInfo: '', contactName: '', contactPhone: '', remark: '' }
    loadData()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString() + ' ' + new Date(time).toLocaleTimeString().slice(0, 5)
}

onMounted(() => loadData())
</script>

<style scoped>
.insurance-page {
  max-width: 900px;
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