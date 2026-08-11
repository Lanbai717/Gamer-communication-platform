import request from '@/utils/request'

// 获取帖子列表
export function getPostList(params) {
  return request({
    url: '/user/post/list',
    method: 'get',
    params
  })
}

// 获取帖子详情
export function getPostDetail(pid) {
  return request({
    url: `/user/post/${pid}`,
    method: 'get'
  })
}

// 创建帖子
export function createPost(data) {
  return request({
    url: '/user/post/create',
    method: 'post',
    data
  })
}

// 点赞/取消点赞
export function likePost(pid) {
  return request({
    url: `/user/post/like/${pid}`,
    method: 'post'
  })
}

// 我的帖子
export function getMyPosts() {
  return request({
    url: '/user/post/my',
    method: 'get'
  })
}

// 管理员：获取待审核帖子
export function getPendingPosts() {
  return request({
    url: '/admin/post/pending',
    method: 'get'
  })
}

// 管理员：审核帖子
export function auditPost(pid, status) {
  return request({
    url: `/admin/post/audit/${pid}`,
    method: 'put',
    params: { status }
  })
}

// 管理员：删除帖子
export function deletePostByAdmin(pid) {
  return request({
    url: `/admin/post/${pid}`,
    method: 'delete'
  })
}