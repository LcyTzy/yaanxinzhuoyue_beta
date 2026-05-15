const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    categories: [],
    activeCategory: '',
    products: [],
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    keyword: ''
  },

  onLoad() {
    this.loadCategories();
    this.loadProducts();
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts();
    }
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, products: [], hasMore: true });
    this.loadProducts().then(() => wx.stopPullDownRefresh());
  },

  loadCategories() {
    api.getCategories().then(categories => {
      this.setData({ categories: categories || [] });
    }).catch(() => {});
  },

  switchCategory(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ activeCategory: id, pageNum: 1, products: [], hasMore: true });
    this.loadProducts();
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.setData({ pageNum: 1, products: [], hasMore: true });
    this.loadProducts();
  },

  loadProducts() {
    if (this.data.loading) return;
    this.setData({ loading: true });
    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };
    if (this.data.activeCategory) params.categoryId = this.data.activeCategory;
    if (this.data.keyword) params.keyword = this.data.keyword;
    return api.getProducts(params).then(res => {
      const records = res.records || [];
      this.setData({
        products: this.data.pageNum === 1 ? records : this.data.products.concat(records),
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

  addToCart(e) {
    if (!app.checkLogin()) return;
    const item = e.currentTarget.dataset.item;
    api.addToCart({ productId: item.id, quantity: 1 }).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' });
    }).catch(() => {});
  },

  onImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['products[' + index + '].image']: '/images/placeholder.png' });
  }
});