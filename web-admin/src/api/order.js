import request from '@/utils/request'

export function getOrderList(params) {
  return request.get('/admin/order/page', { params })
}

export function getOrderDetail(id) {
  return request.get(`/admin/order/${id}`)
}

export function updateOrderStatus(id, status, remark) {
  return request.put(`/admin/order/${id}/status`, { status, remark })
}

export function shipOrder(id, logisticsCompany, logisticsNo) {
  return request.put(`/admin/order/${id}/ship`, null, {
    params: { logisticsCompany, logisticsNo }
  })
}

export function cancelOrder(id) {
  return request.put(`/admin/order/${id}/cancel`)
}

export function getLogistics(id) {
  return request.get(`/admin/order/${id}/logistics`)
}
