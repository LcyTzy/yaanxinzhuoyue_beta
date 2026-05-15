import request from '@/utils/request'

export function getCartList() {
  return request.get('/cart/list')
}

export function addToCart(data) {
  return request.post('/cart/add', null, { params: { productId: data.productId, quantity: data.quantity || 1 } })
}

export function updateCartQuantity(id, quantity) {
  return request.put('/cart/update', null, { params: { cartId: id, quantity } })
}

export function removeFromCart(id) {
  return request.delete(`/cart/${id}`)
}
