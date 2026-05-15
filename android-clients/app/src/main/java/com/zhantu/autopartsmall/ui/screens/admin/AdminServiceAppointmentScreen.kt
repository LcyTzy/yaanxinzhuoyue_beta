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
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceAppointmentScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var appointments by remember { mutableStateOf<List<ServiceOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showActionDialog by remember { mutableStateOf(false) }
    var actionAppointment by remember { mutableStateOf<ServiceOrder?>(null) }

    fun loadAppointments() {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminServiceAppointments("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        appointments = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminService", "加载预约失败", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun confirmAppointment(orderId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.confirmServiceOrder(
                        token = "Bearer $token",
                        orderId = orderId
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "已确认", android.widget.Toast.LENGTH_SHORT).show()
                        loadAppointments()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminService", "确认预约失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun completeAppointment(orderId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.completeServiceOrder(
                        token = "Bearer $token",
                        orderId = orderId
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "已完成", android.widget.Toast.LENGTH_SHORT).show()
                        loadAppointments()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminService", "完成预约失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun cancelAppointment(orderId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.cancelServiceOrder(
                        token = "Bearer $token",
                        orderId = orderId
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "已取消", android.widget.Toast.LENGTH_SHORT).show()
                        loadAppointments()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminService", "取消预约失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAppointments()
    }

    if (showActionDialog && actionAppointment != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("预约操作") },
            text = {
                Column {
                    Text("预约编号: ${actionAppointment?.orderNo ?: actionAppointment?.id}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("当前状态: ${actionAppointment?.status}")
                }
            },
            confirmButton = {
                Row {
                    if (actionAppointment?.status == "pending") {
                        TextButton(
                            onClick = {
                                actionAppointment?.id?.let { confirmAppointment(it) }
                                showActionDialog = false
                            }
                        ) {
                            Text("确认")
                        }
                    }
                    if (actionAppointment?.status == "confirmed") {
                        TextButton(
                            onClick = {
                                actionAppointment?.id?.let { completeAppointment(it) }
                                showActionDialog = false
                            }
                        ) {
                            Text("完成")
                        }
                    }
                    if (actionAppointment?.status != "completed" && actionAppointment?.status != "cancelled") {
                        TextButton(
                            onClick = {
                                actionAppointment?.id?.let { cancelAppointment(it) }
                                showActionDialog = false
                            }
                        ) {
                            Text("取消")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("服务预约", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (appointments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无预约",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(appointments) { appointment ->
                        AdminServiceAppointmentCard(
                            appointment = appointment,
                            onActionClick = {
                                actionAppointment = appointment
                                showActionDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminServiceAppointmentCard(
    appointment: ServiceOrder,
    onActionClick: () -> Unit = {}
) {
    val statusColor = when (appointment.status) {
        "pending" -> Color(0xFFFFB74D)
        "confirmed" -> Color(0xFF64B5F6)
        "completed" -> Color(0xFF81C784)
        "cancelled" -> Color(0xFF9E9E9E)
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "预约编号: ${appointment.orderNo ?: appointment.id}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = appointment.status ?: "未知",
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            appointment.appointmentTime?.let {
                Text(
                    text = "预约时间: $it",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            appointment.userName?.let {
                Text(
                    text = "联系人: $it",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            appointment.userPhone?.let {
                Text(
                    text = "联系电话: $it",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
            appointment.remark?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "备注: $it",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onActionClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "操作", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("操作")
                }
            }
        }
    }
}
