<template>
  <div class="banner-manage">
    <div class="page-header">
      <h2 class="page-title">轮播图管理</h2>
      <p class="page-subtitle">管理商城首页轮播图，支持图片上传和排序</p>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增轮播图
      </el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <el-image v-if="row.image" :src="row.image" fit="cover" style="width: 160px; height: 60px; border-radius: 4px" />
          <span v-else style="color: #999">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="link" label="链接" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="editForm" ref="editFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入轮播图标题" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-upload
            action="/api/upload/image"
            list-type="picture-card"
            :file-list="imageFileList"
            :headers="uploadHeaders"
            :on-success="handleImageSuccess"
            :on-error="handleImageError"
            :on-remove="handleImageRemove"
            :limit="1"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸：1920×600px，支持jpg/png/webp</div>
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="editForm.link" placeholder="点击跳转链接（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sort" :min="0" :max="99" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="editForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增轮播图')
const editFormRef = ref(null)

const editForm = reactive({
  id: null,
  title: '',
  image: '',
  link: '',
  sort: 0,
  status: 1
})

const imageFileList = ref([])

const uploadHeaders = {
  Authorization: 'Bearer ' + localStorage.getItem('adminToken')
}

const getData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/banners')
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取轮播图列表失败', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  editForm.id = null
  editForm.title = ''
  editForm.image = ''
  editForm.link = ''
  editForm.sort = 0
  editForm.status = 1
  imageFileList.value = []
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增轮播图'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  dialogTitle.value = '编辑轮播图'
  editForm.id = row.id
  editForm.title = row.title || ''
  editForm.image = row.image || ''
  editForm.link = row.link || ''
  editForm.sort = row.sort || 0
  editForm.status = row.status != null ? row.status : 1
  if (row.image) {
    imageFileList.value = [{ name: 'banner', url: row.image }]
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该轮播图吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await request.delete(`/admin/banner/${row.id}`)
      ElMessage.success('删除成功')
      getData()
    } catch (error) {
      console.error('删除失败', error)
    }
  }).catch(() => {})
}

const handleImageSuccess = (response, file) => {
  editForm.image = response.data || response
  ElMessage.success('图片上传成功')
}

const handleImageError = () => {
  ElMessage.error('图片上传失败，请检查网络或文件格式')
}

const handleImageRemove = () => {
  editForm.image = ''
}

const handleSave = async () => {
  if (!editForm.image) {
    ElMessage.warning('请上传轮播图图片')
    return
  }
  try {
    if (editForm.id) {
      await request.put('/admin/banner', { ...editForm })
      ElMessage.success('更新成功')
    } else {
      await request.post('/admin/banner', { ...editForm })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getData()
  } catch (error) {
    console.error('保存失败', error)
  }
}

onMounted(() => {
  getData()
})
</script>

<style scoped>
.banner-manage {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 22px;
  font-weight: 600;
  color: #1d1e4c;
  margin: 0 0 6px 0;
}

.page-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.toolbar {
  display: flex;
  justify-content: flex-end;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
}
</style>