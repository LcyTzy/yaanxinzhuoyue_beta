import request from '@/utils/request'

export function toggleFavorite(productId) {
  return request.post('/favorite/toggle', { productId })
}

export function checkFavorite(productId) {
  return request.get('/favorite/check', { params: { productId } })
}

export function getFavoriteList(params) {
  return request.get('/favorite/page', { params })
}

export function getFavoriteCount(productId) {
  return request.get(`/favorite/count/${productId}`)
}
