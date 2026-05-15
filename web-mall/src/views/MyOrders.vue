<template>
  <div class="orders-page">
    <h2 class="page-title">我的订单</h2>
    <div class="order-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" :name="null" />
        <el-tab-pane label="待付款" :name="0" />
        <el-tab-pane label="待发货" :name="1" />
        <el-tab-pane label="待收货" :name="2" />
        <el-tab-pane label="已完成" :name="3" />
      </el-tabs>
    </div>
    <div class="order-list" v-loading="loading">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span>订单号: {{ order.orderNo }}</span>
          <span class="order-status" :style="{ color: statusColorMap[order.status] }">{{ statusMap[order.status] }}</span>
        </div>
        <div class="order-body">
          <div class="order-products">
            <div v-if="order.items && order.items.length > 0" class="product-item" v-for="item in order.items" :key="item.id">
              <img v-if="item.productImage" :src="item.productImage" class="product-thumb" />
              <div class="product-info">
                <p class="product-name">{{ item.productName }}</p>
                <p class="product-price">¥{{ item.price }} × {{ item.quantity }}</p>
              </div>
            </div>
            <p v-else class="no-product">商品信息</p>
          </div>
          <div class="order-amount">
            <span>实付款: </span>
            <span class="price">¥{{ order.payAmount || order.totalAmount }}</span>
          </div>
        </div>
        <div class="order-footer">
          <span class="order-time">{{ formatTime(order.createTime) }}</span>
          <div class="order-actions">
            <el-button v-if="order.status === 0" type="danger" size="small" @click="handlePay(order.id)">去付款</el-button>
            <el-button v-if="order.status === 0" size="small" @click="handleCancel(order.id)">取消订单</el-button>
            <el-button v-if="order.status === 2" type="primary" size="small" @click="handleConfirm(order.id)">确认收货</el-button>
            <el-button v-if="order.status === 1 || order.status === 2 || order.status === 3" type="warning" size="small" @click="handleApplyRefund(order)">申请退款</el-button>
            <el-button size="small" @click="handleViewDetail(order.id)">查看详情</el-button>
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>
    <el-pagination
      v-if="total > 0"
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="total"
      layout="total, prev, pager, next"
      style="margin-top: 20px; justify-content: center"
      @current-change="loadOrders"
    />

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border v-if="currentOrder">
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="statusTypeMap[currentOrder.status]">{{ statusMap[currentOrder.status] }}</el-tag>
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
      <el-table :data="currentOrder?.items || []" border>
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

    <el-dialog v-model="refundVisible" title="申请退款" width="500px">
      <el-form :model="refundForm" label-width="80px">
        <el-form-item label="订单号">
          <span>{{ refundForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="退款金额">
          <span class="refund-amount">¥{{ refundForm.amount }}</span>
        </el-form-item>
        <el-form-item label="退款原因" required>
          <el-select v-model="refundForm.reason" placeholder="请选择退款原因" style="width: 100%">
            <el-option label="不想要了" value="不想要了" />
            <el-option label="商品与描述不符" value="商品与描述不符" />
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="卖家发错货" value="卖家发错货" />
            <el-option label="其他原因" value="其他原因" />
          </el-select>
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="refundForm.remark" type="textarea" :rows="3" placeholder="选填，补充说明退款原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRefund" :loading="refundSubmitting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList, cancelOrder, payOrder, confirmReceive, getOrderDetail } from '@/api/order'
import { applyRefund } from '@/api/refund'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const total = ref(0)
const activeTab = ref(null)
const detailVisible = ref(false)
const currentOrder = ref(null)

const refundVisible = ref(false)
const refundSubmitting = ref(false)
const refundForm = reactive({
  orderId: null,
  orderNo: '',
  amount: 0,
  reason: '',
  remark: ''
})

const statusMap = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
const statusColorMap = { 0: '#909399', 1: '#e6a23c', 2: '#409eff', 3: '#67c23a', 4: '#f56c6c' }
const statusTypeMap = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }

const pagination = reactive({ current: 1, size: 10 })

const formatTime = (time) => {
  if (!time) return '-'
  if (Array.isArray(time)) {
    const [y, m, d, h, min, s] = time
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}:${String(s || 0).padStart(2, '0')}`
  }
  return time
}

const handleTabClick = () => {
  pagination.current = 1
  loadOrders()
}

const handlePay = async (id) => {
  try {
    await payOrder(id)
    ElMessage.success('付款成功')
    loadOrders()
  } catch (e) {
    ElMessage.error('付款失败')
  }
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const handleConfirm = async (id) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'warning' })
    await confirmReceive(id)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const handleViewDetail = async (id) => {
  try {
    const res = await getOrderDetail(id)
    currentOrder.value = res.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  }
}

const handleApplyRefund = (order) => {
  refundForm.orderId = order.id
  refundForm.orderNo = order.orderNo
  refundForm.amount = order.payAmount || order.totalAmount
  refundForm.reason = ''
  refundForm.remark = ''
  refundVisible.value = true
}

const handleSubmitRefund = async () => {
  if (!refundForm.reason) {
    ElMessage.warning('请选择退款原因')
    return
  }
  refundSubmitting.value = true
  try {
    await applyRefund({
      orderId: refundForm.orderId,
      reason: refundForm.reason + (refundForm.remark ? ' - ' + refundForm.remark : ''),
      images: ''
    })
    ElMessage.success('退款申请已提交，请等待审核')
    refundVisible.value = false
    loadOrders()
  } catch (e) {
    ElMessage.error(e.message || '提交退款申请失败')
  } finally {
    refundSubmitting.value = false
  }
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { pageNum: pagination.current, pageSize: pagination.size }
    if (activeTab.value !== null) {
      params.status = activeTab.value
    }
    const res = await getOrderList(params)
    const records = res.data.records || []
    orders.value = records
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载订单失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page { padding-bottom: 40px; }
.page-title { font-size: 22px; font-weight: bold; margin-bottom: 20px; padding-left: 12px; border-left: 4px solid #ff6600; }
.order-tabs { background: #fff; border-radius: 12px; padding: 0 20px; margin-bottom: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.order-list { display: flex; flex-direction: column; gap: 16px; }
.order-card { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.order-header { display: flex; justify-content: space-between; padding: 12px 20px; background: #f5f5f5; font-size: 14px; }
.order-status { font-weight: bold; }
.order-body { display: flex; justify-content: space-between; padding: 16px 20px; align-items: center; }
.order-products { flex: 1; }
.product-item { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.product-thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 6px; }
.product-name { font-size: 14px; color: #333; margin: 0; }
.product-price { font-size: 13px; color: #999; margin: 4px 0 0; }
.no-product { color: #999; }
.order-amount .price { font-size: 18px; font-weight: bold; color: #ff6600; }
.order-footer { display: flex; justify-content: space-between; padding: 12px 20px; border-top: 1px solid #f0f0f0; align-items: center; }
.order-time { color: #999; font-size: 13px; }
.order-actions { display: flex; gap: 8px; }
.refund-amount { font-size: 18px; font-weight: bold; color: #ff6600; }
</style>
