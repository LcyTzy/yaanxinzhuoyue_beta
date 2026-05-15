const api = require('../../utils/api');
const app = getApp();

const STATUS_MAP = {
  0: '待付款',
  1: '已付款',
  2: '已发货',
  3: '已完成',
  4: '已取消'
};

Page({
  data: {
    orders: [],
    activeTab: 0,
    tabs: ['全部', '待付款', '待发货', '待收货', '已完成', '已取消'],
    pageNum: 1,
    hasMore: true
  },

  onLoad(options) {
    if (options.tab) {
      this.setData({ activeTab: parseInt(options.tab) || 0 });
    }
  },

  onShow() {
    if (!app.globalData.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    this.setData({ orders: [], pageNum: 1, hasMore: true });
    this.loadOrders();
  },

  onPullDownRefresh() {
    this.setData({ orders: [], pageNum: 1, hasMore: true });
    this.loadOrders().then(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (this.data.hasMore) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.loadOrders();
    }
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ activeTab: index, orders: [], pageNum: 1, hasMore: true });
    this.loadOrders();
  },

  loadOrders() {
    const statusMap = { 0: null, 1: 0, 2: 1, 3: 2, 4: 3, 5: 4 };
    const params = { pageNum: this.data.pageNum, pageSize: 10 };
    const status = statusMap[this.data.activeTab];
    if (status !== null && status !== undefined) params.status = status;

    return api.getOrders(params).then(res => {
      const records = (res.records || []).map(order => ({
        ...order,
        statusText: STATUS_MAP[order.status] || '未知'
      }));
      this.setData({
        orders: this.data.pageNum === 1 ? records : [...this.data.orders, ...records],
        hasMore: (res.records || []).length >= 10
      });
    }).catch(() => {});
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/order/detail?id=' + id });
  },

  payOrder(e) {
    const order = e.currentTarget.dataset.order;
    wx.navigateTo({ url: '/pages/payment/payment?orderNo=' + order.orderNo + '&amount=' + order.payAmount });
  },

  confirmReceive(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认收货',
      content: '确定已收到商品吗？',
      success: (res) => {
        if (res.confirm) {
          api.confirmReceive(id).then(() => {
            wx.showToast({ title: '已确认收货', icon: 'success' });
            this.setData({ orders: [], pageNum: 1, hasMore: true });
            this.loadOrders();
          });
        }
      }
    });
  },

  onItemImageError(e) {
    const index = e.currentTarget.dataset.index;
    const items = this.data.orders[index].items;
    if (items && items.length > 0) {
      items[0].productImage = '/images/placeholder.png';
      this.setData({ ['orders[' + index + '].items']: items });
    }
  }
});