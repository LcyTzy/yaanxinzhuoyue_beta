import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/MallLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/ProductList.vue'),
        meta: { title: '商品列表' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车' }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/Checkout.vue'),
        meta: { title: '结算' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/MyOrders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'address',
        name: 'Address',
        component: () => import('@/views/Address.vue'),
        meta: { title: '收货地址' }
      },
      {
        path: 'coupons',
        name: 'Coupons',
        component: () => import('@/views/Coupons.vue'),
        meta: { title: '优惠券' }
      },
      {
        path: 'points',
        name: 'Points',
        component: () => import('@/views/Points.vue'),
        meta: { title: '我的积分' }
      },
      {
        path: 'service',
        name: 'ServiceAppointment',
        component: () => import('@/views/ServiceAppointment.vue'),
        meta: { title: '预约修车' }
      },
      {
        path: 'parts/:vin',
        name: 'PartsPage',
        component: () => import('@/views/PartsPage.vue'),
        meta: { title: '适配配件' }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/Notifications.vue'),
        meta: { title: '消息通知' }
      },
      {
        path: 'membership',
        name: 'Membership',
        component: () => import('@/views/Membership.vue'),
        meta: { title: '会员中心' }
      },
      {
        path: 'points-mall',
        name: 'PointsMall',
        component: () => import('@/views/PointsMall.vue'),
        meta: { title: '积分商城' }
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('@/views/Maintenance.vue'),
        meta: { title: '保养档案' }
      },
      {
        path: 'invoice',
        name: 'Invoice',
        component: () => import('@/views/Invoice.vue'),
        meta: { title: '发票管理' }
      },
      {
        path: 'inquiry',
        name: 'Inquiry',
        component: () => import('@/views/Inquiry.vue'),
        meta: { title: '在线询价' }
      },
      {
        path: 'insurance',
        name: 'Insurance',
        component: () => import('@/views/Insurance.vue'),
        meta: { title: '保险年检' }
      },
      {
        path: 'compare',
        name: 'ProductCompare',
        component: () => import('@/views/ProductCompare.vue'),
        meta: { title: '商品对比' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('userToken')
  const needAuth = ['/cart', '/checkout', '/orders', '/profile', '/favorites', '/address', '/coupons', '/points', '/maintenance', '/invoice', '/inquiry', '/insurance', '/notifications', '/membership', '/points-mall']
  if (needAuth.includes(to.path) && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
