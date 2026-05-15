const api = require('../../utils/api');

Page({
  data: {
    id: '',
    name: '',
    phone: '',
    address: '',
    isDefault: false,
    loading: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ id: options.id });
      this.loadAddress(options.id);
    }
  },

  loadAddress(id) {
    api.getAddressList().then(list => {
      const addr = (list || []).find(a => a.id == id);
      if (addr) {
        this.setData({
          name: addr.receiverName || addr.name || '',
          phone: addr.receiverPhone || addr.phone || '',
          address: addr.fullAddress || addr.address || '',
          isDefault: addr.isDefault === 1
        });
      }
    }).catch(() => {});
  },

  onNameInput(e) { this.setData({ name: e.detail.value }); },
  onPhoneInput(e) { this.setData({ phone: e.detail.value }); },
  onAddressInput(e) { this.setData({ address: e.detail.value }); },
  toggleDefault() { this.setData({ isDefault: !this.data.isDefault }); },

  save() {
    const { id, name, phone, address, isDefault } = this.data;
    if (!name.trim()) { wx.showToast({ title: '请输入收货人', icon: 'none' }); return; }
    if (!/^1[3-9]\d{9}$/.test(phone)) { wx.showToast({ title: '手机号格式不正确', icon: 'none' }); return; }
    if (!address.trim()) { wx.showToast({ title: '请输入详细地址', icon: 'none' }); return; }

    this.setData({ loading: true });
    const data = { receiverName: name, receiverPhone: phone, fullAddress: address, isDefault: isDefault ? 1 : 0 };

    const promise = id ? api.updateAddress(id, data) : api.addAddress(data);
    promise.then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => wx.navigateBack(), 1000);
    }).catch(() => {
      this.setData({ loading: false });
    });
  }
});