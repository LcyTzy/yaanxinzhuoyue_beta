<template>
  <div class="part-relation-manage">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>车型选择</span>
          </template>
          <el-input v-model="modelSearch" placeholder="搜索车型" style="margin-bottom: 10px" @input="filterModels" />
          <el-tree
            :data="vehicleTree"
            :props="{ children: 'seriesList', label: 'name' }"
            node-key="id"
            highlight-current
            @node-click="selectModel"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span v-if="data.models" class="custom-tree-node">
                {{ node.label }}
                <el-tag size="small" type="info">{{ data.models.length }}</el-tag>
              </span>
              <span v-else>{{ node.label }}</span>
            </template>
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>配件关联 {{ selectedModel ? ` - ${selectedModel.name}` : '' }}</span>
              <el-button type="primary" size="small" @click="showAddDialog" :disabled="!selectedModel">添加配件</el-button>
            </div>
          </template>
          <el-table :data="relatedParts" v-loading="loading">
            <el-table-column prop="productId" label="配件ID" width="100" />
            <el-table-column prop="position" label="位置" width="120" />
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="removeRelation(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="addDialogVisible" title="添加配件" width="600px">
      <el-input v-model="partSearch" placeholder="搜索配件" style="margin-bottom: 10px" @input="searchParts" />
      <el-table :data="availableParts" @selection-change="handleSelectionChange" height="300">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="code" label="编号" width="120" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="oem" label="OE号" width="120" />
        <el-table-column prop="price" label="价格" width="80" />
      </el-table>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="batchAddParts">确定添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const vehicleTree = ref([])
const relatedParts = ref([])
const availableParts = ref([])
const selectedModel = ref(null)
const loading = ref(false)
const addDialogVisible = ref(false)
const modelSearch = ref('')
const partSearch = ref('')
const selectedParts = ref([])

const loadVehicleTree = async () => {
  try {
    const res = await request.get('/admin/vehicle/tree')
    vehicleTree.value = res.data
  } catch (e) {
    ElMessage.error('加载车型树失败')
  }
}

const selectModel = async (data) => {
  if (!data.models) return
  selectedModel.value = data
  loading.value = true
  try {
    const res = await request.get(`/admin/vehicle/part-relation/model/${data.id}`)
    relatedParts.value = res.data
  } catch (e) {
    ElMessage.error('加载关联配件失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = async () => {
  addDialogVisible.value = true
  await searchParts()
}

const searchParts = async () => {
  try {
    const res = await request.get('/product/page', {
      params: { keyword: partSearch.value || undefined, pageNum: 1, pageSize: 100 }
    })
    availableParts.value = res.data.records
  } catch (e) {
    console.error('搜索配件失败', e)
  }
}

const handleSelectionChange = (selection) => {
  selectedParts.value = selection
}

const batchAddParts = async () => {
  if (selectedParts.value.length === 0) {
    ElMessage.warning('请选择配件')
    return
  }
  try {
    const productIds = selectedParts.value.map(p => p.id)
    await request.post('/admin/vehicle/part-relation/batch', productIds, {
      params: { modelId: selectedModel.value.id }
    })
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    selectModel(selectedModel.value)
  } catch (e) {
    ElMessage.error('添加失败')
  }
}

const removeRelation = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该关联？', '提示', { type: 'warning' })
    await request.delete('/admin/vehicle/part-relation', {
      params: { modelId: selectedModel.value.id, productId: row.productId }
    })
    ElMessage.success('删除成功')
    selectModel(selectedModel.value)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const filterModels = () => {
  // Simple filter logic can be added if needed
}

onMounted(() => {
  loadVehicleTree()
})
</script>

<style scoped>
.part-relation-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
