<template>
  <div class="inquiry-page">
    <div class="page-header">
      <h2>在线询价</h2>
      <el-button type="primary" @click="showDialog = true">提交询价</el-button>
    </div>

    <el-card v-loading="loading">
      <el-empty v-if="inquiries.length === 0" description="暂无询价记录" />
      <el-table v-else :data="inquiries" style="width: 100%">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="oeNumber" label="OE号" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="报价" width="120">
          <template #default="{ row }">
            <span v-if="row.quotedPrice" style="color: #ff6600; font-weight: bold">¥{{ row.quotedPrice }}</span>
            <span v-else style="color: #999">待报价</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已回复' : '处理中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reply" label="回复" min-width="150" />
        <el-table-column label="提交时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDialog" title="提交询价" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="商品名称">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="OE号">
          <el-input v-model="form.oeNumber" placeholder="配件OE号" />
        </el-form-item>
        <el-form-item label="VIN码">
          <el-input v-model="form.vinCode" placeholder="车辆VIN码" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="form.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.description" type="textarea" placeholder="补充说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交询价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const inquiries = ref([])
const showDialog = ref(false)
const form = ref({ productName: '', oeNumber: '', vinCode: '', quantity: 1, description: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/inquiry/list')
    inquiries.value = res.data
  } catch (e) {
    // ignore
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await request.post('/inquiry/submit', form.value)
    ElMessage.success('询价已提交')
    showDialog.value = false
    form.value = { productName: '', oeNumber: '', vinCode: '', quantity: 1, description: '' }
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
.inquiry-page {
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