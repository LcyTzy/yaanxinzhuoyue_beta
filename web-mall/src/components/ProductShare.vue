<template>
  <div class="share-component">
    <el-popover placement="top" :width="280" trigger="click">
      <template #reference>
        <el-button type="primary" link>
          <el-icon><Share /></el-icon>
          分享
        </el-button>
      </template>
      <div class="share-content">
        <p class="share-title">分享商品</p>
        <div class="share-link">
          <el-input v-model="shareUrl" readonly size="small">
            <template #append>
              <el-button @click="copyLink">复制</el-button>
            </template>
          </el-input>
        </div>
        <div class="share-actions">
          <div class="share-item" @click="shareTo('wechat')">
            <div class="share-icon wechat">微信</div>
            <span>微信</span>
          </div>
          <div class="share-item" @click="shareTo('weibo')">
            <div class="share-icon weibo">微博</div>
            <span>微博</span>
          </div>
          <div class="share-item" @click="shareTo('qq')">
            <div class="share-icon qq">QQ</div>
            <span>QQ</span>
          </div>
          <div class="share-item" @click="shareTo('link')">
            <div class="share-icon link">链接</div>
            <span>复制链接</span>
          </div>
        </div>
      </div>
    </el-popover>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  productId: Number,
  productName: String
})

const shareUrl = computed(() => {
  return window.location.origin + '/product/' + props.productId
})

const copyLink = () => {
  navigator.clipboard.writeText(shareUrl.value).then(() => {
    ElMessage.success('链接已复制')
  })
}

const shareTo = (type) => {
  const title = props.productName || '新卓阅汽配'
  const url = shareUrl.value
  const text = '我在新卓阅汽配发现了一个不错的商品，快来看看吧！'

  switch (type) {
    case 'weibo':
      window.open('https://service.weibo.com/share/share.php?title=' + encodeURIComponent(text) + '&url=' + encodeURIComponent(url))
      break
    case 'qq':
      window.open('https://connect.qq.com/widget/shareqq/index.html?title=' + encodeURIComponent(title) + '&url=' + encodeURIComponent(url))
      break
    case 'link':
      copyLink()
      break
    default:
      copyLink()
  }
}
</script>

<style scoped>
.share-content {
  text-align: center;
}

.share-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 12px;
}

.share-link {
  margin-bottom: 16px;
}

.share-actions {
  display: flex;
  justify-content: space-around;
}

.share-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  gap: 4px;
  font-size: 12px;
  color: #666;
}

.share-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.wechat { background: #07C160; }
.weibo { background: #E6162D; }
.qq { background: #12B7F5; }
.link { background: #409EFF; }
</style>