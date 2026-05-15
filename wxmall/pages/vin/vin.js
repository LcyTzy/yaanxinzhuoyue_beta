const api = require('../../utils/api');

Page({
  data: {
    vin: '',
    vehicle: null,
    parts: [],
    loading: false,
    searched: false
  },

  onVinInput(e) {
    this.setData({ vin: e.detail.value.toUpperCase() });
  },

  onPullDownRefresh() {
    wx.stopPullDownRefresh();
  },

  doQuery() {
    const vin = this.data.vin.trim();
    if (!vin) {
      wx.showToast({ title: '请输入VIN码', icon: 'none' });
      return;
    }
    if (vin.length !== 17) {
      wx.showToast({ title: 'VIN码应为17位', icon: 'none' });
      return;
    }

    this.setData({ loading: true, searched: false });

    api.queryVin(vin).then(res => {
      this.setData({
        vehicle: res.vehicle || null,
        parts: res.parts || [],
        loading: false,
        searched: true
      });
    }).catch(() => {
      this.setData({ loading: false, searched: true });
    });
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/product/detail?id=' + id });
  }
});