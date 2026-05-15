const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    form: {
      nickname: '',
      avatar: '',
      email: '',
      gender: 0
    },
    submitting: false
  },

  onShow() {
    const userInfo = app.globalData.userInfo || {};
    this.setData({
      form: {
        nickname: userInfo.nickname || '',
        avatar: userInfo.avatar || '',
        email: userInfo.email || '',
        gender: userInfo.gender || 0
      }
    });
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ ['form.' + field]: e.detail.value });
  },

  onGenderChange(e) {
    this.setData({ 'form.gender': parseInt(e.detail.value) });
  },

  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFilePaths[0];
        wx.showLoading({ title: '上传中...' });
        wx.uploadFile({
          url: app.globalData.baseUrl + '/api/upload/image',
          filePath: tempFilePath,
          name: 'file',
          header: { 'Authorization': 'Bearer ' + app.globalData.token },
          success: (uploadRes) => {
            wx.hideLoading();
            try {
              const data = JSON.parse(uploadRes.data);
              if (data.code === 200) {
                this.setData({ 'form.avatar': data.data });
                wx.showToast({ title: '上传成功', icon: 'success' });
              } else {
                wx.showToast({ title: data.message || '上传失败', icon: 'none' });
              }
            } catch (e) {
              wx.showToast({ title: '上传失败', icon: 'none' });
            }
          },
          fail: () => {
            wx.hideLoading();
            wx.showToast({ title: '上传失败', icon: 'none' });
          }
        });
      }
    });
  },

  submit() {
    if (!this.data.form.nickname.trim()) {
      wx.showToast({ title: '请输入昵称', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    api.updateUserInfo(this.data.form).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' });
      app.globalData.userInfo = { ...app.globalData.userInfo, ...this.data.form };
      setTimeout(() => wx.navigateBack(), 1500);
    }).catch(() => {
      wx.showToast({ title: '保存失败', icon: 'none' });
    }).finally(() => {
      this.setData({ submitting: false });
    });
  }
});