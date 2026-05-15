<template>
  <div class="purchase-order-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购单管理</span>
          <el-button type="primary" @click="showCreateDialog">新建采购单</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="草稿" value="draft" />
            <el-option label="待入库" value="pending" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="采购单号" />
        <el-table-column prop="supplierName" label="供应商" />
        <el-table-column prop="totalAmount" label="总金额">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="row.status === 'pending'" type="warning">待入库</el-tag>
            <el-tag v-else-if="row.status === 'completed'" type="success">已完成</el-tag>
            <el-tag v-else-if="row.status === 'cancelled'" type="danger">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 'draft' || row.status === 'pending'"
              size="small" 
              type="success" 
              @click="confirmOrder(row)"
            >确认入库</el-button>
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

    <!-- Create Purchase Order Dialog -->
    <el-dialog v-model="createDialogVisible" title="新建采购单" width="800px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="供应商">
          <el-select v-model="createForm.supplierId" placeholder="请选择供应商" @change="onSupplierChange">
            <el-option 
              v-for="supplier in suppliers" 
              :key="supplier.id" 
              :label="supplier.name" 
              :value="supplier.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="商品明细">
          <el-table :data="createForm.items" style="width: 100%">
            <el-table-column label="商品">
              <template #default="{ row }">
                <el-select v-model="row.productId" placeholder="选择商品" @change="onProductChange(row)">
                  <el-option 
                    v-for="product in products" 
                    :key="product.id" 
                    :label="product.name" 
                    :value="product.id" 
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.quantity" :min="1" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="100">
              <template #default="{ row }">
                ¥{{ (row.quantity * row.unitPrice).toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ $index }">
                <el-button size="small" type="danger" @click="removeItem($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button style="margin-top: 10px" @click="addItem">添加商品</el-button>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">提交</el-button>
      </template>
    </el-dialog>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailDialogVisible" title="采购单详情" width="600px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="采购单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ detailData.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detailData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
            <el-tag v-if="detailData.status === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="detailData.status === 'pending'" type="warning">待入库</el-tag>
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
const suppliers = ref([])
const products = ref([])
const createDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailData = ref(null)

const queryForm = reactive({
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const createForm = reactive({
  supplierId: null,
  supplierName: '',
  remark: '',
  items: []
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/purchase-order/page', {
      params: {
        status: queryForm.status || undefined,
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      }
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadSuppliers = async () => {
  try {
    const res = await request.get('/admin/supplier/list')
    suppliers.value = res.data
  } catch (e) {
    console.error('加载供应商失败', e)
  }
}

const loadProducts = async () => {
  try {
    const res = await request.get('/product/page', {
      params: { pageNum: 1, pageSize: 100 }
    })
    products.value = res.data.records
  } catch (e) {
    console.error('加载商品失败', e)
  }
}

const showCreateDialog = () => {
  createForm.supplierId = null
  createForm.supplierName = ''
  createForm.remark = ''
  createForm.items = []
  createDialogVisible.value = true
}

const onSupplierChange = (id) => {
  const supplier = suppliers.value.find(s => s.id === id)
  if (supplier) {
    createForm.supplierName = supplier.name
  }
}

const onProductChange = (row) => {
  const product = products.value.find(p => p.id === row.productId)
  if (product) {
    row.unitPrice = product.price
    row.productName = product.name
    row.productCode = product.code
  }
}

const addItem = () => {
  createForm.items.push({
    productId: null,
    quantity: 1,
    unitPrice: 0,
    productName: '',
    productCode: ''
  })
}

const removeItem = (index) => {
  createForm.items.splice(index, 1)
}

const submitCreate = async () => {
  if (!createForm.supplierId) {
    ElMessage.warning('请选择供应商')
    return
  }
  if (createForm.items.length === 0) {
    ElMessage.warning('请添加商品明细')
    return
  }
  try {
    await request.post('/admin/purchase-order', createForm.items, {
      params: {
        supplierId: createForm.supplierId,
        supplierName: createForm.supplierName,
        remark: createForm.remark
      }
    })
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    loadData()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

const viewDetail = async (row) => {
  try {
    const res = await request.get(`/admin/purchase-order/${row.id}`)
    detailData.value = res.data
    detailDialogVisible.value = true
  } catch (e) {
    ElMessage.error('加载详情失败')
  }
}

const confirmOrder = async (row) => {
  try {
    await ElMessageBox.confirm('确认入库后库存将自动增加，是否继续？', '提示', {
      type: 'warning'
    })
    await request.put(`/admin/purchase-order/${row.id}/confirm`)
    ElMessage.success('入库成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('入库失败')
    }
  }
}

onMounted(() => {
  loadData()
  loadSuppliers()
  loadProducts()
})
</script>

<style scoped>
.purchase-order-manage {
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
