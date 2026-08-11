import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/merchant/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/merchant/register',
    method: 'post',
    data
  })
}

export function getMerchantInfo() {
  return request({
    url: '/merchant/info',
    method: 'get'
  })
}

export function updateMerchantInfo(data) {
  return request({
    url: '/merchant/update',
    method: 'put',
    data
  })
}

export function getMerchantList() {
  return request({
    url: '/merchant/user/list',
    method: 'get'
  })
}

export function getMerchantDetail(mid) {
  return request({
    url: `/merchant/user/detail/${mid}`,
    method: 'get'
  })
}

// 提交审核
export function submitAudit() {
  return request({
    url: '/merchant/submitAudit',
    method: 'post'
  })
}
