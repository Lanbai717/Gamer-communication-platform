<template>
  <div class="page-wrapper">
    <!-- 顶部导航栏 → 只保留角色切换 -->
    <header class="header-bar">
      <div class="header-inner">
        <div class="logo">首页</div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleLoginTypeChange">
            <span class="dropdown-btn">
              {{ currentLoginText }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="user">用户登录</el-dropdown-item>
                <el-dropdown-item command="merchant">商家登录</el-dropdown-item>
                <el-dropdown-item command="admin">管理员登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 登录主区域 -->
    <div class="login-container">
      <div class="login-card-wrapper">
        <el-card class="login-card" shadow="hover">
          <div class="card-header">
            <div class="icon-box">
              <el-icon size="36" color="#26aea7">
                <User />
              </el-icon>
            </div>
            <h2 class="card-title">{{ currentLoginText }}</h2>
            <p class="card-subtitle">欢迎回来，请登录您的商家账号</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" label-width="0px" class="login-form">
            <el-form-item prop="username">
              <div class="input-wrapper">
                <el-icon class="input-icon">
                  <User />
                </el-icon>
                <el-input 
                  v-model="form.username" 
                  placeholder="请输入用户名" 
                  class="custom-input"
                />
              </div>
            </el-form-item>
            <el-form-item prop="password">
              <div class="input-wrapper">
                <el-icon class="input-icon">
                  <Lock />
                </el-icon>
                <el-input 
                  v-model="form.password" 
                  type="password" 
                  placeholder="请输入密码" 
                  show-password
                  class="custom-input"
                />
              </div>
            </el-form-item>

            <el-form-item>
              <el-button 
                type="primary" 
                @click="handleLogin" 
                :loading="loading" 
                class="login-btn"
              >
                登 录
              </el-button>
            </el-form-item>

            <div class="form-footer">
              <span class="footer-text">还没有商家账号？</span>
              <router-link to="/merchant/register" class="register-link">立即注册</router-link>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/merchant'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

// 登录类型
const loginType = ref('merchant')
const currentLoginText = computed(() => {
  const map = {
    user: '用户登录',
    merchant: '商家登录',
    admin: '管理员登录'
  }
  return map[loginType.value]
})

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 角色切换跳转
const handleLoginTypeChange = (command) => {
  if (command === 'user') router.push('/login')
  if (command === 'merchant') router.push('/merchant/login')
  if (command === 'admin') router.push('/admin/login')
}

// 商家登录逻辑（完全不变）
const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    userStore.setToken(res.token)
    userStore.setUserInfo(res.userInfo)
    userStore.setRole('merchant')
    ElMessage.success('登录成功')
    router.push('/merchant/profile')
  } catch (error) {
    // 已处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 全局容器 */
.page-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.header-bar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid rgba(38, 174, 167, 0.1);
  z-index: 10;
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.logo {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.dropdown-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  color: #26aea7;
  cursor: pointer;
}

/* 全屏登录区域，无白边 */
.login-container {
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

.login-card-wrapper {
  position: relative;
  z-index: 1;
}
.login-card {
  width: 420px;
  padding: 35px 30px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 12px 40px rgba(38, 174, 167, 0.15);
}
.card-header {
  text-align: center;
  margin-bottom: 30px;
}
.icon-box {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #26aea7 0%, #38c5b7 100%);
  border-radius: 50%;
  margin-bottom: 12px;
}
.card-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}
.card-subtitle {
  margin: 0;
  font-size: 14px;
  color: #999;
}

/* 表单居中 */
.login-form {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.login-form :deep(.el-form-item) {
  width: 100%;
  max-width: 300px;
  margin-bottom: 22px;
}

/* 输入框图标布局 */
.input-wrapper {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaa;
  z-index: 1;
  font-size: 18px;
  pointer-events: none;
}

.custom-input :deep(.el-input__wrapper) {
  padding-left: 44px !important;
  padding-right: 40px !important;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
}

.custom-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 3px 8px rgba(38, 174, 167, 0.2);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(38, 174, 167, 0.2);
}

.login-btn {
  width: 300px;
  height: 46px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #26aea7 0%, #38c5b7 100%);
  border: none;
  transition: all 0.3s ease;
}
.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(38, 174, 167, 0.3);
}

.form-footer {
  text-align: center;
  margin-top: 10px;
  font-size: 14px;
}
.footer-text {
  color: #666;
}
.register-link {
  color: #26aea7;
  font-weight: 500;
  text-decoration: none;
}
.register-link:hover {
  color: #38c5b7;
  text-decoration: underline;
}
</style>