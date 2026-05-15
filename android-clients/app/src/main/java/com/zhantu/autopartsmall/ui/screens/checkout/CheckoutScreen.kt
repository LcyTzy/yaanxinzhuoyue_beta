package com.zhantu.autopartsmall.ui.screens.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import com.zhantu.autopartsmall.data.model.CartItem
import com.zhantu.autopartsmall.data.network.Address
import com.zhantu.autopartsmall.viewmodel.AddressViewModel
import com.zhantu.autopartsmall.viewmodel.CartViewModel
import com.zhantu.autopartsmall.viewmodel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onBack: () -> Unit = {},
    onOrderSuccess: () -> Unit = {},
    cartViewModel: CartViewModel,
    addressViewModel: AddressViewModel,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.collectAsState()
    val selectedItems by cartViewModel.selectedItems.collectAsState()
    val addresses by addressViewModel.addresses.collectAsState()
    var selectedAddress by remember { mutableStateOf<Address?>(null) }
    var remark by remember { mutableStateOf("") }
    var showAddressDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        addressViewModel.loadAddresses()
        if (addresses.isNotEmpty()) {
            selectedAddress = addresses.find { it.isDefault == 1 } ?: addresses.first()
        }
    }

    val selectedCartItems = cartItems.filter { selectedItems.contains(it.id) }
    val totalPrice = selectedCartItems.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("确认订单") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showAddressDialog = true }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "收货地址",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            if (selectedAddress != null) {
                                Text(
                                    text = "${selectedAddress!!.receiverName}  ${selectedAddress!!.receiverPhone}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = buildString {
                                        selectedAddress!!.province?.let { append(it) }
                                        selectedAddress!!.city?.let { append(it) }
                                        selectedAddress!!.district?.let { append(it) }
                                        append(selectedAddress!!.detail)
                                    },
                                    fontSize = 14.sp
                                )
                            } else {
                                Text(
                                    text = "请选择收货地址",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "商品信息",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            selectedCartItems.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${item.productName} x${item.quantity}",
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "¥${item.price * item.quantity}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "订单备注",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = remark,
                                onValueChange = { remark = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("选填") },
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "合计:",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "¥${String.format("%.2f", totalPrice)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = {
                            if (selectedAddress == null) {
                                return@Button
                            }
                            isLoading = true
                            val addr = selectedAddress!!
                            orderViewModel.createOrder(
                                cartIds = selectedCartItems.map { it.id },
                                receiverName = addr.receiverName,
                                receiverPhone = addr.receiverPhone,
                                receiverAddress = buildString {
                                    addr.province?.let { append(it) }
                                    addr.city?.let { append(it) }
                                    addr.district?.let { append(it) }
                                    append(addr.detail)
                                },
                                remark = remark.ifBlank { null },
                                onSuccess = {
                                    cartViewModel.clearCart()
                                    isLoading = false
                                    onOrderSuccess()
                                },
                                onError = { errorMsg ->
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        errorMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        },
                        enabled = selectedAddress != null && selectedCartItems.isNotEmpty() && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("提交订单")
                        }
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressSelectionDialog(
            addresses = addresses,
            selectedAddress = selectedAddress,
            onAddressSelected = { addr ->
                selectedAddress = addr
                showAddressDialog = false
            },
            onDismiss = { showAddressDialog = false }
        )
    }
}

@Composable
fun AddressSelectionDialog(
    addresses: List<Address>,
    selectedAddress: Address?,
    onAddressSelected: (Address) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择收货地址") },
        text = {
            LazyColumn {
                items(addresses) { address ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAddress?.id == address.id,
                            onClick = { onAddressSelected(address) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${address.receiverName}  ${address.receiverPhone}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = buildString {
                                    address.province?.let { append(it) }
                                    address.city?.let { append(it) }
                                    address.district?.let { append(it) }
                                    append(address.detail)
                                },
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}
