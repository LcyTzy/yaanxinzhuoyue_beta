const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    product: {},
    applicableVehicles: [],
    reviews: [],
    quantity: 1,
    currentImage: ''
  },

  onLoad(options) {
    if (options.id) {
      this.loadDetail(options.id);
      this.loadReviews(options.id);
    }
  },

  loadDetail(id) {
    api.getProductDetail(id).then(res => {
      this.setData({
        product: res.product || {},
        applicableVehicles: res.applicableVehicles || [],
        currentImage: (res.product || {}).image || ''
      });
    }).catch(() => {});
  },

  loadReviews(productId) {
    api.getReviews(productId).then(res => {
      const reviews = res.records || res || [];
      this.setData({ reviews: (Array.isArray(reviews) ? reviews : []).slice(0, 5) });
    }).catch(() => {});
  },

  previewImage() {
    const img = this.data.product.image;
    if (img) {
      wx.previewImage({ urls: [img], current: img });
    }
  },

  onImageError() {
    this.setData({ 'product.image': '/images/placeholder.png' });
  },

  addQuantity() {
    this.setData({ quantity: this.data.quantity + 1 });
  },

  subQuantity() {
    if (this.data.quantity > 1) {
      this.setData({ quantity: this.data.quantity - 1 });
    }
  },

  addToCart() {
    if (!app.checkLogin()) return;
    api.addToCart(this.data.product.id, this.data.quantity).then(() => {
      wx.showToast({ title: '已加入购物车', icon: 'success' });
    });
  },

  buyNow() {
    if (!app.checkLogin()) return;
    const product = this.data.product;
    api.addToCart(product.id, this.data.quantity).then(() => {
      return api.getCartList();
    }).then(cartList => {
      const lastItem = cartList[cartList.length - 1];
      const checkoutItems = [{
        cartId: lastItem ? lastItem.id : undefined,
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity: this.data.quantity,
        image: product.image
      }];
      wx.setStorageSync('checkoutItems', checkoutItems);
      wx.navigateTo({ url: '/pages/order/create' });
    }).catch(() => {
      wx.showToast({ title: '操作失败', icon: 'none' });
    });
  }
});