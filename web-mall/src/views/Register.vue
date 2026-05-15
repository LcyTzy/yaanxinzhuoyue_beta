<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="register-title">新卓阅汽配 - 注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" class="register-form">
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" size="large" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="captcha">
          <div class="captcha-row">
            <el-input v-model="form.captcha" placeholder="验证码" size="large" style="flex: 1" />
            <img :src="captchaImage" class="captcha-img" @click="refreshCaptcha" alt="验证码" />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleRegister" :loading="loading">注册</el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <el-button type="primary" link @click="$router.push('/login')">立即登录</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { register, getCaptcha } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const captchaImage = ref('')
const captchaKey = ref('')

const form = reactive({ nickname: '', phone: '', password: '', captcha: '' })
const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    captchaImage.value = res.data.image
    captchaKey.value = res.data.key
  } catch (e) {
    console.error('获取验证码失败', e)
  }
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({ ...form, captchaKey: captchaKey.value })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message || '注册失败')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>

<style scoped>
.register-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.register-card { background: #fff; border-radius: 16px; padding: 40px; width: 400px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
.register-title { text-align: center; margin-bottom: 30px; color: #333; }
.register-form { margin-top: 20px; }
.captcha-row { display: flex; gap: 12px; align-items: center; }
.captcha-img { height: 40px; cursor: pointer; border-radius: 4px; }
.register-footer { text-align: center; color: #999; font-size: 14px; }
</style>
