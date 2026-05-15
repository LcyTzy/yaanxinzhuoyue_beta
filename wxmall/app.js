App({
  onLaunch() {
    const token = wx.getStorageSync('token');
    if (token) {
      this.globalData.token = token;
      this.globalData.isLogin = true;
      const userInfo = wx.getStorageSync('userInfo');
      if (userInfo) {
        this.globalData.userInfo = userInfo;
        this.globalData.role = userInfo.role || 'USER';
      }
    }
  },

  globalData: {
    token: '',
    isLogin: false,
    userInfo: null,
    role: 'USER',
    baseUrl: 'https://yaanxinzhuoyue.top'
},

  checkLogin() {
    if (!this.globalData.isLogin) {
      wx.navigateTo({ url: '/pages/login/login' });
      return false;
    }
    return true;
  },

  isAdmin() {
    return this.globalData.role === 'ADMIN';
  },

  setToken(token) {
    this.globalData.token = token;
    this.globalData.isLogin = true;
    wx.setStorageSync('token', token);
  },

  setUserInfo(userInfo) {
    this.globalData.userInfo = userInfo;
    this.globalData.role = userInfo.role || 'USER';
    wx.setStorageSync('userInfo', userInfo);
  },

  logout() {
    this.globalData.token = '';
    this.globalData.isLogin = false;
    this.globalData.userInfo = null;
    this.globalData.role = 'USER';
    wx.removeStorageSync('token');
    wx.removeStorageSync('userInfo');
  }
});