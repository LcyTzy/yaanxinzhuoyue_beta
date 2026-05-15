<template>
  <div class="admin-invoices">
    <el-card>
      <template #header><span>发票管理</span></template>
      <el-table :data="invoices" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="title" label="发票抬头" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ row.type === 1 ? '电子发票' : '纸质发票' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已开票' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="handleProcess(row)">处理开票</el-button>
            <el-button v-if="row.invoiceUrl" size="small" @click="window.open(row.invoiceUrl)">查看发票</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="处理开票" width="400px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="发票号">
          <el-input v-model="form.invoiceNo" />
        </el-form-item>
        <el-form-item label="发票链接">
          <el-input v-model="form.invoiceUrl" placeholder="电子发票下载链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const invoices = ref([])
const dialogVisible = ref(false)
const currentId = ref(null)
const form = ref({ invoiceNo: '', invoiceUrl: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/invoice/list')
    invoices.value = res.data
  } finally {
    loading.value = false
  }
}

const handleProcess = (row) => {
  currentId.value = row.id
  form.value = { invoiceNo: '', invoiceUrl: '' }
  dialogVisible.value = true
}

const handleConfirm = async () => {
  try {
    await request.put('/admin/invoice/' + currentId.value + '/process', form.value)
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('处理失败')
  }
}

onMounted(() => loadData())
</script>