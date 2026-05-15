const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    activeTab: 'available',
    availableList: [],
    myList: [],
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });
    const promises = [];

    promises.push(
      api.getCoupons().then(list => {
        this.setData({ availableList: list || [] });
      }).catch(() => {})
    );

    promises.push(
      api.getUserCoupons({ pageNum: 1, pageSize: 50 }).then(res => {
        const records = (res.records || []).map(uc => ({
          ...uc,
          name: uc.couponName || '优惠券',
          type: uc.couponType || 1,
          discountAmount: uc.couponDiscountAmount || 0,
          discountRate: uc.couponDiscountRate || 1,
          minAmount: uc.couponMinAmount || 0,
          startTime: uc.couponStartTime || '',
          endTime: uc.couponEndTime || ''
        }));
        this.setData({ myList: records });
      }).catch(() => {})
    );

    return Promise.all(promises).then(() => {
      this.setData({ loading: false });
    });
  },

  receiveCoupon(e) {
    const id = e.currentTarget.dataset.id;
    api.receiveCoupon(id).then(() => {
      wx.showToast({ title: '领取成功', icon: 'success' });
      this.loadData();
    });
  }
});