package com.zhantu.autopartsmall.ui.screens.coupons

import android.widget.Toast
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
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.data.network.*
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponsScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var availableCoupons by remember { mutableStateOf<List<Coupon>>(emptyList()) }
    var myCoupons by remember { mutableStateOf<List<UserCoupon>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val couponResponse = RetrofitClient.apiService.getAvailableCoupons()
                if (couponResponse.code == 200 && couponResponse.data != null) {
                    availableCoupons = couponResponse.data
                }

                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val myCouponResponse = RetrofitClient.apiService.getMyCoupons("Bearer $token")
                    if (myCouponResponse.code == 200 && myCouponResponse.data != null) {
                        myCoupons = myCouponResponse.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    fun loadMyCoupons() {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val status = when (selectedTab) {
                        1 -> 0
                        2 -> 1
                        3 -> 2
                        else -> null
                    }
                    val response = RetrofitClient.apiService.getMyCoupons("Bearer $token", status = status)
                    if (response.code == 200 && response.data != null) {
                        myCoupons = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun receiveCoupon(couponId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.receiveCoupon(
                        "Bearer $token",
                        ReceiveCouponRequest(couponId)
                    )
                    if (response.code == 200) {
                        Toast.makeText(context, "\u9886\u53d6\u6210\u529f", Toast.LENGTH_SHORT).show()
                        val couponResponse = RetrofitClient.apiService.getAvailableCoupons()
                        if (couponResponse.code == 200 && couponResponse.data != null) {
                            availableCoupons = couponResponse.data
                        }
                        loadMyCoupons()
                    } else {
                        Toast.makeText(context, response.message ?: "\u9886\u53d6\u5931\u8d25", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "\u9886\u53d6\u5931\u8d25", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\u4f18\u60e0\u5238\u4e2d\u5fc3") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "\u8fd4\u56de")
                    }
                }
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
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "\u53ef\u9886\u53d6\u4f18\u60e0\u5238",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(availableCoupons) { coupon ->
                    CouponCard(coupon = coupon, onReceive = { receiveCoupon(coupon.id) })
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "\u6211\u7684\u4f18\u60e0\u5238",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("\u5168\u90e8", "\u672a\u4f7f\u7528", "\u5df2\u4f7f\u7528", "\u5df2\u8fc7\u671f").forEachIndexed { index, label ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = {
                                    selectedTab = index
                                    loadMyCoupons()
                                },
                                text = { Text(label, fontSize = 14.sp) }
                            )
                        }
                    }
                }

                if (myCoupons.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "\u6682\u65e0\u4f18\u60e0\u5238",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(myCoupons) { userCoupon ->
                        MyCouponCard(userCoupon = userCoupon)
                    }
                }
            }
        }
    }
}

@Composable
fun CouponCard(coupon: Coupon, onReceive: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coupon.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                coupon.minAmount?.let { amount ->
                    Text(
                        text = "\u6ee1\u00a5$amount\u53ef\u7528",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "\u5df2\u9886 ${coupon.receiveCount}/${coupon.totalCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                if (coupon.type == 1) {
                    coupon.discountAmount?.let {
                        Text(
                            text = "\u00a5$it",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    coupon.discountRate?.let {
                        Text(
                            text = "${(it * 10).toInt()}\u6298",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onReceive,
                    enabled = coupon.receiveCount < coupon.totalCount,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (coupon.receiveCount >= coupon.totalCount) "\u5df2\u9886\u5b8c" else "\u9886\u53d6",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MyCouponCard(userCoupon: UserCoupon) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (userCoupon.status != 0)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userCoupon.coupon?.name ?: "\u4f18\u60e0\u5238",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                userCoupon.coupon?.endTime?.let {
                    Text(
                        text = "\u6709\u6548\u671f\u81f3: ${it.split(" ").first()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (userCoupon.status) {
                    0 -> MaterialTheme.colorScheme.primaryContainer
                    1 -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.errorContainer
                }
            ) {
                Text(
                    text = when (userCoupon.status) {
                        0 -> "\u672a\u4f7f\u7528"
                        1 -> "\u5df2\u4f7f\u7528"
                        else -> "\u5df2\u8fc7\u671f"
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = when (userCoupon.status) {
                        0 -> MaterialTheme.colorScheme.onPrimaryContainer
                        1 -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }
        }
    }
}
