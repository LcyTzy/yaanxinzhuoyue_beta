const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    orders: [],
    types: [],
    showForm: false,
    form: { serviceTypeId: '', vehicleInfo: '', contactName: '', contactPhone: '', appointmentTime: '', remark: '' },
    loading: false,
    pageNum: 1,
    hasMore: true
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadTypes();
    this.setData({ pageNum: 1, orders: [], hasMore: true });
    this.loadOrders();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) this.loadOrders();
  },

  loadTypes() {
    api.getServiceTypes().then(types => {
      this.setData({ types: types || [] });
    }).catch(() => {});
  },

  loadOrders() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    api.getServiceOrders({ pageNum: this.data.pageNum, pageSize: 10 }).then(res => {
      const records = res.records || [];
      this.setData({
        orders: this.data.pageNum === 1 ? records : this.data.orders.concat(records),
        hasMore: records.length >= 10,
        pageNum: this.data.pageNum + 1,
        loading: false
      });
    }).catch(() => this.setData({ loading: false }));
  },

  showForm() { this.setData({ showForm: true }); },
  hideForm() { this.setData({ showForm: false }); },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  onTypeChange(e) {
    this.setData({ 'form.serviceTypeId': this.data.types[e.detail.value].id });
  },

  submit() {
    if (!this.data.form.serviceTypeId) { wx.showToast({ title: '请选择服务类型', icon: 'none' }); return; }
    if (!this.data.form.contactName.trim()) { wx.showToast({ title: '请输入联系人', icon: 'none' }); return; }
    if (!this.data.form.contactPhone.trim()) { wx.showToast({ title: '请输入联系电话', icon: 'none' }); return; }
    api.createServiceOrder(this.data.form).then(() => {
      wx.showToast({ title: '预约成功', icon: 'success' });
      this.setData({ showForm: false, form: { serviceTypeId: '', vehicleInfo: '', contactName: '', contactPhone: '', appointmentTime: '', remark: '' }, pageNum: 1, orders: [], hasMore: true });
      this.loadOrders();
    }).catch(() => {});
  },

  getStatusText(status) {
    const map = { 'PENDING': '待确认', 'CONFIRMED': '已确认', 'IN_PROGRESS': '服务中', 'COMPLETED': '已完成', 'CANCELLED': '已取消' };
    return map[status] || status || '未知';
  }
});