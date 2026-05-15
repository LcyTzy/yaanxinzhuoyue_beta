import request from '@/utils/request'

export function getCategoryTree() {
  return request.get('/product/category/tree')
}

export function getProductPage(params) {
  return request.get('/product/page', { params })
}

export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}
