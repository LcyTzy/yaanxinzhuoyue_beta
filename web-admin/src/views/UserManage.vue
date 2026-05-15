<template>
  <div class="user-manage">
    <div class="search-bar">
      <el-input v-model="searchForm.keyword" placeholder="搜索手机号/昵称" style="width: 300px" clearable @keyup.enter="handleSearch" />
      <el-button type="primary" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
      <el-button @click="handleReset">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe style="width: 100%; margin-top: 20px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'primary'">
            {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button :type="row.status === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button type="primary" size="small" @click="handleResetPwd(row)">重置密码</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next"
      style="margin-top: 20px; justify-content: flex-end"
      @current-change="getData"
      @size-change="getData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUserStatus, resetPassword } from '@/api/user-manage'

const loading = ref(false)
const tableData = ref([])

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      pageNum: pagination.current,
      pageSize: pagination.size,
      keyword: searchForm.keyword
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取用户列表失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  getData()
}

const handleReset = () => {
  searchForm.keyword = ''
  handleSearch()
}

const handleToggleStatus = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const action = newStatus === 1 ? '启用' : '禁用'
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', { type: 'warning' })
    await updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    getData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败', error)
    }
  }
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${row.nickname || row.phone}" 的密码吗？重置后密码为 123456`, '提示', { type: 'warning' })
    await resetPassword(row.id)
    ElMessage.success('密码重置成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败', error)
    }
  }
}

onMounted(() => {
  getData()
})
</script>

<style scoped>
.user-manage {
  padding: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
