package com.zhantu.autopartsmall.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zhantu.autopartsmall.ui.screens.address.AddressScreen
import com.zhantu.autopartsmall.ui.screens.appointment.AppointmentScreen
import com.zhantu.autopartsmall.ui.screens.appointment.MyAppointmentsScreen
import com.zhantu.autopartsmall.ui.screens.checkout.CheckoutScreen
import com.zhantu.autopartsmall.ui.screens.coupons.CouponsScreen
import com.zhantu.autopartsmall.ui.screens.favorite.FavoritesScreen
import com.zhantu.autopartsmall.ui.screens.home.HomeScreen
import com.zhantu.autopartsmall.ui.screens.home.MainScreen
import com.zhantu.autopartsmall.ui.screens.home.CategoryScreen
import com.zhantu.autopartsmall.ui.screens.login.LoginScreen
import com.zhantu.autopartsmall.ui.screens.login.RegisterScreen
import com.zhantu.autopartsmall.ui.screens.login.ForgotPasswordScreen
import com.zhantu.autopartsmall.ui.screens.order.OrdersScreen
import com.zhantu.autopartsmall.ui.screens.order.OrderDetailScreen
import com.zhantu.autopartsmall.ui.screens.points.PointsScreen
import com.zhantu.autopartsmall.ui.screens.product.ProductDetailScreen
import com.zhantu.autopartsmall.ui.screens.product.ProductListScreen
import com.zhantu.autopartsmall.ui.screens.settings.SettingsScreen
import com.zhantu.autopartsmall.ui.screens.vehicle.VehicleSelectorScreen
import com.zhantu.autopartsmall.ui.screens.vin.VinQueryScreen
import com.zhantu.autopartsmall.ui.screens.admin.AdminNavigation
import com.zhantu.autopartsmall.viewmodel.CartViewModel
import com.zhantu.autopartsmall.viewmodel.FavoriteViewModel
import com.zhantu.autopartsmall.viewmodel.OrderViewModel
import com.zhantu.autopartsmall.viewmodel.AddressViewModel
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.runBlocking

@Composable
fun AppNavigation(
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val cartViewModel: CartViewModel = viewModel()
    val favoriteViewModel: FavoriteViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Main.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    isLoggedIn = true
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onCategoryClick = { categoryName ->
                    navController.navigate("product_list?keyword=$categoryName")
                },
                onNavigateToProductList = { keyword ->
                    if (keyword != null) {
                        navController.navigate("product_list?keyword=$keyword")
                    } else {
                        navController.navigate("product_list")
                    }
                },
                onLogout = {
                    runBlocking {
                        (context.applicationContext as AutoPartsApp).dataStore.edit { preferences ->
                            preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY] = ""
                            preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.IS_ADMIN_KEY] = false
                        }
                    }
                    isLoggedIn = false
                    onLogout()
                },
                onNavigateToOrders = { status ->
                    if (status != null) {
                        navController.navigate("${Screen.Orders.route}?status=$status")
                    } else {
                        navController.navigate(Screen.Orders.route)
                    }
                },
                onNavigateToAddress = {
                    navController.navigate(Screen.Address.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAppointment = {
                    navController.navigate(Screen.Appointment.route)
                },
                onNavigateToPoints = {
                    navController.navigate(Screen.Points.route)
                },
                onNavigateToCoupons = {
                    navController.navigate(Screen.Coupons.route)
                },
                onCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                cartViewModel = cartViewModel
            )
        }
        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull() ?: 0L
            ProductDetailScreen(
                productId = productId,
                onBack = { navController.popBackStack() },
                onAddToCart = { pid, qty ->
                    cartViewModel.addToCart(pid, qty)
                    Toast.makeText(context, "已加入购物车", Toast.LENGTH_SHORT).show()
                },
                favoriteViewModel = favoriteViewModel
            )
        }
        composable(Screen.Orders.route) { backStackEntry ->
            val status = backStackEntry.arguments?.getString("status")?.toIntOrNull()
            OrdersScreen(
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                viewModel = orderViewModel,
                initialStatus = status
            )
        }
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId")?.toLongOrNull() ?: 0L
            OrderDetailScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                viewModel = orderViewModel
            )
        }
        composable(Screen.Address.route) {
            AddressScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBack = { navController.popBackStack() },
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                viewModel = favoriteViewModel
            )
        }
        composable(Screen.Checkout.route) {
            val addressViewModel: com.zhantu.autopartsmall.viewmodel.AddressViewModel = viewModel()
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderSuccess = {
                    orderViewModel.loadOrders()
                    navController.navigate(Screen.Orders.route) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                    }
                },
                cartViewModel = cartViewModel,
                addressViewModel = addressViewModel,
                orderViewModel = orderViewModel
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToVinQuery = {
                    navController.navigate(Screen.VinQuery.route)
                }
            )
        }
        composable(Screen.VinQuery.route) {
            VinQueryScreen(
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                }
            )
        }
        composable("category_screen/{categoryName}") { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            com.zhantu.autopartsmall.ui.screens.home.CategoryScreen(
                initialCategoryName = categoryName,
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Appointment.route) {
            AppointmentScreen(
                onBack = { navController.popBackStack() },
                onNavigateToMyAppointments = {
                    navController.navigate(Screen.MyAppointments.route)
                },
                application = context.applicationContext as AutoPartsApp
            )
        }
        composable(Screen.MyAppointments.route) {
            MyAppointmentsScreen(
                onBack = { navController.popBackStack() },
                application = context.applicationContext as AutoPartsApp
            )
        }
        composable(Screen.Points.route) {
            PointsScreen(
                onBack = { navController.popBackStack() },
                application = context.applicationContext as AutoPartsApp
            )
        }
        composable(Screen.Coupons.route) {
            CouponsScreen(
                onBack = { navController.popBackStack() },
                application = context.applicationContext as AutoPartsApp
            )
        }
        composable("product_list") {
            ProductListScreen(
                initialCategoryId = null,
                initialKeyword = null,
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onBack = { navController.popBackStack() },
                application = context.applicationContext as AutoPartsApp
            )
        }
        composable(Screen.VehicleSelector.route) {
            VehicleSelectorScreen(
                onVehicleSelected = { brand, series, model ->
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
