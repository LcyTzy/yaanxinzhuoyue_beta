const app = getApp();

const buildUrl = (url, params) => {
  if (!params || Object.keys(params).length === 0) return url;
  const query = Object.keys(params)
    .filter(k => params[k] !== undefined && params[k] !== null)
    .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
    .join('&');
  return url + (url.includes('?') ? '&' : '?') + query;
};

const request = (url, method = 'GET', data = {}, needAuth = true, queryParams = null) => {
  return new Promise((resolve, reject) => {
    const header = {
      'Content-Type': 'application/json'
    };
    if (needAuth && app.globalData.token) {
      header['Authorization'] = 'Bearer ' + app.globalData.token;
    }
    const finalUrl = app.globalData.baseUrl + buildUrl(url, queryParams);
    wx.request({
      url: finalUrl,
      method: method,
      data: method === 'GET' ? undefined : data,
      header: header,
      success(res) {
        if (res.statusCode === 200) {
          const result = res.data;
          if (result.code === 200) {
            resolve(result.data);
          } else {
            wx.showToast({ title: result.message || '请求失败', icon: 'none' });
            reject(result);
          }
        } else if (res.statusCode === 401 || res.statusCode === 403) {
          app.logout();
          wx.navigateTo({ url: '/pages/login/login' });
          reject({ code: 401, message: '请先登录' });
        } else {
          wx.showToast({ title: '网络错误(' + res.statusCode + ')', icon: 'none' });
          reject(res);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络连接失败', icon: 'none' });
        reject(err);
      }
    });
  });
};

const get = (url, params, needAuth) => request(url, 'GET', null, needAuth !== false, params);
const post = (url, data, needAuth, queryParams) => request(url, 'POST', data, needAuth !== false, queryParams);
const put = (url, data, needAuth, queryParams) => request(url, 'PUT', data, needAuth !== false, queryParams);
const del = (url, params, needAuth) => request(url, 'DELETE', null, needAuth !== false, params);

module.exports = {
  get,
  post,
  put,
  del,

  login: (data) => post('/api/user/login', data, false),
  adminLogin: (data) => post('/api/admin/login', data, false),
  register: (data) => post('/api/user/register', data, false),
  getCaptcha: () => get('/api/user/captcha', {}, false),
  sendResetCode: (data) => post('/api/user/sendResetCode', data, false),
  resetPassword: (data) => post('/api/user/resetPassword', data, false),

  getCategoryTree: () => get('/api/product/category/tree', {}, false),
  getProducts: (params) => get('/api/product/page', params, false),
  getProductDetail: (id) => get('/api/product/' + id, {}, false),

  getCartList: () => get('/api/cart/list'),
  addToCart: (productId, quantity) => post('/api/cart/add', {}, true, { productId, quantity }),
  updateCart: (cartId, quantity) => put('/api/cart/update', {}, true, { cartId, quantity }),
  deleteCart: (cartId) => del('/api/cart/' + cartId),
  clearCart: () => del('/api/cart/clear'),

  createOrder: (data) => post('/api/order/create', data),
  getOrders: (params) => get('/api/order/page', params),
  getOrderDetail: (orderId) => get('/api/order/' + orderId),
  payOrder: (orderId) => post('/api/order/pay/' + orderId),
  confirmReceive: (orderId) => put('/api/order/' + orderId + '/confirm'),

  createPayment: (data) => post('/api/payment/create', data),
  queryPayment: (orderNo, type) => get('/api/payment/query', { orderNo, type }),

  getAddressList: () => get('/api/address/list'),
  addAddress: (data) => post('/api/address/add', data),
  updateAddress: (id, data) => put('/api/address/update', { ...data, id: Number(id) }),
  deleteAddress: (id) => del('/api/address/' + id),

  getUserInfo: () => get('/api/user/info'),
  updateUserInfo: (data) => put('/api/user/info', data),

  getCoupons: () => get('/api/coupon/list'),
  getUserCoupons: (params) => get('/api/coupon/my', params),
  receiveCoupon: (couponId) => post('/api/coupon/receive', { couponId }),

  getSignStatus: () => get('/api/points-mall/sign-status'),
  signIn: () => post('/api/points-mall/sign-in'),
  getPointsRecords: (params) => get('/api/points/log', params),
  getPointsProducts: () => get('/api/points-mall/products'),
  exchangePoints: (productId) => post('/api/points-mall/exchange/' + productId),

  addReview: (data) => post('/api/review/add', data),
  getReviews: (productId) => get('/api/review/page', { productId }, false),

  getVehicles: () => get('/api/vehicle/list'),
  searchVehicles: (keyword) => get('/api/vehicle/search', { keyword }),
  queryVin: (vin) => get('/api/vehicle/vin/' + vin),

  getDashboardStats: () => get('/api/admin/dashboard/stats'),
  getDashboardRecentOrders: () => get('/api/admin/dashboard/recent-orders'),
  getBanners: () => get('/api/banner/list', {}, false),

  stockIn: (productId, quantity) => post('/api/admin/product/stock-in/' + productId, {}, true, { quantity }),
  stockOut: (productId, quantity) => post('/api/admin/product/stock-out/' + productId, {}, true, { quantity }),

  getPurchaseOrders: (params) => get('/api/admin/purchase-order/page', params),
  getPurchaseOrderDetail: (id) => get('/api/admin/purchase-order/' + id),
  confirmPurchaseOrder: (id) => put('/api/admin/purchase-order/' + id + '/confirm'),

  toggleFavorite: (productId) => post('/api/favorite/toggle', { productId }),
  checkFavorite: (productId) => get('/api/favorite/check', { productId }),
  getFavoriteList: (params) => get('/api/favorite/page', params),

  getNotifications: (params) => get('/api/notification/page', params),
  getUnreadCount: () => get('/api/notification/unread-count'),
  markRead: (id) => put('/api/notification/' + id + '/read'),
  markAllRead: () => put('/api/notification/read-all'),

  getMembershipLevels: () => get('/api/membership/levels', {}, false),
  getMyMembership: () => get('/api/membership/my-level'),

  getPointsMallProducts: () => get('/api/points-mall/products', {}, false),
  getPointsMallSignStatus: () => get('/api/points-mall/sign-status'),
  pointsMallSignIn: () => post('/api/points-mall/sign-in'),
  pointsMallExchange: (productId) => post('/api/points-mall/exchange/' + productId),

  getInvoices: () => get('/api/invoice/list'),
  applyInvoice: (data) => post('/api/invoice/apply', data),

  getServiceTypes: () => get('/api/service-order/types', {}, false),
  getServiceOrders: (params) => get('/api/service-order/my', params),
  createServiceOrder: (data) => post('/api/service-order', data),

  getMaintenanceRecords: () => get('/api/maintenance/records'),
  addMaintenanceRecord: (data) => post('/api/maintenance/record', data),

  getInquiries: () => get('/api/inquiry/list'),
  submitInquiry: (data) => post('/api/inquiry/submit', data),

  getInsuranceServices: () => get('/api/insurance/list'),
  applyInsurance: (data) => post('/api/insurance/apply', data),

  getStores: () => get('/api/stores', {}, false),
};