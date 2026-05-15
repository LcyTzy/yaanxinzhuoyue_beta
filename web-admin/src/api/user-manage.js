import request from '@/utils/request'

export function getUserList(params) {
  return request.get('/admin/user/page', { params })
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/user/status/${id}`, { status })
}

export function resetPassword(id) {
  return request.put(`/admin/user/reset-password/${id}`)
}
