<template>
  <div class="service-appointment">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预约修车服务</span>
        </div>
      </template>

      <el-form :model="form" label-width="100px" style="max-width: 600px">
        <el-form-item label="服务类型">
          <el-select v-model="form.serviceType" placeholder="请选择服务类型">
            <el-option 
              v-for="type in serviceTypes" 
              :key="type.id" 
              :label="type.name" 
              :value="type.name" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="汽车品牌">
          <el-input v-model="form.vehicleBrand" placeholder="如：丰田、大众" />
        </el-form-item>
        <el-form-item label="车型">
          <el-input v-model="form.vehicleModel" placeholder="如：凯美瑞、速腾" />
        </el-form-item>
        <el-form-item label="年款">
          <el-input v-model="form.vehicleYear" placeholder="如：2023款" />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="form.licensePlate" placeholder="如：京A12345" />
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker
            v-model="form.appointmentTime"
            type="datetime"
            placeholder="选择预约时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请描述车辆问题或特殊需求" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitAppointment" :loading="submitting">提交预约</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>我的预约</span>
      </template>

      <el-table :data="myAppointments" v-loading="loading">
        <el-table-column prop="orderNo" label="预约单号" />
        <el-table-column prop="serviceType" label="服务类型" />
        <el-table-column prop="appointmentTime" label="预约时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待确认</el-tag>
            <el-tag v-else-if="row.status === 'confirmed'" type="primary">已确认</el-tag>
            <el-tag v-else-if="row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 'cancelled'" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const submitting = ref(false)
const serviceTypes = ref([])
const myAppointments = ref([])

const form = reactive({
  serviceType: '',
  vehicleBrand: '',
  vehicleModel: '',
  vehicleYear: '',
  licensePlate: '',
  appointmentTime: '',
  remark: ''
})

const loadServiceTypes = async () => {
  try {
    const res = await request.get('/service-order/types')
    serviceTypes.value = res.data
  } catch (e) {
    console.error('加载服务类型失败', e)
  }
}

const loadMyAppointments = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) return
  
  loading.value = true
  try {
    const res = await request.get('/service-order/my', {
      params: { pageNum: 1, pageSize: 10 }
    })
    myAppointments.value = res.data.records
  } catch (e) {
    console.error('加载预约记录失败', e)
  } finally {
    loading.value = false
  }
}

const submitAppointment = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) {
    ElMessage.warning('请先登录')
    return
  }
  if (!form.serviceType) {
    ElMessage.warning('请选择服务类型')
    return
  }
  if (!form.appointmentTime) {
    ElMessage.warning('请选择预约时间')
    return
  }

  const userInfoStr = localStorage.getItem('userInfo')
  const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null
  const userId = userInfo?.id
  const userName = userInfo?.nickname || userInfo?.phone || '当前用户'
  const userPhone = userInfo?.phone || ''

  submitting.value = true
  try {
    const data = {
      userId: userId,
      userName: userName,
      userPhone: userPhone,
      serviceType: form.serviceType,
      vehicleBrand: form.vehicleBrand,
      vehicleModel: form.vehicleModel,
      vehicleYear: form.vehicleYear,
      licensePlate: form.licensePlate,
      appointmentTime: form.appointmentTime,
      remark: form.remark
    }
    await request.post('/service-order', data)
    ElMessage.success('预约成功')
    form.serviceType = ''
    form.vehicleBrand = ''
    form.vehicleModel = ''
    form.vehicleYear = ''
    form.licensePlate = ''
    form.appointmentTime = ''
    form.remark = ''
    loadMyAppointments()
  } catch (e) {
    ElMessage.error('预约失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadServiceTypes()
  loadMyAppointments()
})
</script>

<style scoped>
.service-appointment {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
