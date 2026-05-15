const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    products: [],
    signedToday: false,
    totalPoints: 0,
    loading: true
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });
    Promise.all([
      api.getPointsMallProducts().catch(() => []),
      api.getPointsMallSignStatus().catch(() => ({ signedToday: false, totalPoints: 0 }))
    ]).then(([products, signData]) => {
      this.setData({
        products: products || [],
        signedToday: signData.signedToday || false,
        totalPoints: signData.totalPoints || 0,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  signIn() {
    if (this.data.signedToday) {
      wx.showToast({ title: '今日已签到', icon: 'none' });
      return;
    }
    api.pointsMallSignIn().then(res => {
      wx.showToast({ title: '签到成功！+' + (res.points || 0) + '积分', icon: 'success' });
      this.setData({ signedToday: true, totalPoints: res.totalPoints || this.data.totalPoints });
    }).catch(() => {});
  },

  exchange(e) {
    const item = e.currentTarget.dataset.item;
    if (this.data.totalPoints < item.points) {
      wx.showToast({ title: '积分不足', icon: 'none' });
      return;
    }
    wx.showModal({
      title: '确认兑换',
      content: '消耗 ' + item.points + ' 积分兑换「' + item.name + '」？',
      success: (res) => {
        if (res.confirm) {
          api.pointsMallExchange(item.id).then(() => {
            wx.showToast({ title: '兑换成功！', icon: 'success' });
            this.loadData();
          }).catch(() => {});
        }
      }
    });
  },

  goPointsRecord() {
    wx.navigateTo({ url: '/pages/points/list' });
  }
});