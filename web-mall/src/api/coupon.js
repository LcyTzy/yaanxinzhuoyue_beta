import request from '@/utils/request'

export function getAvailableCoupons() {
  return request.get('/coupon/list')
}

export function receiveCoupon(couponId) {
  return request.post('/coupon/receive', { couponId })
}

export function getMyCoupons(params) {
  return request.get('/coupon/my', { params })
}

export function getUserCoupons(params) {
  return request.get('/coupon/my', { params })
}
