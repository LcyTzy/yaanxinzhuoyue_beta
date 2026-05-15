<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="login-title">新卓阅汽配 - 登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="primary" link @click="$router.push('/register')">立即注册</el-button>
          <span style="margin-left: 16px">
            <el-button type="primary" link @click="$router.push('/forgot-password')">忘记密码？</el-button>
          </span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login({ phone: form.phone, password: form.password })
    userStore.setToken(res.data.token)
    userStore.setInfo(res.data.user)
    ElMessage.success('登录成功')
    if (res.data.user && res.data.user.role === 'ADMIN') {
      window.location.href = '/admin/'
    } else {
      router.push('/home')
    }
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { background: #fff; border-radius: 16px; padding: 40px; width: 400px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
.login-title { text-align: center; margin-bottom: 30px; color: #333; }
.login-form { margin-top: 20px; }
.login-footer { text-align: center; color: #999; font-size: 14px; }
</style>
