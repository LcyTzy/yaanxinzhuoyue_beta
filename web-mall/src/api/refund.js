import request from '@/utils/request'

export function applyRefund(data) {
  return request.post('/refund/apply', data)
}

export function getRefundList(params) {
  return request.get('/refund/page', { params })
}

export function getRefundDetail(id) {
  return request.get(`/refund/${id}`)
}