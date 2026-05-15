const app = getApp();

Page({
  data: {
    products: [],
    specs: []
  },

  onShow() {
    const compareList = app.globalData.compareList || [];
    if (compareList.length === 0) {
      this.setData({ products: [], specs: [] });
      return;
    }
    this.setData({ products: compareList });
    this.buildSpecs(compareList);
  },

  buildSpecs(products) {
    const specMap = {};
    products.forEach(p => {
      if (p.specs && Array.isArray(p.specs)) {
        p.specs.forEach(s => {
          if (!specMap[s.name]) specMap[s.name] = {};
          specMap[s.name][p.id] = s.value || '-';
        });
      }
    });
    const specs = Object.keys(specMap).map(name => ({
      name,
      values: specMap[name]
    }));
    this.setData({ specs });
  },

  removeProduct(e) {
    const id = e.currentTarget.dataset.id;
    let compareList = app.globalData.compareList || [];
    compareList = compareList.filter(p => p.id !== id);
    app.globalData.compareList = compareList;
    wx.setStorageSync('compareList', compareList);
    if (compareList.length === 0) {
      this.setData({ products: [], specs: [] });
    } else {
      this.setData({ products: compareList });
      this.buildSpecs(compareList);
    }
    wx.showToast({ title: '已移除', icon: 'success' });
  },

  clearAll() {
    wx.showModal({
      title: '提示',
      content: '确定清空所有对比商品？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.compareList = [];
          wx.setStorageSync('compareList', []);
          this.setData({ products: [], specs: [] });
        }
      }
    });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: '/pages/product/detail?id=' + id });
  }
});