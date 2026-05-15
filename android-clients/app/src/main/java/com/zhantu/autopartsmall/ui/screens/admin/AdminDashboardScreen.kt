package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.data.network.RetrofitClient
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class StatCard(
    val icon: ImageVector,
    val title: String,
    val value: String,
    val gradientStart: Color,
    val gradientEnd: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    var productCount by remember { mutableStateOf("0") }
    var userCount by remember { mutableStateOf("0") }
    var orderCount by remember { mutableStateOf("0") }
    var categoryCount by remember { mutableStateOf("0") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminStats("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        productCount = response.data["productCount"]?.toString() ?: "0"
                        userCount = response.data["userCount"]?.toString() ?: "0"
                        orderCount = response.data["orderCount"]?.toString() ?: "0"
                        categoryCount = response.data["categoryCount"]?.toString() ?: "0"
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminDashboard", "获取统计数据失败", e)
            } finally {
                isLoading = false
            }
        }
    }

    val statCards = listOf(
        StatCard(
            Icons.Default.ShoppingCart,
            "商品总数",
            productCount,
            Color(0xFF667EEA),
            Color(0xFF764BA2)
        ),
        StatCard(
            Icons.Default.People,
            "用户总数",
            userCount,
            Color(0xFFF093FB),
            Color(0xFFF5576C)
        ),
        StatCard(
            Icons.Default.ReceiptLong,
            "订单总数",
            orderCount,
            Color(0xFF4FACFE),
            Color(0xFF00F2FE)
        ),
        StatCard(
            Icons.Default.Category,
            "分类总数",
            categoryCount,
            Color(0xFF43E97B),
            Color(0xFF38F9D7)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("数据概览", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A3A6B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "欢迎回来，管理员！",
                        fontSize = 16.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "以下是今日数据概览",
                        fontSize = 14.sp,
                        color = Color(0xFF999999)
                    )
                }

                items(4) { index ->
                    val stat = statCards[index]
                    StatCardItem(stat = stat)
                }
            }
        }
    }
}

@Composable
fun StatCardItem(stat: StatCard) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(stat.gradientStart, stat.gradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stat.value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1E4C)
                )
                Text(
                    text = stat.title,
                    fontSize = 13.sp,
                    color = Color(0xFF8C8C8C)
                )
            }
        }
    }
}
