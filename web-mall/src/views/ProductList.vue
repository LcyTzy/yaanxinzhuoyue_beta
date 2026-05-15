<template>
  <div class="product-list-page">
    <div class="breadcrumb">
      <el-breadcrumb separator=">">
        <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>全部商品</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="filter-bar">
      <div class="filter-header">
        <el-icon><Filter /></el-icon>
        <span>筛选条件</span>
      </div>
      <div class="filter-row" v-if="selectedVehicle">
        <span class="filter-label">当前车型</span>
        <span class="vehicle-tag">
          {{ selectedVehicle.brand.name }} {{ selectedVehicle.series.name }} {{ selectedVehicle.model.name }}
          <el-icon @click="clearVehicle" class="vehicle-close"><Close /></el-icon>
        </span>
      </div>
      <div class="filter-row">
        <span class="filter-label">商品分类</span>
        <div class="filter-options">
          <span
            class="filter-option"
            :class="{ active: filters.categoryId === null }"
            @click="filters.categoryId = null; handleFilterChange()"
          >全部</span>
          <span
            v-for="cat in categories"
            :key="cat.id"
            class="filter-option"
            :class="{ active: filters.categoryId === cat.id }"
            @click="filters.categoryId = cat.id; handleFilterChange()"
          >{{ cat.name }}</span>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">排序方式</span>
        <div class="filter-options">
          <span
            class="filter-option"
            :class="{ active: filters.sortBy === 'default' }"
            @click="filters.sortBy = 'default'; handleFilterChange()"
          >默认</span>
          <span
            class="filter-option"
            :class="{ active: filters.sortBy === 'price-asc' }"
            @click="filters.sortBy = 'price-asc'; handleFilterChange()"
          >
            价格从低到高
            <el-icon class="sort-icon"><Top /></el-icon>
          </span>
          <span
            class="filter-option"
            :class="{ active: filters.sortBy === 'price-desc' }"
            @click="filters.sortBy = 'price-desc'; handleFilterChange()"
          >
            价格从高到低
            <el-icon class="sort-icon"><Bottom /></el-icon>
          </span>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">价格区间</span>
        <div class="price-range">
          <el-input v-model="filters.minPrice" placeholder="最低价" class="price-input" @keyup.enter="handleFilterChange" />
          <span class="price-sep">—</span>
          <el-input v-model="filters.maxPrice" placeholder="最高价" class="price-input" @keyup.enter="handleFilterChange" />
          <el-button type="primary" size="small" @click="handleFilterChange">确定</el-button>
        </div>
      </div>
      <div class="filter-row">
        <span class="filter-label">关键词</span>
        <el-input
          v-model="filters.keyword"
          placeholder="搜索商品名称 / OEM号 / 品牌"
          class="keyword-input"
          clearable
          @keyup.enter="handleFilterChange"
        >
          <template #append>
            <el-button @click="handleFilterChange">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <div class="product-section">
      <div class="section-header">
        <span class="result-count">共 <strong>{{ total }}</strong> 件商品</span>
      </div>

      <div class="product-grid" v-loading="loading">
        <div v-for="product in products" :key="product.id" class="product-card" @click="goToDetail(product.id)">
          <div class="product-image">
            <el-image v-if="product.image" :src="getImageUrl(product.image)" fit="cover" class="product-img" />
            <div v-else class="no-image">
              <el-icon :size="48" color="#ccc"><Goods /></el-icon>
            </div>
            <div class="product-badge" v-if="product.stock <= 5 && product.stock > 0">库存紧张</div>
            <div class="product-badge sold-out" v-if="product.stock === 0">已售罄</div>
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <p class="product-sub">{{ product.subName }}</p>
            <div class="product-tags">
              <span v-if="product.brand" class="tag">{{ product.brand }}</span>
              <span v-if="product.oem" class="tag oem">{{ product.oem }}</span>
            </div>
            <div class="product-price-row">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
            <div class="product-meta">
              <span class="meta-item">
                <el-icon><Box /></el-icon>
                库存 {{ product.stock }}
              </span>
              <span class="meta-item">
                <el-icon><Star /></el-icon>
                {{ product.favoriteCount || 0 }}
              </span>
            </div>
          </div>
        </div>
        <el-empty v-if="!loading && products.length === 0" description="暂无商品" :image-size="120" />
      </div>

      <el-pagination
        v-if="total > 0"
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next"
        class="pagination-bar"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCategoryTree, getProductPage } from '@/api/product'

const router = useRouter()
const route = useRoute()
const categories = ref([])
const products = ref([])
const loading = ref(false)
const total = ref(0)
const selectedVehicle = ref(null)

const filters = reactive({
  categoryId: null,
  keyword: '',
  sortBy: 'default',
  vehicleModelId: null,
  minPrice: '',
  maxPrice: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 12
})

watch(() => route.query, (newQuery) => {
  if (newQuery.vehicleModelId) {
    filters.vehicleModelId = parseInt(newQuery.vehicleModelId)
    const vehicleStr = localStorage.getItem('selectedVehicle')
    if (vehicleStr) {
      selectedVehicle.value = JSON.parse(vehicleStr)
    }
  } else {
    filters.vehicleModelId = null
    selectedVehicle.value = null
  }
  if (newQuery.keyword !== undefined) {
    filters.keyword = newQuery.keyword || ''
  }
  if (newQuery.categoryId !== undefined) {
    filters.categoryId = newQuery.categoryId ? parseInt(newQuery.categoryId) : null
  }
  pagination.pageNum = 1
  loadProducts()
}, { deep: true })

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const getImageUrl = (image) => {
  if (!image) return ''
  const firstImage = image.split(',')[0]
  if (firstImage.startsWith('http')) return firstImage
  return firstImage
}

const clearVehicle = () => {
  selectedVehicle.value = null
  filters.vehicleModelId = null
  localStorage.removeItem('selectedVehicle')
  handleFilterChange()
}

const handleFilterChange = () => {
  pagination.pageNum = 1
  loadProducts()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadProducts()
}

const loadProducts = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: filters.keyword || undefined,
      categoryId: filters.categoryId || undefined,
      vehicleModelId: filters.vehicleModelId || undefined,
      minPrice: filters.minPrice || undefined,
      maxPrice: filters.maxPrice || undefined
    }
    if (filters.sortBy === 'price-asc') {
      params.sortBy = 'price'
      params.sortOrder = 'asc'
    } else if (filters.sortBy === 'price-desc') {
      params.sortBy = 'price'
      params.sortOrder = 'desc'
    }
    const res = await getProductPage(params)
    products.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error('加载商品失败', e)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategoryTree()
    categories.value = res.data
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

onMounted(() => {
  const vehicleStr = localStorage.getItem('selectedVehicle')
  if (vehicleStr) {
    selectedVehicle.value = JSON.parse(vehicleStr)
    filters.vehicleModelId = selectedVehicle.value.model.id
  }
  if (route.query.categoryId) {
    filters.categoryId = parseInt(route.query.categoryId)
  }
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
  if (route.query.vehicleModelId) {
    filters.vehicleModelId = parseInt(route.query.vehicleModelId)
  }
  loadCategories()
  loadProducts()
})
</script>

<style scoped>
.product-list-page {
  padding-bottom: 60px;
}

.breadcrumb {
  padding: 16px 0;
}

.filter-bar {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.filter-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 14px;
}

.filter-row:last-child {
  margin-bottom: 0;
}

.filter-label {
  font-weight: 600;
  color: #666;
  white-space: nowrap;
  width: 72px;
  flex-shrink: 0;
  font-size: 14px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-option {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  background: #f5f5f5;
  color: #666;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
  user-select: none;
}

.filter-option:hover {
  background: #fff0e6;
  color: #ff6600;
}

.filter-option.active {
  background: #ff6600;
  color: #fff;
}

.sort-icon {
  font-size: 12px;
}

.vehicle-tag {
  background: linear-gradient(135deg, #fff5f0, #fff0e6);
  color: #ff6600;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  border: 1px solid #ffe0cc;
}

.vehicle-close {
  cursor: pointer;
  margin-left: 8px;
  font-size: 14px;
}

.vehicle-close:hover {
  color: #e55a00;
}

.price-range {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-input {
  width: 120px;
}

.price-sep {
  color: #ccc;
}

.keyword-input {
  width: 360px;
}

.product-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.result-count {
  font-size: 14px;
  color: #999;
}

.result-count strong {
  color: #ff6600;
  font-size: 16px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  min-height: 200px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
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

.product-badge.sold-out {
  background: #999;
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
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
}

.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: #f0f0f0;
  color: #999;
}

.tag.oem {
  background: #e8f4ff;
  color: #409eff;
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
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #bbb;
}

.pagination-bar {
  margin-top: 24px;
  justify-content: center;
}
</style>