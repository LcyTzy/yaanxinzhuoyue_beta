const api = require('../../utils/api');

Page({
  data: {
    categories: [],
    activeCategoryId: 0,
    products: [],
    pageNum: 1,
    hasMore: true,
    loading: false
  },

  onLoad() {
    api.getCategoryTree().then(categories => {
      this.setData({ categories });
      if (categories.length > 0) {
        this.setData({ activeCategoryId: categories[0].id });
        this.loadProducts();
      }
    }).catch(() => {});
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 });
      this.loadProducts();
    }
  },

  onPullDownRefresh() {
    this.setData({ products: [], pageNum: 1, hasMore: true });
    this.loadProducts().then(() => wx.stopPullDownRefresh());
  },

  switchCategory(e) {
    const id = e.currentTarget.dataset.id;
    if (id === this.data.activeCategoryId) return;
    this.setData({ activeCategoryId: id, products: [], pageNum: 1, hasMore: true });
    this.loadProducts();
  },

  loadProducts() {
    this.setData({ loading: true });
    return api.getProducts({
      categoryId: this.data.activeCategoryId,
      pageNum: this.data.pageNum,
      pageSize: 10
    }).then(res => {
      const records = res.records || [];
      this.setData({
        products: this.data.pageNum === 1 ? records : [...this.data.products, ...records],
        hasMore: records.length >= 10,
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
  }
});