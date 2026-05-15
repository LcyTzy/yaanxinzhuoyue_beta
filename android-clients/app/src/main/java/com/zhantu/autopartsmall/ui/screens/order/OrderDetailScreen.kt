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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import coil.compose.AsyncImage
import com.zhantu.autopartsmall.data.model.Order
import com.zhantu.autopartsmall.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBack: () -> Unit = {},
    viewModel: OrderViewModel
) {
    val context = LocalContext.current
    var order by remember { mutableStateOf<Order?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(orderId) {
        viewModel.getOrderDetail(
            orderId = orderId,
            onSuccess = { order = it },
            onError = { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("订单详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        bottomBar = {
            if (order != null && (order!!.status == 0 || order!!.status == 2)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (order!!.status == 0) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.cancelOrder(
                                        orderId = order!!.id,
                                        onSuccess = {
                                            Toast.makeText(context, "订单已取消", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        },
                                        onError = { errorMsg ->
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            ) {
                                Text("取消订单")
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    viewModel.payOrder(
                                        orderId = order!!.id,
                                        onSuccess = {
                                            Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        },
                                        onError = { errorMsg ->
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            ) {
                                Text("去付款")
                            }
                        } else if (order!!.status == 2) {
                            Button(
                                onClick = {
                                    viewModel.confirmReceive(
                                        orderId = order!!.id,
                                        onSuccess = {
                                            Toast.makeText(context, "确认收货成功", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        },
                                        onError = { errorMsg ->
                                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            ) {
                                Text("确认收货")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (order != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OrderStatusCard(order = order!!)
                }

                item {
                    OrderInfoCard(order = order!!)
                }

                item {
                    OrderItemsCard(order = order!!)
                }

                item {
                    AddressCard(order = order!!)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun OrderStatusCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getStatusText(order.status),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getStatusColor(order.status)
                )
                Text(
                    text = order.createTime ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun OrderInfoCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "订单信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OrderInfoRow(label = "订单号", value = order.orderNo)
            OrderInfoRow(label = "订单金额", value = "¥${order.totalAmount}")
            OrderInfoRow(label = "实付金额", value = "¥${order.payAmount}")
            if (order.payTime != null) {
                OrderInfoRow(label = "支付时间", value = order.payTime)
            }
            if (order.logisticsCompany != null) {
                OrderInfoRow(label = "物流公司", value = order.logisticsCompany)
            }
            if (order.logisticsNo != null) {
                OrderInfoRow(label = "物流单号", value = order.logisticsNo)
            }
        }
    }
}

@Composable
fun OrderInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OrderItemsCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "商品信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
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
                    Text(
                        text = "¥${item.totalPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AddressCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "收货信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${order.receiverName}  ${order.receiverPhone}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = order.receiverAddress ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
