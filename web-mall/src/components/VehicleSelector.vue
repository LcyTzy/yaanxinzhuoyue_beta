<template>
  <el-dialog v-model="visible" title="选择您的车型" width="600px" @close="handleClose">
    <div class="vehicle-selector">
      <div class="selector-step">
        <h4>1. 选择品牌</h4>
        <div class="brand-grid" v-loading="brandsLoading">
          <div v-for="brand in brands" :key="brand.id" 
               class="brand-item" 
               :class="{ active: selectedBrand?.id === brand.id }"
               @click="selectBrand(brand)">
            {{ brand.name }}
          </div>
        </div>
      </div>

      <div class="selector-step" v-if="selectedBrand">
        <h4>2. 选择车系</h4>
        <div class="series-list" v-loading="seriesLoading">
          <div v-for="series in series" :key="series.id"
               class="series-item"
               :class="{ active: selectedSeries?.id === series.id }"
               @click="selectSeries(series)">
            {{ series.name }}
          </div>
        </div>
      </div>

      <div class="selector-step" v-if="selectedSeries">
        <h4>3. 选择车型</h4>
        <div class="model-list" v-loading="modelsLoading">
          <div v-for="model in models" :key="model.id"
               class="model-item"
               :class="{ active: selectedModel?.id === model.id }"
               @click="selectModel(model)">
            <div class="model-name">{{ model.name }}</div>
            <div class="model-detail">{{ model.year }}款 | {{ model.displacement }} | {{ model.transmission }}</div>
          </div>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="!selectedModel" @click="handleConfirm">确认选择</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getBrands, getSeries, getModels } from '@/api/vehicle'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = ref(false)
const brands = ref([])
const series = ref([])
const models = ref([])
const brandsLoading = ref(false)
const seriesLoading = ref(false)
const modelsLoading = ref(false)
const selectedBrand = ref(null)
const selectedSeries = ref(null)
const selectedModel = ref(null)

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    loadBrands()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const loadBrands = async () => {
  brandsLoading.value = true
  try {
    const res = await getBrands()
    brands.value = res.data || []
  } catch (e) {
    console.error('加载品牌失败', e)
  } finally {
    brandsLoading.value = false
  }
}

const selectBrand = async (brand) => {
  selectedBrand.value = brand
  selectedSeries.value = null
  selectedModel.value = null
  seriesLoading.value = true
  try {
    const res = await getSeries(brand.id)
    series.value = res.data || []
  } catch (e) {
    console.error('加载车系失败', e)
  } finally {
    seriesLoading.value = false
  }
}

const selectSeries = async (seriesItem) => {
  selectedSeries.value = seriesItem
  selectedModel.value = null
  modelsLoading.value = true
  try {
    const res = await getModels(seriesItem.id)
    models.value = res.data || []
  } catch (e) {
    console.error('加载车型失败', e)
  } finally {
    modelsLoading.value = false
  }
}

const selectModel = (model) => {
  selectedModel.value = model
}

const handleConfirm = () => {
  emit('confirm', {
    brand: selectedBrand.value,
    series: selectedSeries.value,
    model: selectedModel.value
  })
  visible.value = false
}

const handleClose = () => {
  selectedBrand.value = null
  selectedSeries.value = null
  selectedModel.value = null
  series.value = []
  models.value = []
}
</script>

<style scoped>
.vehicle-selector {
  max-height: 500px;
  overflow-y: auto;
}

.selector-step {
  margin-bottom: 24px;
}

.selector-step h4 {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}

.brand-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
}

.brand-item {
  padding: 12px;
  text-align: center;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.brand-item:hover {
  border-color: #ff6600;
  color: #ff6600;
}

.brand-item.active {
  border-color: #ff6600;
  background: #fff5f0;
  color: #ff6600;
  font-weight: bold;
}

.series-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.series-item {
  padding: 8px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.series-item:hover {
  border-color: #ff6600;
  color: #ff6600;
}

.series-item.active {
  border-color: #ff6600;
  background: #ff6600;
  color: #fff;
}

.model-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.model-item {
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.model-item:hover {
  border-color: #ff6600;
}

.model-item.active {
  border-color: #ff6600;
  background: #fff5f0;
}

.model-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.model-detail {
  font-size: 12px;
  color: #999;
}
</style>
