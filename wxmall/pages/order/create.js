const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    items: [],
    totalPrice: 0,
    address: null,
    remark: ''
  },

  onLoad() {
    const items = wx.getStorageSync('checkoutItems') || [];
    if (items.length === 0) {
      wx.showToast({ title: '请先选择商品', icon: 'none' });
      setTimeout(() => wx.navigateBack(), 1000);
      return;
    }
    const totalPrice = items.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2);
    this.setData({ items, totalPrice });
    this.loadDefaultAddress();
  },

  loadDefaultAddress() {
    api.getAddressList().then(list => {
      const defaultAddr = (list || []).find(a => a.isDefault) || (list || [])[0];
      if (defaultAddr) {
        this.setData({ address: defaultAddr });
      }
    }).catch(() => {});
  },

  chooseAddress() {
    wx.navigateTo({ url: '/pages/address/list?select=1' });
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  submitOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' });
      return;
    }
    const cartIds = this.data.items.map(item => item.cartId).filter(Boolean);
    const orderData = {
      cartIds: cartIds,
      receiverName: this.data.address.receiverName || this.data.address.name,
      receiverPhone: this.data.address.receiverPhone || this.data.address.phone,
      receiverAddress: this.data.address.fullAddress || this.data.address.address,
      remark: this.data.remark
    };

    api.createOrder(orderData).then(orderId => {
      wx.removeStorageSync('checkoutItems');
      api.getOrderDetail(orderId).then(order => {
        wx.redirectTo({ url: '/pages/payment/payment?orderNo=' + order.orderNo + '&amount=' + order.payAmount });
      }).catch(() => {
        wx.redirectTo({ url: '/pages/order/list' });
      });
    });
  },

  onItemImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['items[' + index + '].image']: '/images/placeholder.png' });
  }
});