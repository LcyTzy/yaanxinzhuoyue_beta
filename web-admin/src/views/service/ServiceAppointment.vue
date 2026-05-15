<template>
  <div class="service-appointment-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>服务预约管理</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="预约单号" />
        <el-table-column prop="userName" label="客户姓名" />
        <el-table-column prop="userPhone" label="联系电话" />
        <el-table-column prop="serviceType" label="服务类型" />
        <el-table-column prop="vehicleBrand" label="品牌" />
        <el-table-column prop="vehicleModel" label="车型" />
        <el-table-column prop="licensePlate" label="车牌号" />
        <el-table-column prop="appointmentTime" label="预约时间" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'pending'" type="warning">待确认</el-tag>
            <el-tag v-else-if="row.status === 'confirmed'" type="primary">已确认</el-tag>
            <el-tag v-else-if="row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 'cancelled'" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'pending'"
              size="small" 
              type="success" 
              @click="confirmAppointment(row)"
            >确认</el-button>
            <el-button 
              v-if="row.status === 'confirmed'"
              size="small" 
              type="primary" 
              @click="completeAppointment(row)"
            >完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailDialogVisible" title="预约详情" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="预约单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户姓名">{{ detailData.userName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.userPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ detailData.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailData.vehicleBrand || '-' }}</el-descriptions-item>
        <el-descriptions-item label="车型">{{ detailData.vehicleModel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="年款">{{ detailData.vehicleYear || '-' }}</el-descriptions-item>
        <el-descriptions-item label="车牌号">{{ detailData.licensePlate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ detailData.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
            <el-tag v-if="detailData.status === 'pending'" type="warning">待确认</el-tag>
            <el-tag v-else-if="detailData.status === 'confirmed'" type="primary">已确认</el-tag>
            <el-tag v-else-if="detailData.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="detailData.status === 'cancelled'" type="danger">已取消</el-tag>
            <span v-else>{{ detailData.status }}</span>
          </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])
const detailDialogVisible = ref(false)
const detailData = ref(null)

const queryForm = reactive({
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      status: queryForm.status || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await request.get('/admin/service-order/page', { params })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = async (row) => {
  try {
    const res = await request.get(`/admin/service-order/${row.id}`)
    detailData.value = res.data
    detailDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

const confirmAppointment = async (row) => {
  try {
    await ElMessageBox.confirm('确认此预约？', '提示', {
      type: 'warning'
    })
    await request.put(`/admin/service-order/${row.id}/confirm`)
    ElMessage.success('确认成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('确认失败')
    }
  }
}

const completeAppointment = async (row) => {
  try {
    await ElMessageBox.confirm('标记此预约为已完成？', '提示', {
      type: 'warning'
    })
    await request.put(`/admin/service-order/${row.id}/complete`)
    ElMessage.success('完成成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.service-appointment-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}
</style>
