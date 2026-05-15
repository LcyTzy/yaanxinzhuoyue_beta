const api = require('../../utils/api');

const HISTORY_KEY = 'search_history';
const MAX_HISTORY = 10;

Page({
  data: {
    keyword: '',
    products: [],
    pageNum: 1,
    hasMore: true,
    loading: false,
    searched: false,
    totalCount: 0,
    history: [],
    hotKeywords: ['机油滤芯', '刹车片', '火花塞', '空调滤芯', '雨刮器', '轮胎']
  },

  onLoad(options) {
    this.loadHistory();
    if (options.keyword) {
      this.setData({ keyword: options.keyword });
      this.onSearch();
    }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.search();
    }
  },

  onPullDownRefresh() {
    if (this.data.searched) {
      this.setData({ products: [], pageNum: 1, hasMore: true });
      this.search().then(() => wx.stopPullDownRefresh());
    } else {
      wx.stopPullDownRefresh();
    }
  },

  loadHistory() {
    const history = wx.getStorageSync(HISTORY_KEY) || [];
    this.setData({ history });
  },

  saveHistory(keyword) {
    let history = this.data.history.filter(h => h !== keyword);
    history.unshift(keyword);
    if (history.length > MAX_HISTORY) history = history.slice(0, MAX_HISTORY);
    this.setData({ history });
    wx.setStorageSync(HISTORY_KEY, history);
  },

  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定清空搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({ history: [] });
          wx.removeStorageSync(HISTORY_KEY);
        }
      }
    });
  },

  searchHistory(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ keyword });
    this.onSearch();
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  clearSearch() {
    this.setData({ keyword: '', searched: false, products: [] });
  },

  onSearch() {
    const keyword = this.data.keyword.trim();
    if (!keyword) return;
    this.saveHistory(keyword);
    this.setData({ products: [], pageNum: 1, hasMore: true, searched: true });
    this.search();
  },

  search() {
    this.setData({ loading: true });
    return api.getProducts({
      keyword: this.data.keyword,
      pageNum: this.data.pageNum,
      pageSize: 10
    }).then(res => {
      const records = res.records || [];
      this.setData({
        products: this.data.pageNum === 1 ? records : [...this.data.products, ...records],
        hasMore: records.length >= 10,
        totalCount: res.total || 0,
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

  onProductImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['products[' + index + '].image']: '/images/placeholder.png' });
  },

  goBack() {
    wx.navigateBack();
  }
});