const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    records: [],
    showForm: false,
    form: { vehicleInfo: '', serviceType: '', serviceDate: '', mileage: '', cost: '', shopName: '', remark: '' },
    loading: false
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadRecords();
  },

  loadRecords() {
    this.setData({ loading: true });
    api.getMaintenanceRecords().then(records => {
      this.setData({ records: records || [], loading: false });
    }).catch(() => this.setData({ loading: false }));
  },

  showForm() { this.setData({ showForm: true }); },
  hideForm() { this.setData({ showForm: false }); },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  submit() {
    if (!this.data.form.vehicleInfo.trim()) { wx.showToast({ title: '请输入车辆信息', icon: 'none' }); return; }
    if (!this.data.form.serviceType.trim()) { wx.showToast({ title: '请输入保养项目', icon: 'none' }); return; }
    api.addMaintenanceRecord(this.data.form).then(() => {
      wx.showToast({ title: '添加成功', icon: 'success' });
      this.setData({ showForm: false, form: { vehicleInfo: '', serviceType: '', serviceDate: '', mileage: '', cost: '', shopName: '', remark: '' } });
      this.loadRecords();
    }).catch(() => {});
  }
});