<template>
  <div class="review-section">
    <div class="review-summary" v-if="ratingInfo">
      <div class="summary-left">
        <div class="rating-score">{{ ratingInfo.averageRating?.toFixed(1) || '0.0' }}</div>
        <el-rate v-model="ratingInfo.averageRating" disabled :colors="['#ff6600', '#ff6600', '#ff6600']" />
        <div class="rating-count">{{ ratingInfo.reviewCount || 0 }} 条评价</div>
      </div>
      <div class="summary-right">
        <el-button type="primary" @click="showReviewDialog = true">发表评价</el-button>
      </div>
    </div>

    <div class="review-list" v-loading="loading">
      <div v-for="review in reviews" :key="review.id" class="review-item">
        <div class="review-header">
          <el-rate v-model="review.rating" disabled :colors="['#ff6600', '#ff6600', '#ff6600']" size="small" />
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <div class="review-content">{{ review.content }}</div>
        <div class="review-images" v-if="review.images">
          <el-image v-for="(img, idx) in review.images.split(',')" :key="idx"
                    :src="img" fit="cover"
                    style="width: 80px; height: 80px; margin-right: 8px; border-radius: 4px" />
        </div>
      </div>
      <el-empty v-if="!loading && reviews.length === 0" description="暂无评价" />
    </div>

    <el-pagination v-if="total > 10"
                   v-model:current-page="pageNum"
                   :page-size="10"
                   :total="total"
                   layout="prev, pager, next"
                   @current-change="loadReviews"
                   style="margin-top: 16px; justify-content: center" />

    <el-dialog v-model="showReviewDialog" title="发表评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :colors="['#ff6600', '#ff6600', '#ff6600']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="请输入评价内容" />
        </el-form-item>
        <el-form-item label="晒图">
          <el-input v-model="reviewForm.images" placeholder="请输入图片URL，多个用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReview" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getReviewList, getAverageRating, addReview } from '@/api/review'
import { ElMessage } from 'element-plus'

const props = defineProps({
  productId: Number
})

const reviews = ref([])
const ratingInfo = ref(null)
const loading = ref(false)
const pageNum = ref(1)
const total = ref(0)
const showReviewDialog = ref(false)
const submitting = ref(false)

const reviewForm = reactive({
  rating: 5,
  content: '',
  images: ''
})

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getReviewList({ productId: props.productId, pageNum: pageNum.value, pageSize: 10 })
    reviews.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error('加载评价失败', e)
  } finally {
    loading.value = false
  }
}

const loadRating = async () => {
  try {
    const res = await getAverageRating(props.productId)
    ratingInfo.value = res.data
  } catch (e) {
    console.error('加载评分失败', e)
  }
}

const handleSubmitReview = async () => {
  if (!reviewForm.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submitting.value = true
  try {
    await addReview({
      productId: props.productId,
      rating: reviewForm.rating,
      content: reviewForm.content,
      images: reviewForm.images
    })
    ElMessage.success('评价成功')
    showReviewDialog.value = false
    reviewForm.content = ''
    reviewForm.images = ''
    reviewForm.rating = 5
    pageNum.value = 1
    loadReviews()
    loadRating()
  } catch (e) {
    ElMessage.error('评价失败')
  } finally {
    submitting.value = false
  }
}

watch(() => props.productId, () => {
  pageNum.value = 1
  loadReviews()
  loadRating()
})

onMounted(() => {
  loadReviews()
  loadRating()
})
</script>

<style scoped>
.review-section {
  padding: 20px 0;
}

.review-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f8f8;
  border-radius: 8px;
  margin-bottom: 20px;
}

.summary-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.rating-score {
  font-size: 32px;
  font-weight: bold;
  color: #ff6600;
}

.rating-count {
  font-size: 14px;
  color: #999;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 8px;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
