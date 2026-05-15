<template>
  <div class="vehicle-manage">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>品牌管理</span>
              <el-button type="primary" size="small" @click="showBrandDialog">新增</el-button>
            </div>
          </template>
          <el-table :data="brands" @row-click="selectBrand" style="cursor: pointer" highlight-current-row>
            <el-table-column prop="name" label="品牌名称" />
            <el-table-column prop="initial" label="首字母" width="80" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" @click.stop="editBrand(row)">编辑</el-button>
                <el-button size="small" type="danger" @click.stop="deleteBrand(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>车系管理</span>
              <el-button type="primary" size="small" @click="showSeriesDialog" :disabled="!selectedBrand">新增</el-button>
            </div>
          </template>
          <el-table :data="series" @row-click="selectSeries" style="cursor: pointer" highlight-current-row>
            <el-table-column prop="name" label="车系名称" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" @click.stop="editSeries(row)">编辑</el-button>
                <el-button size="small" type="danger" @click.stop="deleteSeries(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>车型管理</span>
              <el-button type="primary" size="small" @click="showModelDialog" :disabled="!selectedSeries">新增</el-button>
            </div>
          </template>
          <el-table :data="models">
            <el-table-column prop="name" label="车型名称" />
            <el-table-column prop="year" label="年款" width="80" />
            <el-table-column prop="displacement" label="排量" width="80" />
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button size="small" @click="editModel(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteModel(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="brandDialogVisible" :title="brandForm.id ? '编辑品牌' : '新增品牌'" width="400px">
      <el-form :model="brandForm" label-width="80px">
        <el-form-item label="品牌名称">
          <el-input v-model="brandForm.name" />
        </el-form-item>
        <el-form-item label="首字母">
          <el-input v-model="brandForm.initial" maxlength="1" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="brandForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="brandDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveBrand">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="seriesDialogVisible" :title="seriesForm.id ? '编辑车系' : '新增车系'" width="400px">
      <el-form :model="seriesForm" label-width="80px">
        <el-form-item label="车系名称">
          <el-input v-model="seriesForm.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="seriesForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="seriesDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSeries">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="modelDialogVisible" :title="modelForm.id ? '编辑车型' : '新增车型'" width="500px">
      <el-form :model="modelForm" label-width="80px">
        <el-form-item label="车型名称">
          <el-input v-model="modelForm.name" />
        </el-form-item>
        <el-form-item label="年款">
          <el-input v-model="modelForm.year" />
        </el-form-item>
        <el-form-item label="排量">
          <el-input v-model="modelForm.displacement" />
        </el-form-item>
        <el-form-item label="发动机">
          <el-input v-model="modelForm.engine" />
        </el-form-item>
        <el-form-item label="变速箱">
          <el-input v-model="modelForm.transmission" />
        </el-form-item>
        <el-form-item label="VIN前缀">
          <el-input v-model="modelForm.vinPrefix" maxlength="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="modelForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveModel">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const brands = ref([])
const series = ref([])
const models = ref([])
const selectedBrand = ref(null)
const selectedSeries = ref(null)

const brandDialogVisible = ref(false)
const seriesDialogVisible = ref(false)
const modelDialogVisible = ref(false)

const brandForm = reactive({ id: null, name: '', initial: '', sort: 0, status: 1 })
const seriesForm = reactive({ id: null, brandId: null, name: '', sort: 0, status: 1 })
const modelForm = reactive({ id: null, seriesId: null, name: '', year: '', displacement: '', engine: '', transmission: '', vinPrefix: '', sort: 0, status: 1 })

const loadBrands = async () => {
  try {
    const res = await request.get('/admin/vehicle/brands')
    brands.value = res.data
  } catch (e) {
    ElMessage.error('加载品牌失败')
  }
}

const loadSeries = async (brandId) => {
  if (!brandId) {
    series.value = []
    return
  }
  try {
    const res = await request.get('/admin/vehicle/series', { params: { brandId } })
    series.value = res.data
  } catch (e) {
    ElMessage.error('加载车系失败')
  }
}

const loadModels = async (seriesId) => {
  if (!seriesId) {
    models.value = []
    return
  }
  try {
    const res = await request.get('/admin/vehicle/models', { params: { seriesId, pageNum: 1, pageSize: 100 } })
    models.value = res.data.records
  } catch (e) {
    ElMessage.error('加载车型失败')
  }
}

const selectBrand = (row) => {
  selectedBrand.value = row
  selectedSeries.value = null
  loadSeries(row.id)
  models.value = []
}

const selectSeries = (row) => {
  selectedSeries.value = row
  loadModels(row.id)
}

const showBrandDialog = () => {
  Object.assign(brandForm, { id: null, name: '', initial: '', sort: 0, status: 1 })
  brandDialogVisible.value = true
}

const editBrand = (row) => {
  Object.assign(brandForm, { ...row })
  brandDialogVisible.value = true
}

const saveBrand = async () => {
  try {
    if (brandForm.id) {
      await request.put(`/admin/vehicle/brand/${brandForm.id}`, brandForm)
    } else {
      await request.post('/admin/vehicle/brand', brandForm)
    }
    ElMessage.success('保存成功')
    brandDialogVisible.value = false
    loadBrands()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteBrand = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该品牌？', '提示', { type: 'warning' })
    await request.delete(`/admin/vehicle/brand/${row.id}`)
    ElMessage.success('删除成功')
    loadBrands()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const showSeriesDialog = () => {
  Object.assign(seriesForm, { id: null, brandId: selectedBrand.value.id, name: '', sort: 0, status: 1 })
  seriesDialogVisible.value = true
}

const editSeries = (row) => {
  Object.assign(seriesForm, { ...row })
  seriesDialogVisible.value = true
}

const saveSeries = async () => {
  try {
    if (seriesForm.id) {
      await request.put(`/admin/vehicle/series/${seriesForm.id}`, seriesForm)
    } else {
      await request.post('/admin/vehicle/series', seriesForm)
    }
    ElMessage.success('保存成功')
    seriesDialogVisible.value = false
    loadSeries(selectedBrand.value.id)
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteSeries = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该车系？', '提示', { type: 'warning' })
    await request.delete(`/admin/vehicle/series/${row.id}`)
    ElMessage.success('删除成功')
    loadSeries(selectedBrand.value.id)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

const showModelDialog = () => {
  Object.assign(modelForm, { id: null, seriesId: selectedSeries.value.id, name: '', year: '', displacement: '', engine: '', transmission: '', vinPrefix: '', sort: 0, status: 1 })
  modelDialogVisible.value = true
}

const editModel = (row) => {
  Object.assign(modelForm, { ...row })
  modelDialogVisible.value = true
}

const saveModel = async () => {
  try {
    if (modelForm.id) {
      await request.put(`/admin/vehicle/model/${modelForm.id}`, modelForm)
    } else {
      await request.post('/admin/vehicle/model', modelForm)
    }
    ElMessage.success('保存成功')
    modelDialogVisible.value = false
    loadModels(selectedSeries.value.id)
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

const deleteModel = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该车型？', '提示', { type: 'warning' })
    await request.delete(`/admin/vehicle/model/${row.id}`)
    ElMessage.success('删除成功')
    loadModels(selectedSeries.value.id)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadBrands()
})
</script>

<style scoped>
.vehicle-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
