import request from '@/utils/request'

export function addReview(data) {
  return request.post('/review/add', data)
}

export function getReviewList(params) {
  return request.get('/review/page', { params })
}

export function getAverageRating(productId) {
  return request.get(`/review/rating/${productId}`)
}
