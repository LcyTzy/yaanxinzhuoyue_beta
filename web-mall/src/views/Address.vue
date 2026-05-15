<template>
  <div class="address-page">
    <h2 class="page-title">收货地址管理</h2>
    <div class="address-content">
      <div class="address-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增地址
        </el-button>
      </div>
      <div class="address-list" v-loading="loading">
        <template v-if="addresses.length > 0">
          <div v-for="addr in addresses" :key="addr.id" class="address-card" :class="{ 'is-default': addr.isDefault === 1 }">
            <div class="address-info">
              <div class="address-row">
                <span class="receiver-name">{{ addr.receiverName }}</span>
                <span class="receiver-phone">{{ addr.receiverPhone }}</span>
                <el-tag v-if="addr.isDefault === 1" type="danger" size="small">默认</el-tag>
              </div>
              <div class="address-detail">
                {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
              </div>
            </div>
            <div class="address-actions">
              <el-button type="primary" link size="small" @click="handleEdit(addr)">编辑</el-button>
              <el-button type="primary" link size="small" @click="handleSetDefault(addr.id)" v-if="addr.isDefault !== 1">设为默认</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(addr.id)">删除</el-button>
            </div>
          </div>
        </template>
        <el-empty v-else description="还没有收货地址" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader v-model="form.region" :options="regionOptions" placeholder="请选择省市区" style="width: 100%" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefault } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const addresses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  region: [],
  detail: '',
  isDefault: 0
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [{ required: true, message: '请选择所在地区', trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const regionOptions = [
  { value: '北京市', label: '北京市', children: [{ value: '北京市', label: '北京市', children: [{ value: '东城区', label: '东城区' }, { value: '西城区', label: '西城区' }, { value: '朝阳区', label: '朝阳区' }, { value: '海淀区', label: '海淀区' }] }] },
  { value: '上海市', label: '上海市', children: [{ value: '上海市', label: '上海市', children: [{ value: '黄浦区', label: '黄浦区' }, { value: '徐汇区', label: '徐汇区' }, { value: '浦东新区', label: '浦东新区' }] }] },
  { value: '广东省', label: '广东省', children: [{ value: '广州市', label: '广州市', children: [{ value: '天河区', label: '天河区' }, { value: '越秀区', label: '越秀区' }, { value: '海珠区', label: '海珠区' }] }, { value: '深圳市', label: '深圳市', children: [{ value: '南山区', label: '南山区' }, { value: '福田区', label: '福田区' }, { value: '宝安区', label: '宝安区' }] }] },
  { value: '浙江省', label: '浙江省', children: [{ value: '杭州市', label: '杭州市', children: [{ value: '西湖区', label: '西湖区' }, { value: '上城区', label: '上城区' }, { value: '拱墅区', label: '拱墅区' }] }, { value: '宁波市', label: '宁波市', children: [{ value: '海曙区', label: '海曙区' }, { value: '江北区', label: '江北区' }] }] },
  { value: '江苏省', label: '江苏省', children: [{ value: '南京市', label: '南京市', children: [{ value: '玄武区', label: '玄武区' }, { value: '秦淮区', label: '秦淮区' }, { value: '建邺区', label: '建邺区' }] }, { value: '苏州市', label: '苏州市', children: [{ value: '姑苏区', label: '姑苏区' }, { value: '工业园区', label: '工业园区' }] }] }
]

const loadAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddressList()
    addresses.value = res.data || []
  } catch (e) {
    console.error('加载地址列表失败', e)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, receiverName: '', receiverPhone: '', region: [], detail: '', isDefault: 0 })
  dialogVisible.value = true
}

const handleEdit = (addr) => {
  isEdit.value = true
  Object.assign(form, {
    id: addr.id,
    receiverName: addr.receiverName,
    receiverPhone: addr.receiverPhone,
    region: [addr.province, addr.city, addr.district],
    detail: addr.detail,
    isDefault: addr.isDefault
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const data = {
      ...form,
      province: form.region[0] || '',
      city: form.region[1] || '',
      district: form.region[2] || ''
    }
    delete data.region
    if (isEdit.value) {
      await updateAddress(data)
      ElMessage.success('更新成功')
    } else {
      await addAddress(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadAddresses()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleSetDefault = async (id) => {
  try {
    await setDefault(id)
    ElMessage.success('设置成功')
    loadAddresses()
  } catch (e) {
    ElMessage.error('设置失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' })
    await deleteAddress(id)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(loadAddresses)
</script>

<style scoped>
.address-page {
  padding-bottom: 40px;
}

.page-title {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6600;
}

.address-content {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.address-header {
  margin-bottom: 20px;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-card {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s;
}

.address-card.is-default {
  border-color: #ff6600;
  background: #fff8f5;
}

.address-info {
  flex: 1;
}

.address-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-weight: bold;
  font-size: 16px;
}

.receiver-phone {
  color: #666;
}

.address-detail {
  color: #666;
  font-size: 14px;
}

.address-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}
</style>
