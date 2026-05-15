<template>
  <div class="admin-inquiries">
    <el-card>
      <template #header><span>询价管理</span></template>
      <el-table :data="inquiries" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="oeNumber" label="OE号" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已回复' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" @click="handleReply(row)">回复报价</el-button>
            <span v-else>报价：¥{{ row.quotedPrice }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="回复报价" width="400px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="报价金额">
          <el-input-number v-model="form.quotedPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="form.reply" type="textarea" />
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
const inquiries = ref([])
const dialogVisible = ref(false)
const currentId = ref(null)
const form = ref({ quotedPrice: null, reply: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/inquiry/list')
    inquiries.value = res.data
  } finally {
    loading.value = false
  }
}

const handleReply = (row) => {
  currentId.value = row.id
  form.value = { quotedPrice: null, reply: '' }
  dialogVisible.value = true
}

const handleConfirm = async () => {
  try {
    await request.put('/admin/inquiry/' + currentId.value + '/reply', form.value)
    ElMessage.success('回复成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('回复失败')
  }
}

onMounted(() => loadData())
</script>