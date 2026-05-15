package com.zhantu.autopartsmall.ui.screens.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.zhantu.autopartsmall.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBack: () -> Unit = {},
    onOrderClick: (Long) -> Unit = {},
    viewModel: OrderViewModel,
    initialStatus: Int? = null
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedTab by remember { mutableStateOf<Int?>(initialStatus) }

    LaunchedEffect(selectedTab) {
        viewModel.loadOrders(status = selectedTab)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的订单") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = if (selectedTab == null) 0 else selectedTab!! + 1) {
                listOf("全部" to null, "待付款" to 0, "待发货" to 1, "待收货" to 2, "已完成" to 3).forEach { (label, status) ->
                    Tab(
                        selected = selectedTab == status,
                        onClick = { selectedTab = status },
                        text = { Text(label) }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "加载失败: $error",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无订单",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        OrderCard(order = order, onClick = { onOrderClick(order.id) })
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: com.zhantu.autopartsmall.data.model.Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "订单号: ${order.orderNo}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = getStatusText(order.status),
                    fontSize = 12.sp,
                    color = getStatusColor(order.status),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            order.items?.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.productImage?.split(",")?.firstOrNull()?.let {
                            if (it.startsWith("http")) it else "http://10.178.93.31:8081$it"
                        },
                        contentDescription = item.productName,
                        modifier = Modifier
                            .size(60.dp)
                            .padding(end = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.productName,
                            fontSize = 14.sp,
                            maxLines = 2
                        )
                        Text(
                            text = "¥${item.price} × ${item.quantity}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.createTime ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "实付款: ¥${order.payAmount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun getStatusText(status: Int): String {
    return when (status) {
        0 -> "待付款"
        1 -> "待发货"
        2 -> "待收货"
        3 -> "已完成"
        else -> "未知"
    }
}

fun getStatusColor(status: Int): androidx.compose.ui.graphics.Color {
    return when (status) {
        0 -> androidx.compose.ui.graphics.Color(0xFFFF6B6B)
        1 -> androidx.compose.ui.graphics.Color(0xFFFFB74D)
        2 -> androidx.compose.ui.graphics.Color(0xFF64B5F6)
        3 -> androidx.compose.ui.graphics.Color(0xFF81C784)
        else -> androidx.compose.ui.graphics.Color.Gray
    }
}
