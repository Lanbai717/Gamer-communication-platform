import request from '@/utils/request'

// 获取活动列表
export function getActivityList(params) {
  return request({
    url: '/user/activity/list',
    method: 'get',
    params
  })
}

// 获取活动详情
export function getActivityDetail(aid) {
  return request({
    url: `/user/activity/${aid}`,
    method: 'get'
  })
}

// 创建活动
export function createActivity(data) {
  return request({
    url: '/user/activity/create',
    method: 'post',
    data
  })
}

// 报名活动
export function joinActivity(aid) {
  return request({
    url: `/user/activity/join/${aid}`,
    method: 'post'
  })
}

// 取消报名
export function cancelActivity(aid) {
  return request({
    url: `/user/activity/cancel/${aid}`,
    method: 'delete'
  })
}

// 我创建的活动
export function getMyCreatedActivities() {
  return request({
    url: '/user/activity/my/created',
    method: 'get'
  })
}

// 我参与的活动
export function getMyJoinedActivities() {
  return request({
    url: '/user/activity/my/joined',
    method: 'get'
  })
}