const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    services: [],
    showForm: false,
    form: { serviceType: 'INSURANCE', vehicleInfo: '', contactName: '', contactPhone: '', remark: '' },
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadServices();
  },

  loadServices() {
    this.setData({ loading: true });
    api.getInsuranceServices().then(services => {
      this.setData({ services: services || [], loading: false });
    }).catch(() => this.setData({ loading: false }));
  },

  showForm() { this.setData({ showForm: true }); },
  hideForm() { this.setData({ showForm: false }); },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  onTypeChange(e) {
    const types = ['INSURANCE', 'INSPECTION'];
    this.setData({ 'form.serviceType': types[e.detail.value] });
  },

  submit() {
    if (!this.data.form.vehicleInfo.trim()) { wx.showToast({ title: '请输入车辆信息', icon: 'none' }); return; }
    if (!this.data.form.contactName.trim()) { wx.showToast({ title: '请输入联系人', icon: 'none' }); return; }
    if (!this.data.form.contactPhone.trim()) { wx.showToast({ title: '请输入联系电话', icon: 'none' }); return; }
    api.applyInsurance(this.data.form).then(() => {
      wx.showToast({ title: '申请已提交', icon: 'success' });
      this.setData({ showForm: false, form: { serviceType: 'INSURANCE', vehicleInfo: '', contactName: '', contactPhone: '', remark: '' } });
      this.loadServices();
    }).catch(() => {});
  },

  getTypeText(type) {
    return type === 'INSURANCE' ? '车险服务' : type === 'INSPECTION' ? '年检服务' : type;
  },

  getStatusText(status) {
    const map = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' };
    return map[status] || '未知';
  }
});