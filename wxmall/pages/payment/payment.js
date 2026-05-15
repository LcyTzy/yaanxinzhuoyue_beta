const api = require('../../utils/api');

Page({
  data: {
    orderNo: '',
    amount: '0.00',
    paymentType: 'wechat',
    qrCode: '',
    status: 'pending',
    creating: false,
    errorMsg: '',
    timer: null
  },

  onLoad(options) {
    this.setData({
      orderNo: options.orderNo || '',
      amount: options.amount || '0.00'
    });
  },

  onUnload() {
    if (this.data.timer) clearInterval(this.data.timer);
  },

  createPayment() {
    this.setData({ creating: true });
    api.createPayment({
      orderNo: this.data.orderNo,
      paymentType: this.data.paymentType
    }).then(res => {
      this.setData({ creating: false });
      if (res.codeUrl) {
        this.setData({ qrCode: res.codeUrl, status: 'waiting' });
        this.startPolling();
      } else {
        wx.showToast({ title: res.message || '创建支付失败', icon: 'none' });
      }
    }).catch(() => {
      this.setData({ creating: false });
    });
  },

  startPolling() {
    this.data.timer = setInterval(() => {
      api.queryPayment(this.data.orderNo, this.data.paymentType).then(res => {
        if (res.status === 'SUCCESS' || res.status === 'PAID') {
          clearInterval(this.data.timer);
          this.setData({ status: 'success' });
        }
      }).catch(() => {});
    }, 3000);
  },

  checkPayStatus() {
    wx.showLoading({ title: '查询中...' });
    api.queryPayment(this.data.orderNo, this.data.paymentType).then(res => {
      wx.hideLoading();
      if (res.status === 'SUCCESS' || res.status === 'PAID') {
        clearInterval(this.data.timer);
        this.setData({ status: 'success' });
      } else {
        wx.showToast({ title: '暂未收到支付结果，请稍候', icon: 'none' });
      }
    }).catch(() => {
      wx.hideLoading();
    });
  },

  cancelPay() {
    if (this.data.timer) clearInterval(this.data.timer);
    wx.showModal({
      title: '提示',
      content: '确定要取消支付吗？',
      success: (res) => {
        if (res.confirm) {
          wx.navigateBack();
        } else {
          this.startPolling();
        }
      }
    });
  },

  retryPay() {
    this.setData({ status: 'pending', errorMsg: '', qrCode: '' });
  },

  goOrders() {
    wx.redirectTo({ url: '/pages/order/list' });
  }
});