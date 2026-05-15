const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    loginType: 'user',
    phone: '',
    password: '',
    loading: false
  },

  switchTab(e) {
    this.setData({ loginType: e.currentTarget.dataset.type });
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  doLogin() {
    const { phone, password, loginType } = this.data;
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' });
      return;
    }
    if (!password || password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return;
    }

    this.setData({ loading: true });

    if (loginType === 'admin') {
      api.adminLogin({ username: phone, password }).then(res => {
        app.setToken(res.token);
        app.setUserInfo({ phone: res.username, role: res.role || 'ADMIN' });
        wx.showToast({ title: '管理员登录成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      }).catch(() => {
        this.setData({ loading: false });
      });
    } else {
      api.login({ phone, password }).then(res => {
        app.setToken(res.token);
        app.setUserInfo(res.user);
        wx.showToast({ title: '登录成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      }).catch(() => {
        this.setData({ loading: false });
      });
    }
  },

  goRegister() {
    wx.navigateTo({ url: '/pages/login/register' });
  },

  goForgot() {
    wx.navigateTo({ url: '/pages/login/forgot' });
  }
});