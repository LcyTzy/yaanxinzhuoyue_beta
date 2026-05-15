const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    notifications: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.setData({ pageNum: 1, notifications: [], hasMore: true });
    this.loadNotifications();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadNotifications();
    }
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, notifications: [], hasMore: true });
    this.loadNotifications().then(() => wx.stopPullDownRefresh());
  },

  loadNotifications() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    return api.getNotifications({ pageNum: this.data.pageNum, pageSize: this.data.pageSize }).then(res => {
      const records = res.records || [];
      this.setData({
        notifications: this.data.pageNum === 1 ? records : this.data.notifications.concat(records),
        hasMore: records.length >= this.data.pageSize,
        pageNum: this.data.pageNum + 1,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  markRead(e) {
    const id = e.currentTarget.dataset.id;
    api.markRead(id).then(() => {
      const notifications = this.data.notifications.map(n => {
        if (n.id === id) n.isRead = 1;
        return n;
      });
      this.setData({ notifications });
    }).catch(() => {});
  },

  markAllRead() {
    wx.showModal({
      title: '提示',
      content: '确定全部标记已读？',
      success: (res) => {
        if (res.confirm) {
          api.markAllRead().then(() => {
            const notifications = this.data.notifications.map(n => {
              n.isRead = 1;
              return n;
            });
            this.setData({ notifications });
            wx.showToast({ title: '已全部标记已读', icon: 'success' });
          }).catch(() => {});
        }
      }
    });
  },

  getTypeText(type) {
    const map = { 'ORDER': '订单通知', 'SYSTEM': '系统通知', 'PROMOTION': '促销活动', 'SERVICE': '服务通知' };
    return map[type] || '消息通知';
  }
});