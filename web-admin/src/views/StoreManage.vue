<template>
  <div class="admin-stores">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>门店管理</span>
          <el-button type="primary" @click="handleAdd">添加门店</el-button>
        </div>
      </template>
      <el-table :data="stores" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="门店名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="businessHours" label="营业时间" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑门店' : '添加门店'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="门店名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="营业时间">
          <el-input v-model="form.businessHours" placeholder="如：08:00-20:00" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const stores = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({ name: '', address: '', phone: '', businessHours: '', status: 1 })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/stores')
    stores.value = res.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { name: '', address: '', phone: '', businessHours: '', status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    if (isEdit.value) {
      await request.put('/admin/store/' + form.value.id, form.value)
    } else {
      await request.post('/admin/store', form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该门店？', '提示', { type: 'warning' })
  await request.delete('/admin/store/' + id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>