<template>
  <div class="inventory-log-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存流水</span>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="变动类型">
          <el-select v-model="queryForm.changeType" placeholder="全部" clearable>
            <el-option label="采购入库" value="purchase_in" />
            <el-option label="销售出库" value="sale_out" />
            <el-option label="库存调整" value="adjustment" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" style="width: 100%">
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="changeType" label="变动类型">
          <template #default="{ row }">
            <el-tag v-if="row.changeType === 'purchase_in'" type="success">采购入库</el-tag>
            <el-tag v-else-if="row.changeType === 'sale_out'" type="danger">销售出库</el-tag>
            <el-tag v-else-if="row.changeType === 'adjustment'" type="warning">库存调整</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="changeQuantity" label="变动数量">
          <template #default="{ row }">
            <span :style="{ color: row.changeQuantity > 0 ? '#67C23A' : '#F56C6C' }">
              {{ row.changeQuantity > 0 ? '+' : '' }}{{ row.changeQuantity }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforeQuantity" label="变动前库存" />
        <el-table-column prop="afterQuantity" label="变动后库存" />
        <el-table-column prop="relatedOrderNo" label="关联单号" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="时间" />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dateRange = ref([])

const queryForm = reactive({
  changeType: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10
})

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      changeType: queryForm.changeType || undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const res = await request.get('/admin/inventory/log', { params })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.inventory-log-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}
</style>
