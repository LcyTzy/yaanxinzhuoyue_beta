import request from '@/utils/request'

export function login(data) {
  return request.post('/user/login', data)
}

export function register(data) {
  return request.post('/user/register', data)
}

export function getCaptcha() {
  return request.get('/user/captcha')
}

export function sendResetCode(data) {
  return request.post('/user/sendResetCode', data)
}

export function resetPassword(data) {
  return request.post('/user/resetPassword', data)
}
