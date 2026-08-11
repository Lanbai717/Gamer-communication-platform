import request from '@/utils/request'

// 管理员登录
export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 获取管理员信息
export function getAdminInfo() {
  return request({
    url: '/admin/info',
    method: 'get'
  })
}

// 更新管理员信息
export function updateAdminInfo(data) {
  return request({
    url: '/admin/update',
    method: 'put',
    data
  })
}

// 用户管理
export function getUserList(params) {
  return request({
    url: '/admin/user/list',
    method: 'get',
    params
  })
}
// 兼容旧方法名（若其他地方使用了 getAllUsers）
export const getAllUsers = getUserList

export function updateUserStatus(uid, status) {
  return request({
    url: `/admin/user/status/${uid}`,
    method: 'put',
    params: { status }
  })
}

export function getUserDetail(uid) {
  return request({
    url: `/admin/user/${uid}`,
    method: 'get'
  })
}

// 商家审核（支持分页、搜索、状态筛选）
export function getMerchantList(params) {
  return request({
    url: '/admin/merchant/list',
    method: 'get',
    params
  })
}

// 更新商家状态（启用/禁用）
export function updateMerchantStatus(mid, status) {
  return request({
    url: `/admin/merchant/status/${mid}`,
    method: 'put',
    params: { status }
  })
}

// 审核商家（通过/拒绝）
export function auditMerchant(data) {
  return request({
    url: '/admin/merchant/audit',
    method: 'put',
    data
  })
}

// 商家审核 - 待审核列表
export function getPendingMerchants() {
  return request({
    url: '/admin/merchant/pending',
    method: 'get'
  })
}

// ===================== 帖子审核 =====================
// 获取帖子列表（支持分页、状态筛选、关键字搜索）
export function getPostList(params) {
  return request({
    url: '/admin/post/list',
    method: 'get',
    params
  })
}

// 获取待审核帖子列表（简单版，不分页，如需分页请使用 getPostList 并传 status=1）
export function getPendingPosts() {
  return request({
    url: '/admin/post/pending',
    method: 'get'
  })
}

// 审核帖子（通过 status=0，拒绝 status=2）
export function auditPost(pid, status) {
  return request({
    url: `/admin/post/audit/${pid}`,
    method: 'put',
    params: { status }
  })
}

// 删除帖子（物理删除）
export function deletePost(pid) {
  return request({
    url: `/admin/post/${pid}`,
    method: 'delete'
  })
}

// 获取系统统计数据（用户数、商家数、帖子数、订单数）
export function getSystemStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

// 获取帖子详情
export function getPostDetail(pid) {
  return request({
    url: `/admin/post/${pid}`,
    method: 'get'
  })
}

// 获取帖子评论
export function getCommentsByPost(postId) {
  return request({
    url: `/admin/comment/post/${postId}`,
    method: 'get'
  })
}

// 封禁帖子
export function banPost(pid) {
  return request({
    url: `/admin/post/ban/${pid}`,
    method: 'put'
  })
}

// 解封帖子
export function unbanPost(pid) {
  return request({
    url: `/admin/post/unban/${pid}`,
    method: 'put'
  })
}