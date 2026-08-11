import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

// ==================== 公共页面 ====================
const HomePage = () => import('@/views/home/HomePage.vue')

// ==================== 用户端页面 ====================
const UserLogin = () => import('@/views/user/UserLogin.vue')
const UserRegister = () => import('@/views/user/UserRegister.vue')
const UserActivities = () => import('@/views/user/UserActivities.vue')
const UserMerchantList = () => import('@/views/user/UserMerchantList.vue')
const UserMerchantDetail = () => import('@/views/user/UserMerchantDetail.vue')
const UserOrders = () => import('@/views/user/UserOrders.vue')
const UserOrderCreate = () => import('@/views/user/UserOrderCreate.vue')
const UserPosts = () => import('@/views/user/UserPosts.vue')
const UserPostCreate = () => import('@/views/user/UserPostCreate.vue')
const UserPostDetail = () => import('@/views/user/UserPostDetail.vue')
const UserProfile = () => import('@/views/user/UserProfile.vue')
const FindPartner = () => import('@/views/user/FindPartner.vue')   

// ==================== 商家端页面 ====================
const MerchantLogin = () => import('@/views/merchant/MerchantLogin.vue')
const MerchantRegister = () => import('@/views/merchant/MerchantRegister.vue')
const MerchantVenues = () => import('@/views/merchant/MerchantVenues.vue')
const MerchantOrders = () => import('@/views/merchant/MerchantOrders.vue')
const MerchantProfile = () => import('@/views/merchant/MerchantProfile.vue')

// ==================== 管理员端页面 ====================
const AdminLogin = () => import('@/views/admin/AdminLogin.vue')
const AdminUsers = () => import('@/views/admin/AdminUsers.vue')
const AdminMerchants = () => import('@/views/admin/AdminMerchants.vue')
const AdminPosts = () => import('@/views/admin/AdminPosts.vue')
const AdminDashboard = () => import('@/views/admin/AdminDashboard.vue')

const routes = [
  // ---- 公开路由 ----
  { path: '/', name: 'home', component: HomePage },
  { path: '/login', name: 'userLogin', component: UserLogin },
  { path: '/register', name: 'userRegister', component: UserRegister },
  { path: '/merchant/login', name: 'merchantLogin', component: MerchantLogin },
  { path: '/merchant/register', name: 'merchantRegister', component: MerchantRegister },
  { path: '/admin/login', name: 'adminLogin', component: AdminLogin },

  // ---- 用户端路由 ----
  { path: '/activities', name: 'activities', component: UserActivities },

  // 场地（商家列表）
  { path: '/venues', name: 'venues', component: UserMerchantList },
  // 商家详情页（含该商家的场地列表）
  { path: '/merchant/:mid', name: 'merchantDetail', component: UserMerchantDetail },

  // 订单
  { path: '/orders', name: 'orders', component: UserOrders, meta: { requiresAuth: true, role: 'user' } },
  { path: '/orders/create/:vid', name: 'orderCreate', component: UserOrderCreate, meta: { requiresAuth: true, role: 'user' } },

  // 论坛
  { path: '/forum', name: 'posts', component: UserPosts },
  { path: '/forum/create', name: 'postCreate', component: UserPostCreate, meta: { requiresAuth: true, role: 'user' } },
  { path: '/forum/:pid', name: 'postDetail', component: UserPostDetail },

  // 个人中心 & 智能匹配
  { path: '/profile', name: 'userProfile', component: UserProfile, meta: { requiresAuth: true, role: 'user' } },
  { path: '/find-partner', name: 'findPartner', component: FindPartner, meta: { requiresAuth: true, role: 'user' } },
  { path: '/friend-requests', name: 'friendRequests', component: () => import('@/views/user/FriendRequests.vue'), meta: { requiresAuth: true, role: 'user' } },
  { path: '/chat', name: 'chat', component: () => import('@/views/user/Chat.vue'), meta: { requiresAuth: true, role: 'user' } },
  // ---- 商家端路由 ----
  { path: '/merchant/venues', name: 'merchantVenues', component: MerchantVenues, meta: { requiresAuth: true, role: 'merchant' } },
  { path: '/merchant/orders', name: 'merchantOrders', component: MerchantOrders, meta: { requiresAuth: true, role: 'merchant' } },
  { path: '/merchant/profile', name: 'merchantProfile', component: MerchantProfile, meta: { requiresAuth: true, role: 'merchant' } },

  // ---- 管理员端路由 ----
  { path: '/admin/users', name: 'adminUsers', component: AdminUsers, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/admin/merchants', name: 'adminMerchants', component: AdminMerchants, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/admin/posts', name: 'adminPosts', component: AdminPosts, meta: { requiresAuth: true, role: 'admin' } },
  { path: '/admin/dashboard', name: 'adminDashboard', component: AdminDashboard, meta: { requiresAuth: true, role: 'admin' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn()) {
    next('/login')
  } else if (to.meta.role && userStore.role !== to.meta.role) {
    ElMessage.error('无权限访问')
    next('/')
  } else {
    next()
  }
})

export default router