package com.zhantu.autopartsmall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhantu.autopartsmall.ui.navigation.AppNavigation
import com.zhantu.autopartsmall.ui.screens.admin.AdminNavigation
import com.zhantu.autopartsmall.ui.theme.AutoPartsMallTheme
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AutoPartsMallTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UnifiedAppNavigation()
                }
            }
        }
    }
}

@Composable
fun UnifiedAppNavigation() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
    var isAdmin by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        (context.applicationContext as AutoPartsApp).dataStore.data.collect { preferences ->
            val token = preferences[PreferencesKeys.TOKEN_KEY]
            isLoggedIn = !token.isNullOrEmpty()
            isAdmin = preferences[PreferencesKeys.IS_ADMIN_KEY] ?: false
        }
    }
    
    if (isLoggedIn == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (!isLoggedIn!!) {
        LoginModeScreen(
            onLoginSuccess = { isAdminLogin ->
                runBlocking {
                    (context.applicationContext as AutoPartsApp).dataStore.edit { preferences ->
                        preferences[PreferencesKeys.IS_ADMIN_KEY] = isAdminLogin
                    }
                }
                isAdmin = isAdminLogin
                isLoggedIn = true
            }
        )
    } else if (isAdmin) {
        AdminNavigation(
            application = context.applicationContext as AutoPartsApp,
            onLogout = {
                runBlocking {
                    (context.applicationContext as AutoPartsApp).dataStore.edit { preferences ->
                        preferences[PreferencesKeys.IS_ADMIN_KEY] = false
                        preferences[PreferencesKeys.TOKEN_KEY] = ""
                    }
                }
                isAdmin = false
                isLoggedIn = false
            }
        )
    } else {
        AppNavigation(
            onLogout = {
                runBlocking {
                    (context.applicationContext as AutoPartsApp).dataStore.edit { preferences ->
                        preferences[PreferencesKeys.TOKEN_KEY] = ""
                        preferences[PreferencesKeys.IS_ADMIN_KEY] = false
                    }
                }
                isAdmin = false
                isLoggedIn = false
            }
        )
    }
}

@Composable
fun LoginModeScreen(
    onLoginSuccess: (isAdmin: Boolean) -> Unit
) {
    var showAdminLogin by remember { mutableStateOf(false) }
    
    if (showAdminLogin) {
        com.zhantu.autopartsmall.ui.screens.admin.AdminLoginScreen(
            onLoginSuccess = {
                onLoginSuccess(true)
            },
            onNavigateToRegister = {
            },
            onBackToUserLogin = {
                showAdminLogin = false
            }
        )
    } else {
        com.zhantu.autopartsmall.ui.screens.login.LoginScreen(
            onLoginSuccess = { isAdmin ->
                onLoginSuccess(isAdmin)
            },
            onNavigateToRegister = {
            },
            onNavigateToForgotPassword = {
            },
            onSwitchToAdminLogin = {
                showAdminLogin = true
            }
        )
    }
}
