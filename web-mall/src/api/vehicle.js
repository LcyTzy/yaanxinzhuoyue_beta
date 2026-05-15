import request from '@/utils/request'

export function getBrands() {
  return request.get('/vehicle/brands')
}

export function getSeries(brandId) {
  return request.get(`/vehicle/series/${brandId}`)
}

export function getModels(seriesId) {
  return request.get(`/vehicle/models/${seriesId}`)
}
