const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    myLevel: null,
    discount: 0,
    levels: [],
    loading: true
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.loadData();
  },

  loadData() {
    this.setData({ loading: true });
    Promise.all([
      api.getMyMembership().catch(() => null),
      api.getMembershipLevels().catch(() => [])
    ]).then(([myData, levels]) => {
      this.setData({
        myLevel: myData ? myData.level : null,
        discount: myData ? myData.discount : 0,
        levels: levels || [],
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  getLevelName(level) {
    const names = { 'BRONZE': '青铜会员', 'SILVER': '白银会员', 'GOLD': '黄金会员', 'PLATINUM': '铂金会员', 'DIAMOND': '钻石会员' };
    return names[level] || level || '普通用户';
  },

  getLevelColor(level) {
    const colors = { 'BRONZE': '#cd7f32', 'SILVER': '#c0c0c0', 'GOLD': '#ffd700', 'PLATINUM': '#e5e4e2', 'DIAMOND': '#b9f2ff' };
    return colors[level] || '#1677ff';
  }
});