const api = require('../../utils/api');

const STATUS_MAP = {
  0: '待付款',
  1: '已付款',
  2: '已发货',
  3: '已完成',
  4: '已取消'
};

Page({
  data: {
    order: {},
    items: []
  },

  onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id);
    }
  },

  loadDetail(id) {
    api.getOrderDetail(id).then(order => {
      this.setData({
        order: { ...order, statusText: STATUS_MAP[order.status] || '未知' },
        items: order.items || []
      });
    }).catch(() => {});
  },

  payOrder() {
    const order = this.data.order;
    wx.navigateTo({ url: '/pages/payment/payment?orderNo=' + order.orderNo + '&amount=' + order.payAmount });
  },

  confirmReceive() {
    wx.showModal({
      title: '确认收货',
      content: '确定已收到商品吗？',
      success: (res) => {
        if (res.confirm) {
          api.confirmReceive(this.data.order.id).then(() => {
            wx.showToast({ title: '已确认收货', icon: 'success' });
            this.loadDetail(this.data.order.id);
          });
        }
      }
    });
  },

  onItemImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['items[' + index + '].productImage']: '/images/placeholder.png' });
  }
});