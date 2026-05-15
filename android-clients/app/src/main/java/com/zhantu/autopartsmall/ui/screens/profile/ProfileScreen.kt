package com.zhantu.autopartsmall.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavigateToOrders: (Int?) -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAppointment: () -> Unit = {},
    onNavigateToPoints: () -> Unit = {},
    onNavigateToCoupons: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var userPhone by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        context.dataStore.data.collect { preferences ->
            userPhone = preferences[PreferencesKeys.PHONE_KEY]
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userPhone ?: "用户",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userPhone?.let { "手机号: $it" } ?: "未登录",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val orderMenuItems = listOf(
                Icons.Default.Payment to "待付款" to 0,
                Icons.Default.LocalShipping to "待发货" to 1,
                Icons.AutoMirrored.Filled.ReceiptLong to "待收货" to 2,
                Icons.Default.Star to "已完成" to 3
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "我的订单",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        orderMenuItems.forEach { (pair, status) ->
                            val (icon, label) = pair
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { onNavigateToOrders(status) }
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            val menuItems = listOf(
                Icons.Default.Build to "预约修车",
                Icons.Default.LocationOn to "收货地址",
                Icons.Default.Favorite to "我的收藏",
                Icons.Default.Star to "我的积分",
                Icons.Outlined.LocalOffer to "优惠券",
                Icons.Default.Settings to "设置",
                Icons.AutoMirrored.Filled.Logout to "退出登录"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    menuItems.forEach { (icon, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (label) {
                                        "预约修车" -> onNavigateToAppointment()
                                        "收货地址" -> onNavigateToAddress()
                                        "我的收藏" -> onNavigateToFavorites()
                                        "我的积分" -> onNavigateToPoints()
                                        "优惠券" -> onNavigateToCoupons()
                                        "设置" -> onNavigateToSettings()
                                        "退出登录" -> showLogoutDialog = true
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(24.dp),
                                tint = if (label == "退出登录") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = label,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f),
                                color = if (label == "退出登录") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("退出登录") },
            text = { Text("确定要退出登录吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences.clear()
                            }
                            onLogout()
                        }
                        showLogoutDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
