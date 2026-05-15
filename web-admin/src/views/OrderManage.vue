<template>
  <div class="order-manage">
    <div class="search-bar">
      <el-input v-model="searchForm.orderNo" placeholder="订单号" style="width: 200px" clearable />
      <el-select v-model="searchForm.status" placeholder="订单状态" clearable style="width: 150px; margin-left: 10px">
        <el-option label="待付款" :value="0" />
        <el-option label="待发货" :value="1" />
        <el-option label="待收货" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="orderNo" label="订单号" width="200" />
      <el-table-column prop="userId" label="用户ID" width="100" />
      <el-table-column label="商品信息" min-width="200">
        <template #default="{ row }">
          <div v-if="row.items && row.items.length > 0">
            <div v-for="item in row.items" :key="item.id" class="order-item-row">
              <span>{{ item.productName }} × {{ item.quantity }}</span>
            </div>
          </div>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="payAmount" label="实付金额" width="120">
        <template #default="{ row }">¥{{ row.payAmount || row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleViewDetail(row)">详情</el-button>
          <el-button v-if="row.status === 1" type="success" size="small" @click="handleShip(row)">发货</el-button>
          <el-button v-if="row.status === 2 || row.status === 3" type="info" size="small" @click="handleViewLogistics(row)">物流</el-button>
          <el-button v-if="row.status === 0" type="warning" size="small" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      style="margin-top: 20px; justify-content: flex-end"
      @current-change="getData"
      @size-change="getData"
    />

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ currentOrder.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ currentOrder.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="收货电话">{{ currentOrder.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ currentOrder.payTime ? formatTime(currentOrder.payTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="物流公司" v-if="currentOrder.logisticsCompany">{{ currentOrder.logisticsCompany }}</el-descriptions-item>
        <el-descriptions-item label="物流单号" v-if="currentOrder.logisticsNo">{{ currentOrder.logisticsNo }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>订单商品</el-divider>
      <el-table :data="currentOrder.items || []" border>
        <el-table-column label="商品图片" width="80">
          <template #default="{ row }">
            <img v-if="row.productImage" :src="row.productImage" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalPrice" label="小计" width="100">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="shipDialogVisible" title="发货" width="400px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.logisticsCompany" placeholder="请输入物流公司" />
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.logisticsNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmShip">确认发货</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="logisticsVisible" title="物流追踪" width="500px">
      <el-descriptions :column="1" border v-if="logisticsInfo">
        <el-descriptions-item label="订单号">{{ logisticsInfo.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="物流公司">{{ logisticsInfo.logisticsCompany || '-' }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ logisticsInfo.logisticsNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ logisticsInfo.shipTime ? formatTime(logisticsInfo.shipTime) : '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ logisticsInfo.receiverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货电话">{{ logisticsInfo.receiverPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ logisticsInfo.receiverAddress || '-' }}</el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无物流信息" />
      <template #footer>
        <el-button @click="logisticsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderDetail, shipOrder, cancelOrder, getLogistics } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const detailVisible = ref(false)
const shipDialogVisible = ref(false)
const logisticsVisible = ref(false)
const logisticsInfo = ref(null)
const currentOrder = reactive({ items: [] })
const shipForm = reactive({ logisticsCompany: '', logisticsNo: '' })

const searchForm = reactive({
  orderNo: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusMap = {
  0: { text: '待付款', type: 'info' },
  1: { text: '待发货', type: 'warning' },
  2: { text: '待收货', type: 'primary' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已取消', type: 'danger' }
}

const getStatusText = (status) => statusMap[status]?.text || '未知'
const getStatusType = (status) => statusMap[status]?.type || 'info'

const formatTime = (time) => {
  if (!time) return '-'
  if (Array.isArray(time)) {
    const [y, m, d, h, min, s] = time
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}:${String(s || 0).padStart(2, '0')}`
  }
  return time
}

const getData = async () => {
  loading.value = true
  try {
    const res = await getOrderList({
      pageNum: pagination.current,
      pageSize: pagination.size,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取订单列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  getData()
}

const handleReset = () => {
  searchForm.orderNo = ''
  searchForm.status = null
  handleSearch()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getOrderDetail(row.id)
    Object.assign(currentOrder, res.data)
    detailVisible.value = true
  } catch (error) {
    console.error('获取订单详情失败', error)
  }
}

const handleShip = (row) => {
  currentOrder.id = row.id
  shipForm.logisticsCompany = ''
  shipForm.logisticsNo = ''
  shipDialogVisible.value = true
}

const handleConfirmShip = async () => {
  try {
    await shipOrder(currentOrder.id, shipForm.logisticsCompany, shipForm.logisticsNo)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    getData()
  } catch (error) {
    console.error('发货失败', error)
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await getOrderList({ pageNum: 1, pageSize: 1 })
    ElMessage.success('订单已取消')
    getData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败', error)
    }
  }
}

const handleViewLogistics = async (row) => {
  logisticsInfo.value = null
  logisticsVisible.value = true
  try {
    const res = await getLogistics(row.id)
    logisticsInfo.value = res.data
  } catch (error) {
    console.error('获取物流信息失败', error)
  }
}

onMounted(() => {
  getData()
})
</script>

<style scoped>
.order-manage {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
}

.order-item-row {
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}
</style>
