const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    points: 0,
    signedToday: false,
    exchangeProducts: [],
    records: [],
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  loadData() {
    this.setData({ loading: true });
    return Promise.all([
      api.getSignStatus().then(res => {
        this.setData({
          points: res.totalPoints || 0,
          signedToday: res.signedToday || false
        });
      }).catch(() => {}),
      api.getPointsProducts().then(list => {
        this.setData({ exchangeProducts: list || [] });
      }).catch(() => {}),
      api.getPointsRecords({ pageNum: 1, pageSize: 20 }).then(res => {
        this.setData({ records: res.records || [] });
      }).catch(() => {})
    ]).then(() => {
      this.setData({ loading: false });
    });
  },

  doSignIn() {
    api.signIn().then(res => {
      const points = res.points || 0;
      wx.showToast({ title: '签到成功 +' + points + '积分', icon: 'success' });
      this.setData({ signedToday: true, points: this.data.points + points });
      this.loadData();
    });
  },

  doExchange(e) {
    const id = e.currentTarget.dataset.id;
    const points = e.currentTarget.dataset.points;
    wx.showModal({
      title: '积分兑换',
      content: '确定使用' + points + '积分兑换该商品吗？',
      success: (res) => {
        if (res.confirm) {
          api.exchangePoints(id).then(() => {
            wx.showToast({ title: '兑换成功', icon: 'success' });
            this.loadData();
          });
        }
      }
    });
  },

  onExchangeImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['exchangeProducts[' + index + '].image']: '/images/placeholder.png' });
  }
});