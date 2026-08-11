import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response) {
      // 处理错误响应
      switch (error.response.status) {
        case 401:
          // 未授权，清除token并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          window.location.href = '/auth/login'
          break
        case 403:
          // 禁止访问
          alert('无权限访问')
          break
        case 500:
          // 服务器错误
          alert('服务器错误')
          break
        default:
          alert(error.response.data.message || '请求失败')
      }
    }
    return Promise.reject(error)
  }
)

// 登录相关API
export const authAPI = {
  // 用户登录
  userLogin: (data) => api.post('/user/login', data),
  // 商家登录
  merchantLogin: (data) => api.post('/merchant/login', data),
  // 用户注册
  userRegister: (data) => api.post('/user/register', data),
  // 商家注册
  merchantRegister: (data) => api.post('/merchant/register', data)
}

// 用户相关API
export const userAPI = {
  // 获取用户信息
  getUserInfo: () => api.get('/user/info'),
  // 更新用户信息
  updateUserInfo: (data) => api.put('/user/update', data)
}

// 活动相关API
export const activityAPI = {
  // 创建活动
  createActivity: (data) => api.post('/user/activity/create', data),
  // 获取活动列表
  getActivityList: (params) => api.get('/user/activity/list', { params }),
  // 获取活动详情
  getActivityDetail: (aid) => api.get(`/user/activity/${aid}/detail`),  
  // 报名活动
  joinActivity: (aid) => api.post(`/user/activity/join/${aid}`),
  // 取消报名
  cancelActivity: (aid) => api.delete(`/user/activity/cancel/${aid}`),
  // 获取我创建的活动
  getMyCreatedActivities: () => api.get('/user/activity/my/created'),
  // 获取我参与的活动
  getMyJoinedActivities: () => api.get('/user/activity/my/joined'),
  //更新活动
  updateActivity: (data) => api.put('/user/activity/update', data),
}

// 场馆相关API
export const venueAPI = {
  // 获取场馆列表
  getVenueList: (params) => api.get('/user/venue/list', { params }),
  // 获取场馆详情
  getVenueDetail: (vid) => api.get(`/user/venue/${vid}`)
}

export default api