import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/admin/product/page', { params })
}

export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}

export function addProduct(data) {
  return request.post('/admin/product', data)
}

export function updateProduct(data) {
  return request.put('/admin/product', data)
}

export function deleteProduct(id) {
  return request.delete(`/admin/product/${id}`)
}

export function updateProductStatus(id, status) {
  return request.put(`/admin/product/status/${id}`, null, { params: { status } })
}
