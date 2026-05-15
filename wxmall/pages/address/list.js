const api = require('../../utils/api');

Page({
  data: {
    addresses: [],
    selectMode: false
  },

  onLoad(options) {
    this.setData({ selectMode: options.select === '1' });
  },

  onShow() {
    this.loadAddresses();
  },

  onPullDownRefresh() {
    this.loadAddresses().then(() => wx.stopPullDownRefresh());
  },

  loadAddresses() {
    return api.getAddressList().then(list => {
      this.setData({ addresses: list || [] });
    }).catch(() => {});
  },

  selectAddress(e) {
    if (!this.data.selectMode) return;
    const address = e.currentTarget.dataset.address;
    const pages = getCurrentPages();
    const prevPage = pages[pages.length - 2];
    if (prevPage) {
      prevPage.setData({ address: address });
    }
    wx.navigateBack();
  },

  goEdit(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/address/edit?id=' + id });
  },

  goAdd() {
    wx.navigateTo({ url: '/pages/address/edit' });
  },

  deleteAddress(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteAddress(id).then(() => {
            wx.showToast({ title: '已删除', icon: 'success' });
            this.loadAddresses();
          });
        }
      }
    });
  }
});