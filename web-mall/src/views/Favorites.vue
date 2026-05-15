<template>
  <div class="favorites-page">
    <h2 class="page-title">我的收藏</h2>
    <div class="favorites-content" v-loading="loading">
      <template v-if="favorites.length > 0">
        <div class="favorites-grid">
          <div v-for="item in favorites" :key="item.id" class="favorite-card">
            <div class="product-image" @click="$router.push(`/product/${item.productId}`)">
              <el-icon v-if="!item.productImage" :size="48" color="#ccc"><Goods /></el-icon>
              <img v-else :src="item.productImage" alt="" />
            </div>
            <div class="product-info">
              <h4 class="product-name" @click="$router.push(`/product/${item.productId}`)">{{ item.productName }}</h4>
              <p class="product-sub">{{ item.productSubName }}</p>
              <div class="product-price">¥{{ item.price }}</div>
              <div class="product-actions">
                <el-button type="primary" size="small" @click="$router.push(`/product/${item.productId}`)">查看详情</el-button>
                <el-button type="danger" size="small" plain @click="handleRemove(item.productId)">取消收藏</el-button>
              </div>
            </div>
          </div>
        </div>
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="loadFavorites"
          />
        </div>
      </template>
      <el-empty v-else description="还没有收藏的商品" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getFavoriteList, toggleFavorite } from '@/api/favorite'
import { ElMessage } from 'element-plus'

const favorites = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

const loadFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavoriteList({ pageNum: pageNum.value, pageSize: pageSize.value })
    favorites.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载收藏列表失败', e)
  } finally {
    loading.value = false
  }
}

const handleRemove = async (productId) => {
  try {
    await toggleFavorite(productId)
    ElMessage.success('已取消收藏')
    loadFavorites()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadFavorites)
</script>

<style scoped>
.favorites-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.favorites-content {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.favorites-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.favorite-card {
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.favorite-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.product-image {
  height: 180px;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  margin-bottom: 4px;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-name:hover {
  color: #ff6600;
}

.product-sub {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 18px;
  color: #ff6600;
  font-weight: bold;
  margin-bottom: 12px;
}

.product-actions {
  display: flex;
  gap: 8px;
}

.product-actions .el-button {
  flex: 1;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}
</style>
