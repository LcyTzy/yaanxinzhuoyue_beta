import request from '@/utils/request'

export function getAddressList() {
  return request.get('/address/list')
}

export function addAddress(data) {
  return request.post('/address/add', data)
}

export function updateAddress(data) {
  return request.put('/address/update', data)
}

export function deleteAddress(id) {
  return request.delete(`/address/${id}`)
}

export function setDefault(id) {
  return request.put(`/address/default/${id}`)
}
