package com.zhantu.autopartsmall.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Main : Screen("main")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Long) = "product_detail/$productId"
    }
    object OrderConfirm : Screen("order_confirm")
    object Orders : Screen("orders?status={status}") {
        fun createRoute(status: Int? = null): String {
            return if (status != null) "orders?status=$status" else "orders"
        }
    }
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: Long) = "order_detail/$orderId"
    }
    object Address : Screen("address")
    object Favorites : Screen("favorites")
    object Checkout : Screen("checkout")
    object VinQuery : Screen("vin_query")
    object Settings : Screen("settings")
    object Appointment : Screen("appointment")
    object MyAppointments : Screen("my_appointments")
    object Points : Screen("points")
    object Coupons : Screen("coupons")
    object ProductList : Screen("product_list?categoryId={categoryId}&keyword={keyword}") {
        fun createRoute(categoryId: Long? = null, keyword: String? = null): String {
            var route = "product_list"
            val params = mutableListOf<String>()
            if (categoryId != null) params.add("categoryId=$categoryId")
            if (keyword != null) params.add("keyword=$keyword")
            if (params.isNotEmpty()) route += "?" + params.joinToString("&")
            return route
        }
    }
    object VehicleSelector : Screen("vehicle_selector")
}
