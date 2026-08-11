<template>
  <div id="app">
    <header class="site-header">
      <div class="nav-shell">
        <button class="brand-mark" type="button" @click="goHome" aria-label="返回首页">
          <span class="brand-orbit"></span>
          <span>
            <strong>羽迹</strong>
            <small>Badminton Club</small>
          </span>
        </button>

        <el-menu class="app-menu" mode="horizontal" router>
          <!-- 公共菜单项（未登录时显示） -->
          <template v-if="!userStore.isLoggedIn()">
            <el-menu-item index="/">首页</el-menu-item>
          </template>

          <!-- 用户端菜单 -->
          <template v-if="userStore.isLoggedIn() && userStore.role === 'user'">
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/activities">约球活动</el-menu-item>
            <el-menu-item index="/orders">我的订单</el-menu-item>
            <el-menu-item index="/venues">羽毛球场地</el-menu-item>
            <el-menu-item index="/forum">社区论坛</el-menu-item>
            <el-menu-item index="/profile">个人中心</el-menu-item>
            <el-menu-item index="/chat">我的好友</el-menu-item>
            <el-menu-item index="/find-partner">寻找球友</el-menu-item>
          </template>

          <!-- 商家端菜单 -->
          <template v-if="userStore.isLoggedIn() && userStore.role === 'merchant'">
            <template v-if="userStore.userInfo?.status === 1">
              <el-menu-item index="/merchant/venues">场地管理</el-menu-item>
              <el-menu-item index="/merchant/orders">订单管理</el-menu-item>
            </template>
            <el-menu-item index="/merchant/profile">商家资料</el-menu-item>
          </template>

          <!-- 管理员端菜单 -->
          <template v-if="userStore.isLoggedIn() && userStore.role === 'admin'">
            <el-menu-item index="/admin/dashboard">系统概览</el-menu-item>
            <el-menu-item index="/admin/users">用户管理</el-menu-item>
            <el-menu-item index="/admin/merchants">商家审核</el-menu-item>
            <el-menu-item index="/admin/posts">帖子审核</el-menu-item>
          </template>

          <div class="nav-spacer"></div>

          <!-- 未登录时显示登录/注册入口 -->
          <template v-if="!userStore.isLoggedIn()">
            <el-sub-menu index="login" popper-class="nav-dropdown">
              <template #title>登录</template>
              <el-menu-item index="/login">用户登录</el-menu-item>
              <el-menu-item index="/merchant/login">商家登录</el-menu-item>
              <el-menu-item index="/admin/login">管理员登录</el-menu-item>
            </el-sub-menu>
            <el-sub-menu index="register" popper-class="nav-dropdown">
              <template #title>注册</template>
              <el-menu-item index="/register">用户注册</el-menu-item>
              <el-menu-item index="/merchant/register">商家注册</el-menu-item>
            </el-sub-menu>
          </template>

          <!-- 已登录时显示用户信息和退出按钮 -->
          <template v-else>
            <el-sub-menu index="user" popper-class="nav-dropdown user-dropdown">
              <template #title>
                <span class="role-pill">{{ roleLabel }}</span>
                <el-avatar class="nav-avatar" :size="28" :src="userStore.userInfo?.avatar || ''" />
                <span class="nav-username">
                  {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                </span>
              </template>
              <el-menu-item @click="handleLogout">退出登录</el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </div>
    </header>

    <div class="page-shell">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { computed } from 'vue'

const userStore = useUserStore()
const router = useRouter()

const roleLabel = computed(() => {
  const roleMap = {
    user: '球友',
    merchant: '场馆主',
    admin: '管理员'
  }
  return roleMap[userStore.role] || '访客'
})

const goHome = () => {
  router.push('/')
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style>
:root {
  --nav-ink: #103c35;
  --nav-muted: #6d8c86;
  --nav-glow: #24d7b7;
  --nav-gold: #f5c15c;
  --nav-glass: rgba(255, 255, 255, 0.82);
  --nav-border: rgba(20, 118, 105, 0.14);
  --court-ink: #123b35;
  --court-title: #102f2a;
  --court-muted: #5f7d77;
  --court-soft: #f4fffb;
  --court-accent: #16b8a7;
  --court-accent-2: #67d7c8;
  --court-warm: #f5c15c;
  --court-card: rgba(255, 255, 255, 0.88);
  --court-border: rgba(33, 145, 130, 0.12);
  --court-shadow: 0 20px 56px rgba(38, 112, 101, 0.1);
}

body {
  margin: 0;
  min-width: 320px;
  color: var(--nav-ink);
  font-family: 'Avenir Next', 'Noto Sans SC', 'PingFang SC', sans-serif;
  background:
    radial-gradient(circle at 18% 0%, rgba(36, 215, 183, 0.16), transparent 30%),
    radial-gradient(circle at 88% 12%, rgba(245, 193, 92, 0.14), transparent 24%),
    #f6fbf9;
}

.site-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  padding: 14px 20px 8px;
  background:
    linear-gradient(180deg, rgba(246, 251, 249, 0.94), rgba(246, 251, 249, 0.72) 68%, transparent);
  backdrop-filter: blur(18px);
}

.nav-shell {
  position: relative;
  display: flex;
  align-items: center;
  gap: 18px;
  max-width: 1240px;
  min-height: 68px;
  margin: 0 auto;
  padding: 8px 12px 8px 18px;
  overflow: visible;
  border: 1px solid var(--nav-border);
  border-radius: 26px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.94), var(--nav-glass)),
    linear-gradient(90deg, rgba(36, 215, 183, 0.12), rgba(245, 193, 92, 0.08));
  box-shadow: 0 18px 50px rgba(24, 95, 84, 0.12);
}

.nav-shell::before {
  content: '';
  position: absolute;
  inset: 8px auto 8px 48%;
  width: 1px;
  background: linear-gradient(transparent, rgba(36, 215, 183, 0.34), transparent);
  pointer-events: none;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  flex: 0 0 auto;
  padding: 7px 12px 7px 8px;
  border: 0;
  border-radius: 20px;
  color: var(--nav-ink);
  background: transparent;
  cursor: pointer;
  transition:
    transform 0.24s ease,
    background 0.24s ease;
}

.brand-mark:hover {
  transform: translateY(-1px);
  background: rgba(36, 215, 183, 0.09);
}

.brand-orbit {
  position: relative;
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background:
    radial-gradient(circle at 58% 38%, #ffffff 0 14%, transparent 15%),
    conic-gradient(from 145deg, var(--nav-gold), var(--nav-glow), #117d72, var(--nav-gold));
  box-shadow: inset 0 0 0 6px rgba(255, 255, 255, 0.62), 0 8px 20px rgba(36, 215, 183, 0.28);
}

.brand-orbit::after {
  content: '';
  position: absolute;
  right: -3px;
  bottom: 7px;
  width: 16px;
  height: 3px;
  border-radius: 999px;
  background: var(--nav-ink);
  transform: rotate(-34deg);
  box-shadow: 6px 4px 0 -1px var(--nav-gold);
}

.brand-mark strong {
  display: block;
  font-size: 21px;
  line-height: 1;
  letter-spacing: 0.18em;
}

.brand-mark small {
  display: block;
  margin-top: 5px;
  color: var(--nav-muted);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.app-menu.el-menu {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 0;
  border-bottom: 0;
  background: transparent;
}

.app-menu .el-menu-item,
.app-menu .el-sub-menu__title {
  height: 44px;
  margin: 0 3px;
  padding: 0 14px;
  border-radius: 16px;
  border-bottom: 0 !important;
  color: var(--nav-ink);
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.02em;
  transition:
    color 0.2s ease,
    background 0.2s ease,
    box-shadow 0.2s ease,
    transform 0.2s ease;
}

.app-menu .el-menu-item:hover,
.app-menu .el-sub-menu__title:hover {
  color: #075e55;
  background: rgba(36, 215, 183, 0.12);
  transform: translateY(-1px);
}

.app-menu .el-menu-item.is-active,
.app-menu .el-sub-menu.is-active > .el-sub-menu__title {
  color: #073f38;
  background: linear-gradient(135deg, rgba(36, 215, 183, 0.2), rgba(245, 193, 92, 0.2));
  box-shadow: inset 0 0 0 1px rgba(36, 215, 183, 0.24);
}

.nav-spacer {
  flex: 1;
  min-width: 16px;
}

.role-pill {
  display: inline-flex;
  align-items: center;
  height: 24px;
  margin-right: 8px;
  padding: 0 9px;
  border-radius: 999px;
  color: #0a6057;
  background: rgba(36, 215, 183, 0.16);
  font-size: 12px;
  font-weight: 800;
}

.nav-avatar {
  margin-right: 8px;
  border: 2px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 5px 14px rgba(20, 118, 105, 0.18);
}

.nav-username {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-dropdown {
  border: 1px solid rgba(20, 118, 105, 0.14) !important;
  border-radius: 18px !important;
  overflow: hidden;
  box-shadow: 0 18px 44px rgba(24, 95, 84, 0.18) !important;
}

.nav-dropdown .el-menu {
  padding: 6px;
}

.nav-dropdown .el-menu-item {
  border-radius: 12px;
  font-weight: 700;
}

.page-shell {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 全局页面主题：统一未逐页重构页面的浅色晨光球馆风格 */
#app .user-orders,
#app .merchant-orders,
#app .merchant-venues,
#app .merchant-profile,
#app .merchant-detail,
#app .user-profile,
#app .admin-dashboard,
#app .admin-users,
#app .admin-merchants,
#app .post-management,
#app .posts,
#app .post-create,
#app .post-detail,
#app .friend-requests,
#app .find-partner,
#app .order-create,
#app .register-container,
#app .page-wrapper,
#app .chat-container {
  position: relative;
  min-height: calc(100vh - 130px);
  padding: 30px 4px 72px !important;
  overflow: hidden;
  color: var(--court-ink);
  background: transparent !important;
}

#app .user-orders::before,
#app .merchant-orders::before,
#app .merchant-venues::before,
#app .merchant-profile::before,
#app .merchant-detail::before,
#app .user-profile::before,
#app .admin-dashboard::before,
#app .admin-users::before,
#app .admin-merchants::before,
#app .post-management::before,
#app .posts::before,
#app .post-create::before,
#app .post-detail::before,
#app .friend-requests::before,
#app .find-partner::before,
#app .order-create::before,
#app .register-container::before,
#app .page-wrapper::before,
#app .chat-container::before {
  content: '';
  position: absolute;
  top: 28px;
  right: -130px;
  z-index: 0;
  width: 360px;
  height: 360px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(119, 231, 214, 0.24), transparent 68%);
  pointer-events: none;
}

#app .user-orders::after,
#app .merchant-orders::after,
#app .merchant-venues::after,
#app .merchant-profile::after,
#app .merchant-detail::after,
#app .user-profile::after,
#app .admin-dashboard::after,
#app .admin-users::after,
#app .admin-merchants::after,
#app .post-management::after,
#app .posts::after,
#app .post-create::after,
#app .post-detail::after,
#app .friend-requests::after,
#app .find-partner::after,
#app .order-create::after,
#app .register-container::after,
#app .page-wrapper::after,
#app .chat-container::after {
  content: '';
  position: absolute;
  left: -90px;
  bottom: 12%;
  z-index: 0;
  width: 300px;
  height: 300px;
  border-radius: 999px;
  background: radial-gradient(circle, rgba(255, 207, 112, 0.19), transparent 64%);
  pointer-events: none;
}

#app .page-title,
#app .card-title,
#app .subtitle,
#app .search-bar,
#app .order-filter,
#app .venue-actions,
#app .profile-card,
#app .stat-card,
#app .main-card,
#app .post-card,
#app .merchant-info-card,
#app .venue-card,
#app .partner-card,
#app .register-card,
#app .login-card,
#app .friend-panel,
#app .chat-window,
#app .chat-placeholder,
#app .el-card,
#app .el-table,
#app .pagination {
  position: relative;
  z-index: 1;
}

#app .page-title {
  margin: 0 0 24px !important;
  padding: 34px 38px !important;
  border: 1px solid var(--court-border);
  border-radius: 34px;
  color: var(--court-title) !important;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.96), rgba(246, 255, 252, 0.88)),
    radial-gradient(circle at 88% 8%, rgba(96, 221, 203, 0.22), transparent 30%);
  box-shadow: 0 24px 70px rgba(38, 112, 101, 0.12);
  font-size: clamp(34px, 4.5vw, 56px) !important;
  font-weight: 900 !important;
  line-height: 1 !important;
  letter-spacing: -0.06em;
  text-shadow: none !important;
}

#app .page-title::before {
  display: none !important;
}

#app .el-card,
#app .profile-card,
#app .stat-card,
#app .main-card,
#app .post-card,
#app .merchant-info-card,
#app .venue-card,
#app .partner-card,
#app .register-card,
#app .login-card,
#app .friend-panel,
#app .chat-window,
#app .chat-placeholder {
  border: 1px solid var(--court-border) !important;
  border-radius: 28px !important;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.94), rgba(250, 255, 253, 0.86)),
    radial-gradient(circle at 92% 12%, rgba(47, 201, 184, 0.12), transparent 26%) !important;
  box-shadow: var(--court-shadow) !important;
}

#app .el-card {
  overflow: hidden;
}

#app .el-card__header {
  border-bottom: 1px solid rgba(33, 145, 130, 0.1) !important;
  background: rgba(255, 255, 255, 0.52) !important;
}

#app .el-table {
  overflow: hidden;
  border: 1px solid var(--court-border) !important;
  border-radius: 26px !important;
  color: var(--court-ink);
  background: var(--court-card) !important;
  box-shadow: var(--court-shadow);
}

#app .el-table th.el-table__cell {
  color: #0d7368;
  background: #f4fffb !important;
  font-weight: 900;
}

#app .el-table td.el-table__cell {
  border-bottom-color: rgba(33, 145, 130, 0.08) !important;
}

#app .el-table__row:hover > td.el-table__cell {
  background: #f4fffb !important;
}

#app .search-bar,
#app .order-filter,
#app .venue-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 22px !important;
  padding: 16px !important;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 26px;
  background: linear-gradient(135deg, rgba(248, 255, 252, 0.86), rgba(255, 255, 255, 0.9));
  box-shadow: 0 16px 44px rgba(38, 112, 101, 0.08);
}

#app .el-input__wrapper,
#app .el-textarea__inner,
#app .el-select__wrapper,
#app .el-date-editor.el-input__wrapper,
#app .el-input-group__append {
  border-radius: 14px !important;
  background: #fff !important;
  box-shadow: inset 0 0 0 1px rgba(33, 145, 130, 0.12) !important;
}

#app .search-bar .el-input__wrapper,
#app .search-bar .el-input-group__append,
#app .order-filter .el-input__wrapper,
#app .order-filter .el-input-group__append {
  border-radius: 999px !important;
}

#app .el-button {
  border-radius: 999px !important;
  font-weight: 800 !important;
}

#app .el-button--primary {
  border: 0 !important;
  background: linear-gradient(135deg, var(--court-accent), var(--court-accent-2)) !important;
  box-shadow: 0 10px 22px rgba(22, 184, 167, 0.2);
}

#app .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(22, 184, 167, 0.28);
}

#app .el-tag {
  border-radius: 999px !important;
  font-weight: 800;
}

#app .pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px !important;
}

#app .el-pagination {
  padding: 10px 14px;
  border: 1px solid rgba(33, 145, 130, 0.1);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.82);
}

#app .el-pagination button,
#app .el-pagination .el-pager li {
  border-radius: 999px;
}

#app .el-pagination .el-pager li.is-active {
  color: #0d7368;
  background: rgba(47, 201, 184, 0.14);
}

#app .el-dialog {
  overflow: hidden;
  border: 1px solid rgba(33, 145, 130, 0.12);
  border-radius: 28px !important;
  background: rgba(255, 255, 255, 0.96) !important;
  box-shadow: 0 28px 80px rgba(38, 112, 101, 0.18) !important;
}

#app .el-dialog__title {
  color: #173b36;
  font-size: 22px;
  font-weight: 900;
}

#app .el-form-item__label {
  color: var(--court-muted);
  font-weight: 800;
}

#app .stat-value {
  color: #0d8f82 !important;
  font-size: 34px !important;
  font-weight: 900 !important;
}

#app .stat-label,
#app .info-label,
#app .venue-count,
#app .input-hint,
#app .card-subtitle {
  color: var(--court-muted) !important;
}

#app .price,
#app .price-text,
#app .total-price {
  color: #159f91 !important;
  font-weight: 900 !important;
}

#app .register-card,
#app .login-card {
  max-width: 520px;
  margin-inline: auto;
}

#app .header-bar {
  display: none !important;
}

#app .login-container {
  min-height: auto !important;
  padding: 20px 0 !important;
  background: transparent !important;
}

#app .icon-box {
  color: #0d7368 !important;
  background: linear-gradient(135deg, #dffbf4, #fff2d6) !important;
  box-shadow: 0 18px 42px rgba(20, 118, 105, 0.12);
}

#app .chat-container {
  display: grid !important;
  grid-template-columns: 320px minmax(0, 1fr);
  gap: 18px;
  height: calc(100vh - 180px) !important;
}

#app .friend-panel,
#app .chat-window,
#app .chat-placeholder {
  min-height: 0;
}

#app .friend-item {
  border-radius: 18px;
  transition: background 0.2s ease, transform 0.2s ease;
}

#app .friend-item:hover,
#app .friend-item.active {
  background: #f4fffb !important;
  transform: translateX(3px);
}

#app .message-content {
  border-radius: 18px !important;
}

#app .message-self .message-content {
  background: linear-gradient(135deg, var(--court-accent), var(--court-accent-2)) !important;
}

#app .participant-item,
#app .fac-tag {
  border: 0 !important;
  border-radius: 999px !important;
  color: #0d7368 !important;
  background: rgba(47, 201, 184, 0.14) !important;
  font-weight: 800;
}

#app .photo-preview,
#app .venue-photo,
#app .detail-photo,
#app .license-wrapper,
#app .preview-image-wrapper {
  border-radius: 22px !important;
  overflow: hidden;
}

@media (max-width: 760px) {
  #app .user-orders,
  #app .merchant-orders,
  #app .merchant-venues,
  #app .merchant-profile,
  #app .merchant-detail,
  #app .user-profile,
  #app .admin-dashboard,
  #app .admin-users,
  #app .admin-merchants,
  #app .post-management,
  #app .posts,
  #app .post-create,
  #app .post-detail,
  #app .friend-requests,
  #app .find-partner,
  #app .order-create,
  #app .register-container,
  #app .page-wrapper,
  #app .chat-container {
    padding-top: 12px !important;
  }

  #app .page-title {
    padding: 26px 20px !important;
    border-radius: 26px;
    font-size: 38px !important;
  }

  #app .search-bar,
  #app .order-filter,
  #app .venue-actions {
    align-items: stretch;
    flex-direction: column;
  }

  #app .search-input,
  #app .sort-select,
  #app .search-bar .el-input,
  #app .order-filter .el-radio-group {
    width: 100% !important;
  }

  #app .chat-container {
    grid-template-columns: 1fr;
    height: auto !important;
  }

  #app .friend-panel,
  #app .chat-window,
  #app .chat-placeholder {
    min-height: 360px;
  }

  #app .el-pagination {
    width: 100%;
    justify-content: center;
    border-radius: 22px;
  }
}

@media (max-width: 900px) {
  .site-header {
    padding-inline: 10px;
  }

  .nav-shell {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    border-radius: 22px;
  }

  .nav-shell::before {
    display: none;
  }

  .app-menu.el-menu {
    width: 100%;
    flex-wrap: wrap;
  }

  .nav-spacer {
    display: none;
  }
}

@media (max-width: 560px) {
  .brand-mark strong {
    font-size: 18px;
  }

  .app-menu .el-menu-item,
  .app-menu .el-sub-menu__title {
    height: 40px;
    padding: 0 10px;
    font-size: 13px;
  }
}
</style>