const api = require('../../utils/api');

Page({
  data: {
    keyword: '',
    list: [],
    loading: false
  },

  onShow() {
    this.loadData();
  },

  onPullDownRefresh() {
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  doSearch() {
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });
    const params = { pageNum: 1, pageSize: 50 };
    if (this.data.keyword) params.keyword = this.data.keyword;

    return api.getProducts(params).then(res => {
      this.setData({ list: res.records || [], loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  stockIn(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '入库操作',
      editable: true,
      placeholderText: '请输入入库数量',
      success: (res) => {
        if (res.confirm && res.content) {
          const qty = parseInt(res.content);
          if (isNaN(qty) || qty <= 0) {
            wx.showToast({ title: '请输入有效数量', icon: 'none' });
            return;
          }
          api.stockIn(id, qty).then(() => {
            wx.showToast({ title: '入库成功', icon: 'success' });
            this.loadData();
          });
        }
      }
    });
  },

  stockOut(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '出库操作',
      editable: true,
      placeholderText: '请输入出库数量',
      success: (res) => {
        if (res.confirm && res.content) {
          const qty = parseInt(res.content);
          if (isNaN(qty) || qty <= 0) {
            wx.showToast({ title: '请输入有效数量', icon: 'none' });
            return;
          }
          api.stockOut(id, qty).then(() => {
            wx.showToast({ title: '出库成功', icon: 'success' });
            this.loadData();
          });
        }
      }
    });
  }
});