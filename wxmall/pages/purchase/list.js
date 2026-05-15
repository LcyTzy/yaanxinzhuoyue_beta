const api = require('../../utils/api');

const STATUS_MAP = {
  'pending': '待审核',
  'approved': '已通过',
  'completed': '已完成',
  'cancelled': '已取消'
};

Page({
  data: {
    activeTab: 'all',
    list: [],
    pageNum: 1,
    hasMore: true,
    loading: false
  },

  onShow() {
    this.setData({ list: [], pageNum: 1, hasMore: true });
    this.loadData();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.loadData();
    }
  },

  onPullDownRefresh() {
    this.setData({ list: [], pageNum: 1, hasMore: true });
    this.loadData().then(() => wx.stopPullDownRefresh());
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab, list: [], pageNum: 1, hasMore: true });
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });
    const params = { pageNum: this.data.pageNum, pageSize: 10 };
    if (this.data.activeTab !== 'all') {
      params.status = this.data.activeTab;
    }

    return api.getPurchaseOrders(params).then(res => {
      const records = (res.records || []).map(order => ({
        ...order,
        statusText: STATUS_MAP[order.status] || order.status || '未知'
      }));
      this.setData({
        list: this.data.pageNum === 1 ? records : [...this.data.list, ...records],
        hasMore: (res.records || []).length >= 10,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  confirmOrder(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认入库',
      content: '确定将该采购单确认入库吗？',
      success: (res) => {
        if (res.confirm) {
          api.confirmPurchaseOrder(id).then(() => {
            wx.showToast({ title: '入库成功', icon: 'success' });
            this.setData({ list: [], pageNum: 1, hasMore: true });
            this.loadData();
          });
        }
      }
    });
  },

  createOrder() {
    wx.showToast({ title: '请在电脑端创建采购订单', icon: 'none' });
  }
});