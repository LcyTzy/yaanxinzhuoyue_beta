import request from '@/utils/request'

export function login(data) {
  return request.post('/admin/login', data)
}

export function getAdminInfo() {
  return request.get('/admin/info')
}

export function getCaptcha() {
  return request.get('/user/captcha')
}

export function register(data) {
  return request.post('/admin/register', data)
}
