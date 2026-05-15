<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>
    <div class="cart-content" v-loading="loading">
      <template v-if="cartItems.length > 0">
        <div class="cart-table">
          <div class="cart-header">
            <div class="col-check"></div>
            <div class="col-product">商品</div>
            <div class="col-price">单价</div>
            <div class="col-quantity">数量</div>
            <div class="col-subtotal">小计</div>
            <div class="col-action">操作</div>
          </div>
          <div v-for="item in cartItems" :key="item.id" class="cart-row">
            <div class="col-check">
              <el-checkbox v-model="item.checked" />
            </div>
            <div class="col-product">
              <div class="product-info">
                <div class="product-image">
                  <el-image v-if="item.productImage" :src="getImageUrl(item.productImage)" fit="cover" style="width: 100%; height: 100%; border-radius: 8px;" />
                  <el-icon v-else :size="32" color="#ccc"><Goods /></el-icon>
                </div>
                <div class="product-text">
                  <h4>{{ item.productName }}</h4>
                  <p>{{ item.productSubName }}</p>
                </div>
              </div>
            </div>
            <div class="col-price">¥{{ item.price }}</div>
            <div class="col-quantity">
              <el-input-number v-model="item.quantity" :min="1" :max="999" size="small" @change="handleQuantityChange(item)" />
            </div>
            <div class="col-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            <div class="col-action">
              <el-button type="danger" size="small" @click="handleRemove(item.id)">删除</el-button>
            </div>
          </div>
        </div>
        <div class="cart-footer">
          <div class="cart-total">
            <span>已选 <strong>{{ checkedCount }}</strong> 件商品</span>
            <span class="total-price">合计: <strong class="price">¥{{ totalPrice.toFixed(2) }}</strong></span>
            <el-button type="danger" size="large" @click="handleCheckout" :disabled="checkedCount === 0">
              结算({{ checkedCount }})
            </el-button>
          </div>
        </div>
      </template>
      <el-empty v-else description="购物车是空的" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCartList, updateCartQuantity, removeFromCart } from '@/api/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartItems = ref([])
const loading = ref(false)

const checkedCount = computed(() => cartItems.value.filter(i => i.checked).length)

const totalPrice = computed(() => {
  return cartItems.value
    .filter(i => i.checked)
    .reduce((sum, i) => sum + i.price * i.quantity, 0)
})

const handleQuantityChange = async (item) => {
  try {
    await updateCartQuantity(item.id, item.quantity)
  } catch (e) {
    ElMessage.error('更新数量失败')
  }
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await removeFromCart(id)
    ElMessage.success('删除成功')
    loadCart()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleCheckout = () => {
  const checkedItems = cartItems.value.filter(i => i.checked)
  if (checkedItems.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  router.push('/checkout')
}

const getImageUrl = (image) => {
  if (!image) return ''
  const firstImage = image.split(',')[0]
  if (firstImage.startsWith('http')) return firstImage
  return firstImage
}

const loadCart = async () => {
  loading.value = true
  try {
    const res = await getCartList()
    cartItems.value = (res.data || []).map(item => ({ ...item, checked: true }))
  } catch (e) {
    console.error('加载购物车失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadCart)
</script>

<style scoped>
.cart-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.cart-content {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.cart-table {
  width: 100%;
}

.cart-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f5f5f5;
  border-radius: 8px;
  font-weight: 600;
  color: #666;
}

.cart-row {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.col-check { width: 50px; }
.col-product { flex: 1; }
.col-price { width: 100px; text-align: center; }
.col-quantity { width: 120px; text-align: center; }
.col-subtotal { width: 100px; text-align: center; color: #ff6600; font-weight: bold; }
.col-action { width: 80px; text-align: center; }

.product-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-image {
  width: 60px;
  height: 60px;
  background: #f8f8f8;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.product-text h4 {
  font-size: 14px;
  margin-bottom: 4px;
}

.product-text p {
  font-size: 12px;
  color: #999;
}

.cart-footer {
  margin-top: 20px;
}

.cart-total {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 24px;
  padding: 16px;
  background: #fff5f0;
  border-radius: 8px;
}

.total-price {
  font-size: 16px;
}

.total-price .price {
  font-size: 24px;
  color: #ff6600;
}
</style>
