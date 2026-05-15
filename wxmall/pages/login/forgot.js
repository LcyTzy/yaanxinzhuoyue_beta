const api = require('../../utils/api');

Page({
  data: {
    phone: '',
    code: '',
    newPassword: '',
    countdown: 0,
    sending: false,
    loading: false
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  onCodeInput(e) {
    this.setData({ code: e.detail.value });
  },

  onNewPasswordInput(e) {
    this.setData({ newPassword: e.detail.value });
  },

  sendCode() {
    const { phone } = this.data;
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '手机号格式不正确', icon: 'none' });
      return;
    }

    this.setData({ sending: true });
    api.sendResetCode({ phone }).then(() => {
      wx.showToast({ title: '验证码已发送', icon: 'success' });
      this.setData({ countdown: 60 });
      this.startCountdown();
    }).catch(() => {}).finally(() => {
      this.setData({ sending: false });
    });
  },

  startCountdown() {
    const timer = setInterval(() => {
      if (this.data.countdown <= 1) {
        clearInterval(timer);
        this.setData({ countdown: 0 });
      } else {
        this.setData({ countdown: this.data.countdown - 1 });
      }
    }, 1000);
  },

  resetPassword() {
    const { phone, code, newPassword } = this.data;
    if (!phone) {
      wx.showToast({ title: '请输入手机号', icon: 'none' });
      return;
    }
    if (!code) {
      wx.showToast({ title: '请输入验证码', icon: 'none' });
      return;
    }
    if (!newPassword || newPassword.length < 6) {
      wx.showToast({ title: '新密码至少6位', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    api.resetPassword({ phone, code, newPassword }).then(() => {
      wx.showToast({ title: '密码重置成功，请登录', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    }).catch(() => {}).finally(() => {
      this.setData({ loading: false });
    });
  },

  goLogin() {
    wx.navigateBack();
  }
});