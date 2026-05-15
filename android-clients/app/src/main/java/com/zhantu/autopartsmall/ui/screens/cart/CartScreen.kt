package com.zhantu.autopartsmall.ui.screens.cart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
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
import com.zhantu.autopartsmall.viewmodel.CartViewModel

@Composable
fun CartScreen(
    viewModel: CartViewModel,
    onCheckout: () -> Unit = {}
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedItems by viewModel.selectedItems.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "购物车是空的",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        isSelected = selectedItems.contains(item.id),
                        onToggleSelect = { viewModel.toggleSelectItem(item.id) },
                        onQuantityChange = { newQty ->
                            viewModel.updateQuantity(item.id, newQty)
                        },
                        onDelete = { viewModel.removeFromCart(item.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Surface(
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (selectedItems.size == cartItems.size) {
                                viewModel.clearSelection()
                            } else {
                                viewModel.selectAll()
                            }
                        }
                    ) {
                        Checkbox(
                            checked = selectedItems.size == cartItems.size && cartItems.isNotEmpty(),
                            onCheckedChange = {
                                if (it) viewModel.selectAll() else viewModel.clearSelection()
                            }
                        )
                        Text("全选")
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "合计: ¥${String.format("%.2f", viewModel.getTotalPrice())}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "已选 ${viewModel.getSelectedCount()} 件",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onCheckout,
                        enabled = viewModel.getSelectedCount() > 0
                    ) {
                        Text("结算")
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: com.zhantu.autopartsmall.data.model.CartItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onQuantityChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelect() }
            )

            AsyncImage(
                model = item.productImage?.split(",")?.firstOrNull()?.let {
                    if (it.startsWith("http")) it else "http://10.178.93.31:8081$it"
                },
                contentDescription = item.productName,
                modifier = Modifier
                    .size(80.dp)
                    .padding(start = 8.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = item.productName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¥${item.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (item.quantity > 1) onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "减少", modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${item.quantity}",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "增加", modifier = Modifier.size(16.dp))
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
