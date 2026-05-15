<template>
  <div class="home-page">
    <div class="vehicle-match-section">
      <div class="vehicle-card">
        <div class="vehicle-card-content">
          <div class="vehicle-info">
            <h3>按车型精准匹配配件</h3>
            <p v-if="selectedVehicle">
              当前车型：<strong>{{ selectedVehicle.brand.name }} {{ selectedVehicle.series.name }} {{ selectedVehicle.model.name }}</strong>
            </p>
            <p v-else>选择您的车型，精准匹配适配配件，避免买错</p>
          </div>
          <div class="vehicle-actions">
            <el-button type="primary" size="large" round @click="showVehicleSelector = true">
              <el-icon><Search /></el-icon>
              {{ selectedVehicle ? '重新选择车型' : '选择您的车型' }}
            </el-button>
            <el-button size="large" round @click="openVinQuery" class="vin-btn">
              <el-icon><Document /></el-icon>
              VIN码查询
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="banner-section">
      <el-carousel height="360px" :interval="4000" v-loading="bannerLoading">
        <el-carousel-item v-for="(banner, index) in banners" :key="banner.id">
          <div class="banner-item" @click="handleBannerClick(banner)" :style="getBannerStyle(banner, index)">
            <el-image
              v-if="banner.image && !banner.image.includes('via.placeholder.com')"
              :src="banner.image"
              fit="cover"
              style="width: 100%; height: 100%"
              @error="handleImageError"
            />
            <div class="banner-overlay">
              <h2>{{ banner.title }}</h2>
            </div>
          </div>
        </el-carousel-item>
        <el-carousel-item v-if="banners.length === 0">
          <div class="banner-item banner-placeholder">
            <h2>欢迎来到新卓阅汽配</h2>
            <p>品质配件 · 极速配送 · 售后无忧</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="feature-section">
      <div class="feature-grid">
        <div class="feature-item">
          <div class="feature-icon">🔧</div>
          <div class="feature-text">
            <h4>正品保证</h4>
            <p>100%原厂正品配件</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">🚚</div>
          <div class="feature-text">
            <h4>极速配送</h4>
            <p>下单后最快当日送达</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">🛡️</div>
          <div class="feature-text">
            <h4>售后无忧</h4>
            <p>30天无理由退换货</p>
          </div>
        </div>
        <div class="feature-item">
          <div class="feature-icon">📞</div>
          <div class="feature-text">
            <h4>专业客服</h4>
            <p>一对一技术顾问支持</p>
          </div>
        </div>
      </div>
    </div>

    <div class="category-section">
      <div class="section-header">
        <h3 class="section-title">商品分类</h3>
        <el-button type="primary" link @click="$router.push('/products')">查看全部 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.id" class="category-card" @click="goToCategory(cat.id)">
          <div class="category-icon">{{ categoryIcons[cat.name] || '🔧' }}</div>
          <span class="category-name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <div class="hot-section">
      <div class="section-header">
        <h3 class="section-title">热门商品</h3>
        <el-button type="primary" link @click="$router.push('/products')">查看更多 <el-icon><ArrowRight /></el-icon></el-button>
      </div>
      <div class="product-grid">
        <div v-for="product in hotProducts" :key="product.id" class="product-card" @click="goToDetail(product.id)">
          <div class="product-image">
            <el-image v-if="product.image" :src="getImageUrl(product.image)" fit="cover" class="product-img" />
            <div v-else class="no-image">
              <el-icon :size="48" color="#ccc"><Goods /></el-icon>
            </div>
            <div class="product-badge" v-if="product.stock <= 5 && product.stock > 0">库存紧张</div>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-sub">{{ product.subName }}</p>
            <div class="product-price-row">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
            <div class="product-meta">
              <span class="meta-item">已售 {{ product.sales || 0 }}</span>
              <span class="meta-item">收藏 {{ product.favoriteCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <VehicleSelector v-model="showVehicleSelector" @confirm="handleVehicleConfirm" />
    <VinQueryDialog ref="vinQueryDialogRef" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategoryTree, getProductPage } from '@/api/product'
import { getBanners } from '@/api/banner'
import VehicleSelector from '@/components/VehicleSelector.vue'
import VinQueryDialog from '@/components/VinQueryDialog.vue'

const router = useRouter()
const categories = ref([])
const hotProducts = ref([])
const banners = ref([])
const bannerLoading = ref(false)
const showVehicleSelector = ref(false)
const selectedVehicle = ref(null)
const vinQueryDialogRef = ref(null)

const categoryIcons = {
  '机油': '🛢️',
  '滤清器': '🔧',
  '刹车片': '🛑',
  '火花塞': '⚡',
  '雨刮器': '🌧️',
  '蓄电池': '🔋',
  '防冻液': '❄️',
  '变速箱油': '⚙️',
  '辅助油液': '💧'
}

const handleVehicleConfirm = (vehicle) => {
  selectedVehicle.value = vehicle
  localStorage.setItem('selectedVehicle', JSON.stringify(vehicle))
  router.push({ path: '/products', query: { vehicleModelId: vehicle.model.id } })
}

const openVinQuery = () => {
  vinQueryDialogRef.value.open()
}

const bannerGradients = [
  'linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
]

const handleImageError = (e) => {
  const parent = e.target.parentElement
  if (parent) {
    e.target.style.display = 'none'
    parent.style.background = bannerGradients[Math.floor(Math.random() * bannerGradients.length)]
  }
}

const getBannerStyle = (banner, index) => {
  if (banner.image && banner.image.includes('via.placeholder.com')) {
    return { background: bannerGradients[index % bannerGradients.length] }
  }
  return {}
}

const handleBannerClick = (banner) => {
  if (banner.link) {
    router.push(banner.link)
  }
}

const goToCategory = (id) => {
  router.push({ path: '/products', query: { categoryId: id } })
}

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const getImageUrl = (image) => {
  if (!image) return ''
  const firstImage = image.split(',')[0]
  if (firstImage.startsWith('http')) return firstImage
  return firstImage
}

const loadData = async () => {
  const vehicleStr = localStorage.getItem('selectedVehicle')
  if (vehicleStr) {
    selectedVehicle.value = JSON.parse(vehicleStr)
  }

  try {
    bannerLoading.value = true
    const bannerRes = await getBanners()
    banners.value = bannerRes.data || []
  } catch (e) {
    console.error('加载轮播图失败', e)
  } finally {
    bannerLoading.value = false
  }

  try {
    const catRes = await getCategoryTree()
    categories.value = catRes.data
  } catch (e) {
    console.error('加载分类失败', e)
  }

  try {
    const prodRes = await getProductPage({ pageNum: 1, pageSize: 8, sortBy: 'sales' })
    hotProducts.value = prodRes.data.records
  } catch (e) {
    console.error('加载商品失败', e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
}

.vehicle-match-section {
  margin-bottom: 24px;
}

.vehicle-card {
  background: linear-gradient(135deg, #ff6600 0%, #ff8533 100%);
  border-radius: 16px;
  padding: 32px 36px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(255, 102, 0, 0.3);
}

.vehicle-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.vehicle-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.vin-btn {
  background: rgba(255, 255, 255, 0.2) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
  color: #fff !important;
}

.vin-btn:hover {
  background: rgba(255, 255, 255, 0.3) !important;
}

.vehicle-info h3 {
  font-size: 22px;
  margin: 0 0 8px 0;
}

.vehicle-info p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

.vehicle-info strong {
  font-weight: 700;
}

.banner-section {
  margin-bottom: 30px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.banner-item {
  height: 360px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  cursor: pointer;
  position: relative;
}

.banner-item .banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));
  padding: 40px 32px 24px;
}

.banner-item h2 {
  font-size: 32px;
  margin: 0 0 8px 0;
}

.banner-item p {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

.banner-placeholder {
  background: linear-gradient(135deg, #ff6600 0%, #ff8533 100%);
}

.feature-section {
  margin-bottom: 40px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.feature-item {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.feature-icon {
  font-size: 36px;
  flex-shrink: 0;
}

.feature-text h4 {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.feature-text p {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  padding-left: 14px;
  border-left: 4px solid #ff6600;
}

.category-section {
  margin-bottom: 48px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 14px;
}

.category-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid transparent;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  border-color: #ffd4b8;
}

.category-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.category-name {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

.hot-section {
  margin-bottom: 40px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid #f0f0f0;
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  border-color: #ffd4b8;
}

.product-image {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  position: relative;
  overflow: hidden;
}

.product-img {
  width: 100%;
  height: 100%;
  transition: transform 0.4s;
}

.product-card:hover .product-img {
  transform: scale(1.05);
}

.no-image {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.product-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #ff6600;
  color: #fff;
  padding: 2px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.product-info {
  padding: 14px 16px 16px;
}

.product-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-sub {
  font-size: 12px;
  color: #bbb;
  margin: 0 0 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price-row {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.price-symbol {
  font-size: 14px;
  font-weight: 700;
  color: #ff6600;
}

.price-value {
  font-size: 22px;
  font-weight: 700;
  color: #ff6600;
  line-height: 1;
}

.product-meta {
  display: flex;
  justify-content: space-between;
}

.meta-item {
  font-size: 12px;
  color: #bbb;
}

@media (max-width: 768px) {
  .vehicle-card-content {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .feature-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>