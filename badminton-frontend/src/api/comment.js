import request from '@/utils/request'

// 发表评论
export function createComment(data) {
  return request({
    url: '/user/comment/create',
    method: 'post',
    data
  })
}

// 获取帖子评论列表
export function getCommentsByPost(postId) {
  return request({
    url: `/user/comment/post/${postId}`,
    method: 'get'
  })
}

// 删除评论
export function deleteComment(cid) {
  return request({
    url: `/user/comment/${cid}`,
    method: 'delete'
  })
}