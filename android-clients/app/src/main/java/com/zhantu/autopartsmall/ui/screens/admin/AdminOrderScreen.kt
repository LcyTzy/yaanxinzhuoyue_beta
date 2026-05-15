package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.data.network.*
import com.zhantu.autopartsmall.data.model.Order
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedStatus by remember { mutableStateOf<Int?>(null) }
    var showShipDialog by remember { mutableStateOf(false) }
    var shippingOrderId by remember { mutableStateOf<Long?>(null) }
    var expressCompany by remember { mutableStateOf("") }
    var expressNo by remember { mutableStateOf("") }

    val statusTabs = listOf(
        null to "全部",
        0 to "待付款",
        1 to "待发货",
        2 to "待收货",
        3 to "已完成"
    )

    fun loadOrders(status: Int? = null) {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminOrders(
                        token = "Bearer $token",
                        status = status,
                        pageNum = 1,
                        pageSize = 50
                    )
                    if (response.code == 200 && response.data != null) {
                        orders = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminOrder", "加载订单失败", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun shipOrder(orderId: Long) {
        if (expressCompany.isEmpty() || expressNo.isEmpty()) {
            android.widget.Toast.makeText(context, "请填写物流公司和运单号", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.adminShipOrder(
                        token = "Bearer $token",
                        orderId = orderId,
                        expressCompany = expressCompany,
                        expressNo = expressNo
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "发货成功", android.widget.Toast.LENGTH_SHORT).show()
                        showShipDialog = false
                        expressCompany = ""
                        expressNo = ""
                        loadOrders(selectedStatus)
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "发货失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminOrder", "发货失败", e)
                android.widget.Toast.makeText(context, "发货失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadOrders()
    }

    if (showShipDialog && shippingOrderId != null) {
        AlertDialog(
            onDismissRequest = { showShipDialog = false },
            title = { Text("发货") },
            text = {
                Column {
                    OutlinedTextField(
                        value = expressCompany,
                        onValueChange = { expressCompany = it },
                        label = { Text("物流公司") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = expressNo,
                        onValueChange = { expressNo = it },
                        label = { Text("运单号") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { shippingOrderId?.let { shipOrder(it) } }) {
                    Text("确认发货")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShipDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单管理", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            ScrollableTabRow(
                selectedTabIndex = statusTabs.indexOfFirst { it.first == selectedStatus }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp
            ) {
                statusTabs.forEach { (status, label) ->
                    Tab(
                        selected = selectedStatus == status,
                        onClick = {
                            selectedStatus = status
                            loadOrders(status)
                        },
                        text = { Text(label, fontSize = 14.sp) }
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (orders.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "暂无订单",
                                    fontSize = 14.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                        }
                    } else {
                        items(orders) { order ->
                            AdminOrderCard(
                                order = order,
                                onShipClick = {
                                    shippingOrderId = order.id
                                    showShipDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onShipClick: () -> Unit = {}
) {
    val statusColor = when (order.status) {
        0 -> Color(0xFFFFB74D)
        1 -> Color(0xFF64B5F6)
        2 -> Color(0xFF81C784)
        3 -> Color(0xFF9E9E9E)
        else -> Color.Gray
    }

    val statusText = when (order.status) {
        0 -> "待付款"
        1 -> "待发货"
        2 -> "待收货"
        3 -> "已完成"
        else -> "未知"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "订单号: ${order.orderNo}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "总金额: ¥${order.totalAmount}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6600)
            )

            Spacer(modifier = Modifier.height(4.dp))

            order.createTime?.let {
                Text(
                    text = "下单时间: $it",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }

            order.receiverName?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "收货人: $it ${order.receiverPhone ?: ""}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            if (order.status == 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onShipClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3A6B))
                ) {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("发货")
                }
            }
        }
    }
}
