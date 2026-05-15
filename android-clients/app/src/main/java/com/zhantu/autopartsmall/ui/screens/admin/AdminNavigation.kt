package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first

@Composable
fun AdminNavigation(
    application: android.app.Application,
    onLogout: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf<AdminScreen>(AdminScreen.Main) }

    when (currentScreen) {
        AdminScreen.Login -> {
            AdminLoginScreen(
                onLoginSuccess = {
                    currentScreen = AdminScreen.Main
                },
                onNavigateToRegister = {
                    currentScreen = AdminScreen.Register
                },
                onBackToUserLogin = {
                    onLogout()
                }
            )
        }
        AdminScreen.Register -> {
            AdminRegisterScreen(
                onRegisterSuccess = {
                    currentScreen = AdminScreen.Main
                },
                onBackToLogin = {
                    currentScreen = AdminScreen.Login
                }
            )
        }
        AdminScreen.Main -> {
            AdminMainScreen(
                onNavigateToDashboard = { currentScreen = AdminScreen.Dashboard },
                onNavigateToProducts = { currentScreen = AdminScreen.Products },
                onNavigateToCategories = { currentScreen = AdminScreen.Categories },
                onNavigateToOrders = { currentScreen = AdminScreen.Orders },
                onNavigateToUsers = { currentScreen = AdminScreen.Users },
                onNavigateToServiceAppointments = { currentScreen = AdminScreen.ServiceAppointments },
                onNavigateToVehicles = { currentScreen = AdminScreen.Vehicles },
                onNavigateToVinQuery = { currentScreen = AdminScreen.VinQuery },
                onLogout = {
                    onLogout()
                }
            )
        }
        AdminScreen.Dashboard -> {
            AdminDashboardScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.Products -> {
            AdminProductScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.Categories -> {
            AdminCategoryScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.Orders -> {
            AdminOrderScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.Users -> {
            AdminUserScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.ServiceAppointments -> {
            AdminServiceAppointmentScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.Vehicles -> {
            AdminVehicleScreen(
                onBack = { currentScreen = AdminScreen.Main },
                application = application
            )
        }
        AdminScreen.VinQuery -> {
            com.zhantu.autopartsmall.ui.screens.vin.VinQueryScreen(
                onProductClick = { },
                onBack = { currentScreen = AdminScreen.Main }
            )
        }
    }
}

enum class AdminScreen {
    Login,
    Register,
    Main,
    Dashboard,
    Products,
    Categories,
    Orders,
    Users,
    ServiceAppointments,
    Vehicles,
    VinQuery
}
