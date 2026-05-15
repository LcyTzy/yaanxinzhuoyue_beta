<template>
  <div class="category-manage">
    <el-button type="primary" @click="handleAdd">添加分类</el-button>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px" row-key="id" :tree-props="{ children: 'children' }">
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="level" label="层级" width="80">
        <template #default="{ row }">
          <el-tag :type="row.level === 1 ? 'primary' : 'success'">{{ row.level === 1 ? '一级' : '二级' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="上级分类">
          <el-select v-model="editForm.parentId" placeholder="无上级分类（一级分类）" clearable>
            <el-option v-for="item in parentOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="editForm.icon" placeholder="图标类名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryTree, addCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加分类')
const editFormRef = ref(null)

const editForm = reactive({
  id: null,
  name: '',
  parentId: null,
  level: 1,
  sort: 0,
  icon: '',
  status: 1
})

const editRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const parentOptions = computed(() => {
  return tableData.value.filter(item => item.level === 1)
})

const getData = async () => {
  loading.value = true
  try {
    const res = await getCategoryTree()
    tableData.value = res.data
  } catch (error) {
    console.error('获取分类树失败', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  Object.assign(editForm, { id: null, name: '', parentId: null, level: 1, sort: 0, icon: '', status: 1 })
  dialogTitle.value = '添加分类'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(editForm, row)
  dialogTitle.value = '编辑分类'
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (editForm.id) {
      await updateCategory(editForm)
    } else {
      await addCategory(editForm)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    getData()
  } catch (error) {
    console.error('保存失败', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    getData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
    }
  }
}

onMounted(() => {
  getData()
})
</script>

<style scoped>
.category-manage {
  padding: 20px;
}
</style>
