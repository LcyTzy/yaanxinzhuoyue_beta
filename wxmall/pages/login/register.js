const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    phone: '',
    nickname: '',
    password: '',
    confirmPassword: '',
    captcha: '',
    captchaImage: '',
    captchaKey: '',
    loading: false
  },

  onLoad() {
    this.refreshCaptcha();
  },

  refreshCaptcha() {
    api.getCaptcha().then(res => {
      this.setData({
        captchaImage: res.image || '',
        captchaKey: res.captchaKey || ''
      });
    }).catch(() => {});
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  onCaptchaInput(e) {
    this.setData({ captcha: e.detail.value });
  },

  register() {
    const { phone, nickname, password, confirmPassword, captcha, captchaKey } = this.data;
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
    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }
    if (!captcha || captcha.length < 4) {
      wx.showToast({ title: '请输入验证码', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    api.register({
      phone,
      password,
      nickname: nickname || '用户' + phone.slice(-4),
      captcha,
      captchaKey
    }).then(token => {
      app.setToken(token);
      api.getUserInfo().then(user => {
        app.setUserInfo(user);
        wx.showToast({ title: '注册成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/index/index' });
        }, 1000);
      }).catch(() => {
        wx.switchTab({ url: '/pages/index/index' });
      });
    }).catch(() => {
      this.setData({ loading: false });
      this.refreshCaptcha();
    });
  },

  goLogin() {
    wx.navigateBack();
  }
});