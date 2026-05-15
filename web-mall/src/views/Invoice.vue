<template>
  <div class="invoice-page">
    <div class="page-header">
      <h2>发票管理</h2>
      <el-button type="primary" @click="showDialog = true">申请发票</el-button>
    </div>

    <el-card v-loading="loading">
      <el-empty v-if="invoices.length === 0" description="暂无发票" />
      <el-table v-else :data="invoices" style="width: 100%">
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
              {{ row.status === 1 ? '已开票' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="申请发票" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="订单号">
          <el-input v-model="form.orderNo" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="发票类型">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">电子发票</el-radio>
            <el-radio :value="2">纸质发票</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发票抬头">
          <el-input v-model="form.title" placeholder="公司名称或个人姓名" />
        </el-form-item>
        <el-form-item label="税号">
          <el-input v-model="form.taxNo" placeholder="企业税号（个人可不填）" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="接收电子发票的邮箱" />
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
const invoices = ref([])
const showDialog = ref(false)
const form = ref({ orderNo: '', type: 1, title: '', taxNo: '', amount: null, email: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/invoice/list')
    invoices.value = res.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleApply = async () => {
  try {
    await request.post('/invoice/apply', form.value)
    ElMessage.success('申请已提交')
    showDialog.value = false
    form.value = { orderNo: '', type: 1, title: '', taxNo: '', amount: null, email: '' }
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
.invoice-page {
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