<template>
  <div class="product-detail" v-loading="loading">
    <div class="breadcrumb">
      <el-breadcrumb separator=">">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/products' }">全部商品</el-breadcrumb-item>
        <el-breadcrumb-item>{{ product?.name || '商品详情' }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="detail-card" v-if="product">
      <div class="detail-main">
        <div class="product-gallery">
          <div class="main-image" @click="showPreview = true">
            <el-image v-if="mainImage" :src="mainImage" fit="contain" class="main-img" />
            <div v-else class="no-image">
              <el-icon :size="64" color="#ccc"><Goods /></el-icon>
              <span>暂无图片</span>
            </div>
            <div class="zoom-hint" v-if="mainImage">
              <el-icon><ZoomIn /></el-icon>
              点击放大
            </div>
          </div>
          <div class="thumbnail-list" v-if="imageList.length > 1">
            <div
              v-for="(img, index) in imageList"
              :key="index"
              class="thumbnail"
              :class="{ active: currentImageIndex === index }"
              @click="currentImageIndex = index"
            >
              <el-image :src="img" fit="cover" class="thumb-img" />
            </div>
          </div>
        </div>

        <div class="product-info">
          <div class="product-header">
            <h1 class="product-name">{{ product.name }}</h1>
            <p class="product-sub">{{ product.subName }}</p>
            <div class="product-tags">
              <el-tag v-if="product.brand" type="info" size="small">{{ product.brand }}</el-tag>
              <el-tag v-if="product.oem" size="small">{{ product.oem }}</el-tag>
            </div>
          </div>

          <div class="price-section">
            <div class="price-main">
              <span class="price-label">新卓阅价</span>
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
            <div class="price-extra">
              <span class="stock-info">
                库存 <strong>{{ product.stock }}</strong> 件
              </span>
              <span class="sales-info" v-if="product.sales">
                已售 <strong>{{ product.sales }}</strong> 件
              </span>
            </div>
          </div>

          <div class="spec-section">
            <div class="spec-row">
              <span class="spec-label">商品编号</span>
              <span class="spec-value">{{ product.code }}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">品牌</span>
              <span class="spec-value">{{ product.brand || '-' }}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">规格</span>
              <span class="spec-value">{{ product.spec || '-' }}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">单位</span>
              <span class="spec-value">{{ product.unit || '-' }}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">OEM编号</span>
              <span class="spec-value">{{ product.oem || '-' }}</span>
            </div>
            <div class="spec-row">
              <span class="spec-label">适用系列</span>
              <span class="spec-value">{{ product.series || '-' }}</span>
            </div>
          </div>

          <div class="quantity-section">
            <span class="qty-label">数量</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock"
              size="large"
              class="qty-input"
            />
            <span class="qty-unit">{{ product.unit || '件' }}</span>
          </div>

          <div class="action-section">
            <el-button type="primary" size="large" class="btn-cart" @click="handleAddToCart">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button type="danger" size="large" class="btn-buy" @click="handleBuyNow">
              立即购买
            </el-button>
            <el-button
              :type="isFavorite ? 'warning' : 'default'"
              size="large"
              class="btn-fav"
              @click="handleToggleFavorite"
            >
              <el-icon><Star /></el-icon>
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <div class="service-promise">
            <div class="promise-item">
              <el-icon color="#ff6600"><CircleCheck /></el-icon>
              <span>正品保证</span>
            </div>
            <div class="promise-item">
              <el-icon color="#ff6600"><Truck /></el-icon>
              <span>极速配送</span>
            </div>
            <div class="promise-item">
              <el-icon color="#ff6600"><Service /></el-icon>
              <span>售后无忧</span>
            </div>
          </div>
        </div>
      </div>

      <div class="detail-tabs">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="商品详情" name="detail">
            <div class="detail-content">
              <div class="detail-block">
                <h3 class="block-title">商品信息</h3>
                <div class="info-table">
                  <div class="info-row">
                    <span class="info-label">商品名称</span>
                    <span class="info-val">{{ product.name }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">副标题</span>
                    <span class="info-val">{{ product.subName }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">适用系列</span>
                    <span class="info-val">{{ product.series || '-' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">OEM编号</span>
                    <span class="info-val">{{ product.oem || '-' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">规格</span>
                    <span class="info-val">{{ product.spec || '-' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">品牌</span>
                    <span class="info-val">{{ product.brand || '-' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">单位</span>
                    <span class="info-val">{{ product.unit || '-' }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">商品编号</span>
                    <span class="info-val">{{ product.code }}</span>
                  </div>
                </div>
              </div>
              <div class="detail-block" v-if="product.description">
                <h3 class="block-title">商品描述</h3>
                <div class="desc-content" v-html="product.description"></div>
              </div>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="`适用车型 (${applicableVehicles.length})`" name="vehicles" v-if="applicableVehicles.length > 0">
            <div class="vehicle-section">
              <el-table :data="applicableVehicles" stripe class="vehicle-table">
                <el-table-column prop="name" label="车型名称" min-width="200" />
                <el-table-column prop="year" label="年款" width="100" />
                <el-table-column prop="displacement" label="排量" width="100" />
                <el-table-column prop="position" label="安装位置" width="120" />
              </el-table>
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品评价" name="review">
            <ProductReview :productId="product.id" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <el-dialog v-model="showPreview" width="80%" :show-close="true" class="preview-dialog">
      <div class="preview-container">
        <el-image :src="mainImage" fit="contain" style="width: 100%; max-height: 80vh" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import { addToCart } from '@/api/cart'
import { toggleFavorite, checkFavorite } from '@/api/favorite'
import { ElMessage } from 'element-plus'
import ProductReview from '@/components/ProductReview.vue'

const route = useRoute()
const router = useRouter()
const product = ref(null)
const loading = ref(false)
const quantity = ref(1)
const activeTab = ref('detail')
const isFavorite = ref(false)
const applicableVehicles = ref([])
const imageList = ref([])
const currentImageIndex = ref(0)
const showPreview = ref(false)

const mainImage = computed(() => {
  return imageList.value[currentImageIndex.value] || ''
})

const handleAddToCart = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('加入购物车失败')
  }
}

const handleBuyNow = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await addToCart({ productId: product.value.id, quantity: quantity.value })
    router.push('/checkout')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleToggleFavorite = async () => {
  const token = localStorage.getItem('userToken')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    await toggleFavorite(product.value.id)
    isFavorite.value = !isFavorite.value
    ElMessage.success(isFavorite.value ? '已收藏' : '已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const loadProduct = async () => {
  loading.value = true
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data.product
    applicableVehicles.value = res.data.applicableVehicles || []

    if (product.value.image) {
      const images = product.value.image.split(',').filter(url => url.trim())
      imageList.value = images.map(url => {
        if (url.startsWith('http')) return url
        return url
      })
    }

    const token = localStorage.getItem('userToken')
    if (token) {
      try {
        const favRes = await checkFavorite(product.value.id)
        isFavorite.value = favRes.data.favorite
      } catch (e) {
        console.error('检查收藏状态失败', e)
      }
    }
  } catch (e) {
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadProduct)
</script>

<style scoped>
.product-detail {
  padding-bottom: 60px;
}

.breadcrumb {
  padding: 16px 0;
}

.detail-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.detail-main {
  display: flex;
  gap: 48px;
  padding: 32px;
}

.product-gallery {
  flex: 0 0 420px;
  width: 420px;
}

.main-image {
  width: 420px;
  height: 420px;
  background: #fafafa;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.main-img {
  width: 100%;
  height: 100%;
}

.main-img :deep(img) {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #ccc;
  font-size: 14px;
}

.zoom-hint {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
}

.main-image:hover .zoom-hint {
  opacity: 1;
}

.thumbnail-list {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.thumbnail {
  width: 68px;
  height: 68px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
  flex-shrink: 0;
}

.thumbnail:hover {
  border-color: #ff8533;
}

.thumbnail.active {
  border-color: #ff6600;
}

.thumb-img {
  width: 100%;
  height: 100%;
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-header {
  margin-bottom: 20px;
}

.product-name {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.product-sub {
  font-size: 14px;
  color: #999;
  margin: 0 0 10px 0;
}

.product-tags {
  display: flex;
  gap: 8px;
}

.price-section {
  background: linear-gradient(135deg, #fff5f0 0%, #fff0e6 100%);
  padding: 20px 24px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.price-main {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 10px;
}

.price-label {
  font-size: 13px;
  color: #ff6600;
  background: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 8px;
}

.price-symbol {
  font-size: 20px;
  font-weight: 700;
  color: #ff6600;
}

.price-value {
  font-size: 36px;
  font-weight: 700;
  color: #ff6600;
  line-height: 1;
}

.price-extra {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: #999;
}

.price-extra strong {
  color: #666;
}

.spec-section {
  margin-bottom: 20px;
}

.spec-row {
  display: flex;
  padding: 8px 0;
  font-size: 14px;
}

.spec-label {
  width: 80px;
  color: #999;
  flex-shrink: 0;
}

.spec-value {
  color: #333;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px 0;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
}

.qty-label {
  font-size: 14px;
  color: #666;
}

.qty-input {
  width: 140px;
}

.qty-unit {
  font-size: 14px;
  color: #999;
}

.action-section {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-cart {
  flex: 1;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
}

.btn-buy {
  flex: 1;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
}

.btn-fav {
  width: 120px;
  height: 48px;
  font-size: 14px;
  border-radius: 8px;
  flex-shrink: 0;
}

.service-promise {
  display: flex;
  gap: 32px;
  padding: 16px 0;
  border-top: 1px solid #f5f5f5;
}

.promise-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.detail-tabs {
  border-top: 1px solid #f0f0f0;
  padding: 0 32px 32px;
}

.custom-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.custom-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.detail-content {
  padding: 24px 0;
}

.detail-block {
  margin-bottom: 32px;
}

.block-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 16px 0;
  padding-left: 12px;
  border-left: 3px solid #ff6600;
}

.info-table {
  background: #fafafa;
  border-radius: 8px;
  padding: 4px 0;
}

.info-row {
  display: flex;
  padding: 10px 20px;
  font-size: 14px;
}

.info-row:nth-child(even) {
  background: #fff;
}

.info-label {
  width: 100px;
  color: #999;
  flex-shrink: 0;
}

.info-val {
  color: #333;
}

.desc-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.desc-content :deep(h1),
.desc-content :deep(h2),
.desc-content :deep(h3) {
  color: #333;
  margin: 16px 0 8px;
}

.desc-content :deep(p) {
  margin: 8px 0;
}

.desc-content :deep(ul),
.desc-content :deep(ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.desc-content :deep(li) {
  margin: 4px 0;
}

.desc-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}

.desc-content :deep(a) {
  color: #ff6600;
}

.desc-content :deep(blockquote) {
  border-left: 4px solid #ff6600;
  padding-left: 16px;
  margin: 12px 0;
  color: #999;
  background: #fff8f5;
  padding: 12px 16px;
  border-radius: 0 8px 8px 0;
}

.vehicle-section {
  padding: 16px 0;
}

.vehicle-table {
  border-radius: 8px;
  overflow: hidden;
}

.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.preview-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: #1a1a1a;
  border-radius: 8px;
  overflow: hidden;
}

@media (max-width: 900px) {
  .detail-main {
    flex-direction: column;
    gap: 24px;
    padding: 20px;
  }

  .product-gallery {
    flex: none;
    width: 100%;
  }

  .main-image {
    width: 100%;
    height: 300px;
  }

  .action-section {
    flex-wrap: wrap;
  }

  .btn-fav {
    width: 100%;
  }
}
</style>