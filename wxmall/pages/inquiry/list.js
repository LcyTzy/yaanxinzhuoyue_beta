const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    inquiries: [],
    showForm: false,
    form: { productName: '', brand: '', model: '', partNo: '', quantity: 1, contactPhone: '', remark: '' },
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadInquiries();
  },

  loadInquiries() {
    this.setData({ loading: true });
    api.getInquiries().then(inquiries => {
      this.setData({ inquiries: inquiries || [], loading: false });
    }).catch(() => this.setData({ loading: false }));
  },

  showForm() { this.setData({ showForm: true }); },
  hideForm() { this.setData({ showForm: false }); },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  submit() {
    if (!this.data.form.productName.trim()) { wx.showToast({ title: '请输入配件名称', icon: 'none' }); return; }
    if (!this.data.form.contactPhone.trim()) { wx.showToast({ title: '请输入联系电话', icon: 'none' }); return; }
    api.submitInquiry(this.data.form).then(() => {
      wx.showToast({ title: '询价已提交', icon: 'success' });
      this.setData({ showForm: false, form: { productName: '', brand: '', model: '', partNo: '', quantity: 1, contactPhone: '', remark: '' } });
      this.loadInquiries();
    }).catch(() => {});
  },

  getStatusText(status) {
    const map = { 0: '待回复', 1: '已回复', 2: '已关闭' };
    return map[status] || '未知';
  }
});