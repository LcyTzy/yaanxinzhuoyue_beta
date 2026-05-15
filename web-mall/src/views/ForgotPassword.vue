<template>
  <div class="forgot-page">
    <div class="forgot-card">
      <h2 class="forgot-title">找回密码</h2>
      <el-steps :active="step" finish-status="success" style="margin-bottom: 30px">
        <el-step title="验证手机号" />
        <el-step title="设置新密码" />
      </el-steps>

      <el-form v-if="step === 0" :model="form1" :rules="rules1" ref="form1Ref" class="forgot-form">
        <el-form-item prop="phone">
          <el-input v-model="form1.phone" placeholder="请输入注册手机号" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="code">
          <div style="display: flex; gap: 10px; width: 100%">
            <el-input v-model="form1.code" placeholder="请输入验证码" size="large" prefix-icon="Key" />
            <el-button type="primary" size="large" @click="handleSendCode" :disabled="countdown > 0" style="white-space: nowrap">
              {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleVerify" :loading="loading">下一步</el-button>
        </el-form-item>
      </el-form>

      <el-form v-if="step === 1" :model="form2" :rules="rules2" ref="form2Ref" class="forgot-form">
        <el-form-item prop="newPassword">
          <el-input v-model="form2.newPassword" type="password" placeholder="请输入新密码（至少6位）" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form2.confirmPassword" type="password" placeholder="请确认新密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleReset" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleReset" :loading="loading">确认重置</el-button>
        </el-form-item>
      </el-form>

      <div v-if="step === 2" class="success-msg">
        <el-icon :size="60" color="#67c23a"><CircleCheck /></el-icon>
        <p style="margin-top: 16px; font-size: 18px; color: #67c23a">密码重置成功！</p>
        <el-button type="primary" size="large" style="margin-top: 20px" @click="$router.push('/login')">返回登录</el-button>
      </div>

      <div class="forgot-footer">
        <el-button type="primary" link @click="$router.push('/login')">返回登录</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { sendResetCode, resetPassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'

const router = useRouter()
const form1Ref = ref(null)
const form2Ref = ref(null)
const loading = ref(false)
const step = ref(0)
const countdown = ref(0)
let timer = null

const form1 = reactive({ phone: '', code: '' })
const rules1 = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const form2 = reactive({ newPassword: '', confirmPassword: '' })
const rules2 = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form2.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleSendCode = async () => {
  const valid = await form1Ref.value.validateField('phone').catch(() => false)
  if (!valid) return
  try {
    await sendResetCode({ phone: form1.phone })
    ElMessage.success('验证码已发送')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    ElMessage.error(e.message || '发送失败')
  }
}

const handleVerify = async () => {
  const valid = await form1Ref.value.validate().catch(() => false)
  if (!valid) return
  step.value = 1
}

const handleReset = async () => {
  const valid = await form2Ref.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await resetPassword({
      phone: form1.phone,
      code: form1.code,
      newPassword: form2.newPassword
    })
    step.value = 2
  } catch (e) {
    ElMessage.error(e.message || '重置失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.forgot-card { background: #fff; border-radius: 16px; padding: 40px; width: 420px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); }
.forgot-title { text-align: center; margin-bottom: 30px; color: #333; }
.forgot-form { margin-top: 20px; }
.forgot-footer { text-align: center; margin-top: 16px; }
.success-msg { text-align: center; padding: 30px 0; }
</style>
