<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="字母、数字、下划线，3-20位" />
          <div class="input-hint">用户名用于登录，一经注册不可修改</div>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
          <div class="input-hint">至少8位，包含字母和数字</div>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password />
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="2-20个字符，可包含中文" />
          <div class="input-hint">昵称将展示给其他用户，可随时修改</div>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
          <el-button size="small" @click="sendCode" :disabled="codeDisabled" style="margin-left: 10px">
            {{ codeButtonText }}
          </el-button>
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码" />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item>
          <el-checkbox v-model="agree">我已阅读并同意《用户协议》</el-checkbox>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%">注册</el-button>
        </el-form-item>
        <div class="login-tip">已有账号？<router-link to="/login">立即登录</router-link></div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, sendCode as apiSendCode } from '@/api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const agree = ref(false)

const codeDisabled = ref(false)
const codeButtonText = ref('获取验证码')
let timer = null

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  code: '',
  phone: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{3,20}$/, message: '用户名由字母、数字、下划线组成，长度3-20位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, message: '密码至少8位', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d).+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirm, trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const sendCode = async () => {
  if (!form.email) {
    ElMessage.warning('请先填写邮箱')
    return
  }
  const emailRegex = /^[^\s@]+@([^\s@]+\.)+[^\s@]+$/
  if (!emailRegex.test(form.email)) {
    ElMessage.error('邮箱格式不正确')
    return
  }

  codeDisabled.value = true
  codeButtonText.value = '发送中...'
  try {
    await apiSendCode(form.email)
    ElMessage.success('验证码已发送，请查收邮件')
    let count = 60
    timer = setInterval(() => {
      if (count <= 0) {
        clearInterval(timer)
        codeDisabled.value = false
        codeButtonText.value = '获取验证码'
      } else {
        codeButtonText.value = `${count}秒后重试`
        count--
      }
    }, 1000)
  } catch (error) {
    ElMessage.error('发送失败，请稍后重试')
    codeDisabled.value = false
    codeButtonText.value = '获取验证码'
  }
}

const handleRegister = async () => {
  if (!agree.value) {
    ElMessage.warning('请阅读并同意用户协议')
    return
  }
  await formRef.value.validate()
  loading.value = true
  try {
    const { confirmPassword, ...data } = form
    await register(data)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 全屏渐变背景（和登录页完全一致） */
.register-container {
  flex: 1;
  margin-top: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 60px);
  background: linear-gradient(135deg, #e0f7f6 0%, #b2ebf2 100%);
  background-size: 200% 200%;
  animation: bgGradient 12s ease infinite;
  overflow: hidden;
}

@keyframes bgGradient {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 毛玻璃注册卡片 */
.register-card {
  width: 580px;
  padding: 45px 45px;
  border-radius: 20px;
  border: none;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 15px 35px rgba(38, 174, 167, 0.12);
  transition: all 0.3s ease;
}

.register-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(38, 174, 167, 0.18);
}

/* 标题 */
:deep(h2) {
  text-align: center;
  margin: 0 0 36px 0;
  font-size: 26px;
  font-weight: 600;
  color: #26aea7;
  letter-spacing: 1px;
}

/* 表单项 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 输入框聚焦高亮 */
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #26aea7, 0 0 0 6px rgba(38, 174, 167, 0.12) !important;
}

/* 按钮样式统一 */
:deep(.el-button--primary) {
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(to right, #26aea7, #38c5b7) !important;
  border: none !important;
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(38, 174, 167, 0.3);
}

/* 提示文字 */
.input-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.login-tip {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-tip :deep(a) {
  color: #26aea7;
  font-weight: 500;
  text-decoration: none;
}

.login-tip :deep(a:hover) {
  color: #38c5b7;
  text-decoration: underline;
}
</style>