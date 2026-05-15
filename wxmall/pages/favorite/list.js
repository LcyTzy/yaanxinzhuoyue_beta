const api = require('../../../utils/api');
const app = getApp();

Page({
  data: {
    favorites: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    hasMore: true
  },

  onShow() {
    if (!app.checkLogin()) return;
    this.setData({ pageNum: 1, favorites: [], hasMore: true });
    this.loadFavorites();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadFavorites();
    }
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, favorites: [], hasMore: true });
    this.loadFavorites().then(() => wx.stopPullDownRefresh());
  },

  loadFavorites() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    return api.getFavoriteList({ pageNum: this.data.pageNum, pageSize: this.data.pageSize }).then(res => {
      const records = res.records || [];
      this.setData({
        favorites: this.data.pageNum === 1 ? records : this.data.favorites.concat(records),
        total: res.total || 0,
        hasMore: records.length >= this.data.pageSize,
        pageNum: this.data.pageNum + 1,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/product/detail?id=' + id });
  },

  removeFavorite(e) {
    const productId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定取消收藏？',
      success: (res) => {
        if (res.confirm) {
          api.toggleFavorite(productId).then(() => {
            wx.showToast({ title: '已取消收藏', icon: 'success' });
            const favorites = this.data.favorites.filter(f => f.productId !== productId);
            this.setData({ favorites });
          }).catch(() => {
            wx.showToast({ title: '操作失败', icon: 'none' });
          });
        }
      }
    });
  },

  onImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['favorites[' + index + '].productImage']: '/images/placeholder.png' });
  }
});