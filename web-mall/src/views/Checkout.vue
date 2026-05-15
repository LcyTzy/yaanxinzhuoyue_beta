<template>
  <div class="checkout-page">
    <h2 class="page-title">确认订单</h2>
    <div class="checkout-content">
      <div class="checkout-form">
        <div class="section">
          <h3>收货地址</h3>
          <div v-if="addresses.length > 0">
            <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ active: selectedAddress?.id === addr.id }" @click="selectedAddress = addr">
              <div class="address-info">
                <span class="name">{{ addr.receiverName }}</span>
                <span class="phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" size="small" type="danger">默认</el-tag>
              </div>
              <p>{{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detail }}</p>
            </div>
          </div>
          <el-empty v-else description="暂无收货地址" />
        </div>

        <div class="section">
          <h3>优惠券</h3>
          <div class="coupon-selector" @click="showCouponDialog = true">
            <template v-if="selectedCoupon">
              <span class="coupon-selected">已选：{{ selectedCoupon.coupon?.name || '优惠券' }}</span>
              <span class="coupon-discount">-¥{{ calculateDiscount(selectedCoupon) }}</span>
            </template>
            <template v-else>
              <span class="coupon-placeholder">选择优惠券</span>
              <span class="coupon-count">{{ availableCoupons.length }}张可用</span>
            </template>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="section">
          <h3>订单备注</h3>
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="选填，可以告诉商家您的特殊需求" />
        </div>
      </div>

      <div class="order-summary">
        <h3>订单信息</h3>
        <div v-for="item in cartItems" :key="item.id" class="order-item">
          <span>{{ item.productName }}</span>
          <span>x{{ item.quantity }}</span>
          <span>¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
        <div class="order-total">
          <div class="total-row">
            <span>商品总额</span>
            <span>¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="total-row" v-if="selectedCoupon">
            <span>优惠券抵扣</span>
            <span class="discount">-¥{{ calculateDiscount(selectedCoupon) }}</span>
          </div>
          <div class="total-row">
            <span>运费</span>
            <span>免运费</span>
          </div>
          <div class="total-row final">
            <span>应付总额</span>
            <span class="price">¥{{ finalPrice.toFixed(2) }}</span>
          </div>
        </div>
        <el-button type="danger" size="large" style="width: 100%; margin-top: 16px" @click="handleSubmit">
          提交订单
        </el-button>
      </div>
    </div>

    <el-dialog v-model="showCouponDialog" title="选择优惠券" width="600px">
      <div class="coupon-list">
        <div v-for="coupon in availableCoupons" :key="coupon.id" class="coupon-item" :class="{ selected: selectedCoupon?.id === coupon.id }" @click="selectedCoupon = coupon">
          <div class="coupon-info">
            <h4>{{ coupon.coupon?.name }}</h4>
            <p v-if="coupon.coupon?.minAmount > 0">满{{ coupon.coupon.minAmount }}元可用</p>
            <p v-else>无门槛</p>
          </div>
          <div class="coupon-value">
            <template v-if="coupon.coupon?.type === 1">
              <span class="amount">¥{{ coupon.coupon.discountAmount }}</span>
            </template>
            <template v-else-if="coupon.coupon?.type === 2">
              <span class="amount">{{ coupon.coupon.discountRate }}折</span>
            </template>
          </div>
        </div>
        <el-empty v-if="availableCoupons.length === 0" description="暂无可用优惠券" />
      </div>
      <template #footer>
        <el-button @click="showCouponDialog = false">取消</el-button>
        <el-button type="primary" @click="showCouponDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createOrder } from '@/api/order'
import { getCartList } from '@/api/cart'
import { getUserCoupons } from '@/api/coupon'
import { getAddressList } from '@/api/address'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const addresses = ref([])
const selectedAddress = ref(null)
const remark = ref('')
const cartItems = ref([])
const availableCoupons = ref([])
const selectedCoupon = ref(null)
const showCouponDialog = ref(false)

const totalPrice = computed(() => {
  return cartItems.value.reduce((sum, i) => sum + i.price * i.quantity, 0)
})

const finalPrice = computed(() => {
  let price = totalPrice.value
  if (selectedCoupon.value) {
    price -= calculateDiscount(selectedCoupon.value)
  }
  return Math.max(0, price)
})

const calculateDiscount = (coupon) => {
  if (!coupon || !coupon.coupon) return 0
  const total = totalPrice.value
  if (coupon.coupon.type === 1) {
    return Math.min(coupon.coupon.discountAmount, total)
  } else if (coupon.coupon.type === 2) {
    const discount = total * (1 - coupon.coupon.discountRate)
    return Math.min(discount, total)
  }
  return 0
}

const handleSubmit = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (cartItems.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  try {
    const cartIds = cartItems.value.map(item => item.id)
    const addr = selectedAddress.value
    await createOrder({
      cartIds,
      receiverName: addr.receiverName,
      receiverPhone: addr.receiverPhone,
      receiverAddress: `${addr.province} ${addr.city} ${addr.district} ${addr.detail}`,
      remark: remark.value,
      userCouponId: selectedCoupon.value?.id || null
    })
    ElMessage.success('订单提交成功')
    router.push('/orders')
  } catch (e) {
    ElMessage.error(e.message || '提交订单失败')
  }
}

const loadCart = async () => {
  try {
    const res = await getCartList()
    cartItems.value = res.data || []
  } catch (e) {
    console.error('加载购物车失败', e)
  }
}

const loadAddresses = async () => {
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) {
      selectedAddress.value = defaultAddr
    } else if (addresses.value.length > 0) {
      selectedAddress.value = addresses.value[0]
    }
  } catch (e) {
    console.error('加载地址失败', e)
  }
}

const loadCoupons = async () => {
  try {
    const res = await getUserCoupons({ status: 0, pageNum: 1, pageSize: 100 })
    const coupons = res.data?.records || []
    availableCoupons.value = coupons.filter(c => {
      if (!c.coupon || !c.coupon.minAmount) return true
      return totalPrice.value >= c.coupon.minAmount
    })
  } catch (e) {
    console.error('加载优惠券失败', e)
  }
}

onMounted(() => {
  loadCart()
  loadAddresses()
  loadCoupons()
})
</script>

<style scoped>
.checkout-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.checkout-content {
  display: flex;
  gap: 24px;
}

.checkout-form {
  flex: 1;
}

.section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.section h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.address-card {
  padding: 16px;
  border: 2px solid #eee;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.address-card.active {
  border-color: #ff6600;
  background: #fff5f0;
}

.address-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.address-info .name {
  font-weight: bold;
}

.address-info .phone {
  color: #666;
}

.order-summary {
  width: 360px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  height: fit-content;
}

.order-summary h3 {
  font-size: 16px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
}

.order-total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.total-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.total-row.final {
  font-size: 18px;
  font-weight: bold;
}

.total-row.final .price {
  color: #ff6600;
  font-size: 24px;
}

.coupon-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.coupon-selector:hover {
  border-color: #ff6600;
  background: #fff5f0;
}

.coupon-selected {
  flex: 1;
  color: #333;
}

.coupon-discount {
  color: #ff6600;
  font-weight: bold;
  margin: 0 12px;
}

.coupon-placeholder {
  color: #999;
}

.coupon-count {
  color: #ff6600;
  margin: 0 12px;
}

.coupon-list {
  max-height: 400px;
  overflow-y: auto;
}

.coupon-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border: 2px solid #eee;
  border-radius: 8px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.coupon-item.selected {
  border-color: #ff6600;
  background: #fff5f0;
}

.coupon-info h4 {
  margin: 0 0 8px;
  font-size: 16px;
}

.coupon-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.coupon-value .amount {
  color: #ff6600;
  font-size: 20px;
  font-weight: bold;
}

.total-row .discount {
  color: #ff6600;
  font-weight: bold;
}
</style>
