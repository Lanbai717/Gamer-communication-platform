<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2>商家注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <!-- 用户名 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="字母、数字、下划线，3-20位" />
          <div class="input-hint">用户名用于登录，一经注册不可修改</div>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
          <div class="input-hint">至少8位，包含字母和数字</div>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" show-password />
          <div class="input-hint">请再次输入密码</div>
        </el-form-item>

        <!-- 场馆名称 -->
        <el-form-item label="场馆名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入场馆名称" />
          <div class="input-hint">场馆名称将展示给用户，请填写真实名称</div>
        </el-form-item>

        <!-- 联系人 -->
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人姓名" />
          <div class="input-hint">负责人姓名，便于沟通</div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width:100%">注册</el-button>
        </el-form-item>
        <div class="login-link">已有账号？<router-link to="/merchant/login">立即登录</router-link></div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/merchant'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  companyName: '',
  contactPerson: ''
})

// 自定义校验：密码强度
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (!/^(?=.*[A-Za-z])(?=.*\d).+$/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

// 自定义校验：确认密码
const validateConfirmPassword = (rule, value, callback) => {
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
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  companyName: [
    { required: true, message: '请输入场馆名称', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: '请输入联系人', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const { confirmPassword, ...data } = form
    await register(data)
    ElMessage.success('注册成功，请登录后完善资料')
    router.push('/merchant/login')
  } catch (error) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 全屏渐变背景（整套系统统一） */
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

/* 毛玻璃卡片统一风格 */
.register-card {
  width: 580px;
  padding: 40px 45px;
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

/* 标题统一 */
:deep(h2) {
  text-align: center;
  margin: 0 0 36px 0;
  font-size: 26px;
  font-weight: 600;
  color: #26aea7;
  letter-spacing: 1px;
}

/* 表单项统一间距 */
:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-form-item__label) {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 输入框高亮统一 */
:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #26aea7, 0 0 0 6px rgba(38, 174, 167, 0.12) !important;
}

/* 按钮渐变统一 */
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

/* 登录链接 */
.login-link {
  text-align: center;
  font-size: 14px;
  color: #666;
}

.login-link :deep(a) {
  color: #26aea7;
  font-weight: 500;
  text-decoration: none;
}

.login-link :deep(a:hover) {
  color: #38c5b7;
  text-decoration: underline;
}
</style>