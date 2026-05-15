import { createRouter, createWebHistory } from 'vue-router'

const routes = [
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
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/ProductManage.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('@/views/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/OrderManage.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'purchase',
        name: 'Purchase',
        component: () => import('@/views/purchase/PurchaseManage.vue'),
        meta: { title: '采购管理' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/InventoryLog.vue'),
        meta: { title: '库存流水' }
      },
      {
        path: 'service-appointment',
        name: 'ServiceAppointment',
        component: () => import('@/views/service/ServiceAppointment.vue'),
        meta: { title: '服务预约' }
      },
      {
        path: 'epc/vehicle',
        name: 'VehicleManage',
        component: () => import('@/views/epc/VehicleManage.vue'),
        meta: { title: '车型管理' }
      },
      {
        path: 'epc/part-relation',
        name: 'PartRelation',
        component: () => import('@/views/epc/PartRelation.vue'),
        meta: { title: '配件关联' }
      },
      {
        path: 'vin-query',
        name: 'VinQuery',
        component: () => import('@/views/VinQuery.vue'),
        meta: { title: 'VIN查配件' }
      },
      {
        path: 'stores',
        name: 'StoreManage',
        component: () => import('@/views/StoreManage.vue'),
        meta: { title: '门店管理' }
      },
      {
        path: 'invoices',
        name: 'InvoiceManage',
        component: () => import('@/views/InvoiceManage.vue'),
        meta: { title: '发票管理' }
      },
      {
        path: 'inquiries',
        name: 'InquiryManage',
        component: () => import('@/views/InquiryManage.vue'),
        meta: { title: '询价管理' }
      },
      {
        path: 'banners',
        name: 'BannerManage',
        component: () => import('@/views/BannerManage.vue'),
        meta: { title: '轮播图管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  if (to.path !== '/login' && to.path !== '/register' && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    next()
  }
})

export default router
