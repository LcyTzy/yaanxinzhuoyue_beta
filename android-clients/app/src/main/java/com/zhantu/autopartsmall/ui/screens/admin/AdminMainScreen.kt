package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AdminMenuItem(
    val icon: ImageVector,
    val title: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToProducts: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToUsers: () -> Unit = {},
    onNavigateToServiceAppointments: () -> Unit = {},
    onNavigateToVehicles: () -> Unit = {},
    onNavigateToVinQuery: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val menuItems = listOf(
        AdminMenuItem(Icons.Default.Dashboard, "数据概览", Color(0xFF667EEA)),
        AdminMenuItem(Icons.Default.ShoppingCart, "商品管理", Color(0xFFF093FB)),
        AdminMenuItem(Icons.Default.Category, "分类管理", Color(0xFF4FACFE)),
        AdminMenuItem(Icons.Default.ReceiptLong, "订单管理", Color(0xFF43E97B)),
        AdminMenuItem(Icons.Default.People, "用户管理", Color(0xFFFF6B6B)),
        AdminMenuItem(Icons.Default.CalendarToday, "服务预约", Color(0xFFFFB74D)),
        AdminMenuItem(Icons.Default.DirectionsCar, "车型管理", Color(0xFF26C6DA)),
        AdminMenuItem(Icons.Default.Search, "VIN查询", Color(0xFF7C4DFF))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("战途汽配管理后台", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A3A6B),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "退出登录",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems) { item ->
                AdminMenuCard(
                    item = item,
                    onClick = {
                        when (item.title) {
                            "数据概览" -> onNavigateToDashboard()
                            "商品管理" -> onNavigateToProducts()
                            "分类管理" -> onNavigateToCategories()
                            "订单管理" -> onNavigateToOrders()
                            "用户管理" -> onNavigateToUsers()
                            "服务预约" -> onNavigateToServiceAppointments()
                            "车型管理" -> onNavigateToVehicles()
                            "VIN查询" -> onNavigateToVinQuery()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AdminMenuCard(item: AdminMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }
    }
}
