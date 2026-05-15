const api = require('../../utils/api');

Page({
  data: {
    keyword: '',
    products: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: false,
    loading: false
  },

  onLoad(options) {
    if (options.keyword) {
      this.setData({ keyword: options.keyword });
    }
    this.loadProducts();
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, products: [] });
    this.loadProducts().then(() => wx.stopPullDownRefresh());
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  doSearch() {
    this.setData({ pageNum: 1, products: [] });
    this.loadProducts();
  },

  loadProducts() {
    this.setData({ loading: true });
    const params = {
      pageNum: this.data.pageNum,
      pageSize: this.data.pageSize
    };
    if (this.data.keyword) {
      params.keyword = this.data.keyword;
    }

    return api.getProducts(params).then(res => {
      const records = res.records || [];
      this.setData({
        products: this.data.pageNum === 1 ? records : this.data.products.concat(records),
        hasMore: records.length >= this.data.pageSize,
        loading: false
      });
    }).catch(() => {
      this.setData({ loading: false });
    });
  },

  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    this.setData({ pageNum: this.data.pageNum + 1 });
    this.loadProducts();
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/product/detail?id=' + id });
  },

  onProductImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['products[' + index + '].image']: '/images/product.png' });
  }
});