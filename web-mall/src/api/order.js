import request from '@/utils/request'

export function createOrder(data) {
  return request.post('/order/create', data)
}

export function getOrderList(params) {
  return request.get('/order/page', { params })
}

export function getOrderDetail(id) {
  return request.get(`/order/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/order/${id}/cancel`)
}

export function payOrder(id) {
  return request.post(`/order/pay/${id}`)
}

export function confirmReceive(id) {
  return request.put(`/order/${id}/confirm`)
}
