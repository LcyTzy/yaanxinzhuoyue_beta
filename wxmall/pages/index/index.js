const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    isAdmin: false,
    stats: null,
    banners: [],
    recentOrders: [],
    loading: false
  },

  onShow() {
    this.setData({ isAdmin: app.isAdmin() });
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });

    if (app.isAdmin()) {
      Promise.all([
        api.getDashboardStats().catch(() => null),
        api.getDashboardRecentOrders().catch(() => [])
      ]).then(([stats, orders]) => {
        this.setData({
          stats: stats,
          recentOrders: orders || [],
          loading: false
        });
      }).catch(() => {
        this.setData({ loading: false });
      });
    } else {
      api.getBanners().then(banners => {
        this.setData({ banners: banners || [], loading: false });
      }).catch(() => {
        this.setData({ banners: [], loading: false });
      });
    }
  },

  goToSearch(e) {
    const keyword = e && e.currentTarget && e.currentTarget.dataset.keyword;
    if (keyword) {
      wx.navigateTo({ url: '/pages/search/search?keyword=' + keyword });
    } else {
      wx.navigateTo({ url: '/pages/search/search' });
    }
  },

  goToProducts() {
    wx.navigateTo({ url: '/pages/product/list' });
  },

  goToOrders() {
    wx.switchTab({ url: '/pages/order/list' });
  },

  goToVin() {
    wx.navigateTo({ url: '/pages/vin/vin' });
  },

  goToCart() {
    wx.switchTab({ url: '/pages/cart/cart' });
  },

  goToInventory() {
    wx.navigateTo({ url: '/pages/inventory/list' });
  },

  goToPurchaseOrders() {
    wx.navigateTo({ url: '/pages/purchase/list' });
  },

  goToSalesOrders() {
    wx.navigateTo({ url: '/pages/order/list' });
  },

  goToOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/order/detail?id=' + id });
  },

  onBannerError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['banners[' + index + '].imageUrl']: '/images/placeholder.png' });
  }
});