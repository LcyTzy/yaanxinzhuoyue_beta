const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    cartList: [],
    allChecked: false,
    totalPrice: 0
  },

  onShow() {
    if (!app.globalData.isLogin) {
      this.setData({ cartList: [] });
      return;
    }
    this.loadCart();
  },

  onPullDownRefresh() {
    this.loadCart().then(() => wx.stopPullDownRefresh());
  },

  loadCart() {
    return api.getCartList().then(list => {
      const cartList = (list || []).map(item => ({ ...item, checked: item.checked !== 0 }));
      this.setData({ cartList });
      this.calcTotal();
    }).catch(() => {});
  },

  toggleCheck(e) {
    const index = e.currentTarget.dataset.index;
    const cartList = this.data.cartList;
    cartList[index].checked = !cartList[index].checked;
    const allChecked = cartList.every(item => item.checked);
    this.setData({ cartList, allChecked });
    this.calcTotal();
  },

  toggleAll() {
    const allChecked = !this.data.allChecked;
    const cartList = this.data.cartList.map(item => ({ ...item, checked: allChecked }));
    this.setData({ cartList, allChecked });
    this.calcTotal();
  },

  changeQty(e) {
    const index = e.currentTarget.dataset.index;
    const type = e.currentTarget.dataset.type;
    const cartList = this.data.cartList;
    const item = cartList[index];
    if (type === 'minus' && item.quantity <= 1) return;
    const newQty = type === 'minus' ? item.quantity - 1 : item.quantity + 1;
    api.updateCart(item.id, newQty).then(() => {
      cartList[index].quantity = newQty;
      this.setData({ cartList });
      this.calcTotal();
    });
  },

  deleteItem(e) {
    const index = e.currentTarget.dataset.index;
    const item = this.data.cartList[index];
    wx.showModal({
      title: '提示',
      content: '确定要删除该商品吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteCart(item.id).then(() => {
            const cartList = this.data.cartList;
            cartList.splice(index, 1);
            this.setData({ cartList });
            this.calcTotal();
          });
        }
      }
    });
  },

  calcTotal() {
    const checkedItems = this.data.cartList.filter(item => item.checked);
    const totalPrice = checkedItems.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0);
    this.setData({ totalPrice: totalPrice.toFixed(2) });
  },

  checkout() {
    const checkedItems = this.data.cartList.filter(item => item.checked);
    if (checkedItems.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }
    const checkoutItems = checkedItems.map(item => ({
      cartId: item.id,
      productId: item.productId,
      productName: item.productName,
      price: item.price,
      quantity: item.quantity,
      mainImage: item.productImage
    }));
    wx.setStorageSync('checkoutItems', checkoutItems);
    wx.navigateTo({ url: '/pages/order/create' });
  },

  goShopping() {
    wx.switchTab({ url: '/pages/category/category' });
  },

  onItemImageError(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({ ['cartList[' + index + '].productImage']: '/images/placeholder.png' });
  }
});