import request from '@/utils/request'

// ==================== 用户端 ====================

// 用户端：创建订单
export function createOrder(data) {
  return request({
    url: '/user/order/create',
    method: 'post',
    data
  })
}

// 用户端：获取可用时间段
export function getAvailableSlots(params) {
  return request({
    url: '/user/order/available-slots',
    method: 'get',
    params
  })
}

// 用户端：获取我的订单
export function getMyOrders() {
  return request({
    url: '/user/order/my',
    method: 'get'
  })
}

// 用户端：取消订单（待支付状态下可用）
export function cancelOrder(oid) {
  return request({
    url: `/user/order/cancel/${oid}`,
    method: 'put'
  })
}

// 用户端：订单详情
export function getOrderDetail(oid) {
  return request({
    url: `/user/order/${oid}`,
    method: 'get'
  })
}

// 用户端：获取我的订单（支持分页和状态筛选）
export function getUserOrders(params) {
  return request({
    url: '/user/order/my',
    method: 'get',
    params
  })
}

// 用户端：模拟支付订单 (新增)
export function payOrder(oid) {
  return request({
    url: `/user/order/pay/${oid}`,
    method: 'put'
  })
}



// ==================== 商家端 ====================

// 商家端：获取订单列表
export function getMerchantOrders(params) {
  return request({
    url: '/merchant/order/list',
    method: 'get',
    params
  })
}

// 商家端：确认订单（从待支付 → 已支付）
export function confirmOrder(oid) {
  return request({
    url: `/merchant/order/confirm/${oid}`,
    method: 'put'
  })
}

// 商家端：拒绝订单（从待支付 → 已取消）
export function rejectOrder(oid) {
  return request({
    url: `/merchant/order/reject/${oid}`,
    method: 'put'
  })
}

// 商家端：完成订单（从已支付 → 已完成） (新增)
export function completeOrder(oid) {
  return request({
    url: `/merchant/order/complete/${oid}`,
    method: 'put'
  })
}