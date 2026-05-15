const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    isLogin: false,
    isAdmin: false,
    userInfo: {},
    orderCounts: {},
    couponCount: 0,
    points: 0,
    signedToday: false
  },

  onShow() {
    const isLogin = app.globalData.isLogin;
    this.setData({
      isLogin,
      isAdmin: app.isAdmin(),
      userInfo: app.globalData.userInfo || {}
    });
    if (isLogin) {
      this.loadOrderCounts();
      this.loadCouponCount();
      this.loadPoints();
    }
  },

  onPullDownRefresh() {
    const isLogin = app.globalData.isLogin;
    this.setData({
      isLogin,
      isAdmin: app.isAdmin(),
      userInfo: app.globalData.userInfo || {}
    });
    if (isLogin) {
      Promise.all([
        this.loadOrderCounts(),
        this.loadCouponCount(),
        this.loadPoints()
      ]).then(() => wx.stopPullDownRefresh());
    } else {
      wx.stopPullDownRefresh();
    }
  },

  loadOrderCounts() {
    api.getOrders({ pageNum: 1, pageSize: 1, status: 0 }).then(res => {
      this.setData({ 'orderCounts.pendingPayment': res.total || 0 });
    }).catch(() => {});
    api.getOrders({ pageNum: 1, pageSize: 1, status: 1 }).then(res => {
      this.setData({ 'orderCounts.pendingShipping': res.total || 0 });
    }).catch(() => {});
    api.getOrders({ pageNum: 1, pageSize: 1, status: 2 }).then(res => {
      this.setData({ 'orderCounts.pendingReceiving': res.total || 0 });
    }).catch(() => {});
  },

  loadCouponCount() {
    api.getUserCoupons().then(res => {
      this.setData({ couponCount: res.total || 0 });
    }).catch(() => {});
  },

  loadPoints() {
    api.getSignStatus().then(res => {
      this.setData({
        points: res.totalPoints || 0,
        signedToday: res.signedToday || false
      });
    }).catch(() => {});
  },

  goLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goOrders(e) {
    if (!app.checkLogin()) return;
    const tab = e.currentTarget.dataset.tab || 0;
    wx.navigateTo({ url: '/pages/order/list?tab=' + tab });
  },

  goAddress() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/address/list' });
  },

  goCoupons() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/coupon/list' });
  },

  goPoints() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/points/list' });
  },

  goEditProfile() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/profile/edit' });
  },

  goFavorites() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/favorite/list' });
  },

  goNotifications() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/notification/list' });
  },

  goMembership() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/membership/index' });
  },

  goPointsMall() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/points/mall' });
  },

  goInvoice() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/invoice/list' });
  },

  goService() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/service/appointment' });
  },

  goMaintenance() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/maintenance/list' });
  },

  goInquiry() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/inquiry/list' });
  },

  goInsurance() {
    if (!app.checkLogin()) return;
    wx.navigateTo({ url: '/pages/insurance/list' });
  },

  goCompare() {
    wx.navigateTo({ url: '/pages/compare/index' });
  },

  goParts() {
    wx.navigateTo({ url: '/pages/parts/list' });
  },

  goAbout() {
    wx.showModal({
      title: '关于我们',
      content: '新卓阅汽配 - 专业汽车配件商城\n版本: 1.0.0\n为您提供优质的汽配零件选购服务',
      showCancel: false
    });
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

  goToProducts() {
    wx.navigateTo({ url: '/pages/product/list' });
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout();
          this.setData({ isLogin: false, userInfo: {} });
        }
      }
    });
  },

  onAvatarError() {
    this.setData({ 'userInfo.avatar': '/images/default-avatar.png' });
  }
});