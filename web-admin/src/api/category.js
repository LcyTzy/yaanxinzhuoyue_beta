import request from '@/utils/request'

export function getCategoryList() {
  return request.get('/admin/category/tree')
}

export function getCategoryTree() {
  return request.get('/admin/category/tree')
}

export function addCategory(data) {
  return request.post('/admin/category', data)
}

export function updateCategory(data) {
  return request.put('/admin/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/admin/category/${id}`)
}
