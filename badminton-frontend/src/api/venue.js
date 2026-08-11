import request from '@/utils/request'

// 用户端获取场地列表（支持分页、搜索、排序）
export function getVenueList(params) {
  return request({
    url: '/user/venue/list',
    method: 'get',
    params
  })
}

// 获取场地详情
export function getVenueDetail(vid) {
  return request({
    url: `/user/venue/${vid}`,
    method: 'get'
  })
}

// 商家端获取自己的场地
export function getMyVenues() {
  return request({
    url: '/merchant/venue/my',
    method: 'get'
  })
}

// 添加场地
export function addVenue(data) {
  return request({
    url: '/merchant/venue/add',
    method: 'post',
    data
  })
}

// 更新场地
export function updateVenue(vid, data) {
  return request({
    url: `/merchant/venue/update/${vid}`,
    method: 'put',
    data
  })
}

// 删除场地
export function deleteVenue(vid) {
  return request({
    url: `/merchant/venue/${vid}`,
    method: 'delete'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/user/order/create',
    method: 'post',
    data
  })
}