package com.zhantu.autopartsmall.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.zhantu.autopartsmall.ui.screens.cart.CartScreen
import com.zhantu.autopartsmall.ui.screens.profile.ProfileScreen

sealed class MainTab(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : MainTab("home", "首页", Icons.Default.Home)
    object Category : MainTab("category", "分类", Icons.Default.List)
    object VinQuery : MainTab("vin_query", "VIN查询", Icons.Default.Search)
    object Cart : MainTab("cart", "购物车", Icons.Default.ShoppingCart)
    object Profile : MainTab("profile", "我的", Icons.Default.Person)
}

@Composable
fun MainScreen(
    onProductClick: (Long) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onNavigateToProductList: (String?) -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToOrders: (Int?) -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAppointment: () -> Unit = {},
    onNavigateToPoints: () -> Unit = {},
    onNavigateToCoupons: () -> Unit = {},
    onCheckout: () -> Unit = {},
    cartViewModel: com.zhantu.autopartsmall.viewmodel.CartViewModel
) {
    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(MainTab.Home, MainTab.Category, MainTab.VinQuery, MainTab.Cart, MainTab.Profile).forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen(
                    onProductClick = onProductClick,
                    onCategoryClick = { categoryName ->
                        onNavigateToProductList(categoryName)
                    }
                )
                MainTab.Category -> CategoryScreen(
                    onProductClick = onProductClick,
                    onNavigateToProductList = onNavigateToProductList
                )
                MainTab.VinQuery -> com.zhantu.autopartsmall.ui.screens.vin.VinQueryScreen(
                    onProductClick = onProductClick
                )
                MainTab.Cart -> CartScreen(viewModel = cartViewModel, onCheckout = onCheckout)
                MainTab.Profile -> ProfileScreen(
                    onLogout = onLogout,
                    onNavigateToOrders = onNavigateToOrders,
                    onNavigateToAddress = onNavigateToAddress,
                    onNavigateToFavorites = onNavigateToFavorites,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToAppointment = onNavigateToAppointment,
                    onNavigateToPoints = onNavigateToPoints,
                    onNavigateToCoupons = onNavigateToCoupons
                )
            }
        }
    }
}
