const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    invoices: [],
    showForm: false,
    form: { orderId: '', type: 1, title: '', taxNo: '', email: '', amount: '' },
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadInvoices();
  },

  loadInvoices() {
    this.setData({ loading: true });
    api.getInvoices().then(invoices => {
      this.setData({ invoices: invoices || [], loading: false });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  showApplyForm() {
    this.setData({ showForm: true });
  },

  hideForm() {
    this.setData({ showForm: false });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  onTypeChange(e) {
    this.setData({ 'form.type': parseInt(e.detail.value) });
  },

  submit() {
    if (!this.data.form.orderId.trim()) {
      wx.showToast({ title: '请输入订单号', icon: 'none' });
      return;
    }
    if (!this.data.form.title.trim()) {
      wx.showToast({ title: '请输入发票抬头', icon: 'none' });
      return;
    }
    api.applyInvoice(this.data.form).then(() => {
      wx.showToast({ title: '申请成功', icon: 'success' });
      this.setData({ showForm: false, form: { orderId: '', type: 1, title: '', taxNo: '', email: '', amount: '' } });
      this.loadInvoices();
    }).catch(() => {});
  },

  getTypeText(type) {
    return type === 1 ? '电子发票' : '纸质发票';
  },

  getStatusText(status) {
    const map = { 0: '待处理', 1: '已开票', 2: '已邮寄', 3: '已完成' };
    return map[status] || '未知';
  }
});