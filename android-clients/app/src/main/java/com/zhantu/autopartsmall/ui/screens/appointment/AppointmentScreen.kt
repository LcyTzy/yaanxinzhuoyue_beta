package com.zhantu.autopartsmall.ui.screens.appointment

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun AppointmentScreen(
    onBack: () -> Unit = {},
    onNavigateToMyAppointments: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var serviceTypes by remember { mutableStateOf<List<ServiceType>>(emptyList()) }
    var selectedType by remember { mutableStateOf<ServiceType?>(null) }
    var showTypePicker by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var vehicleBrand by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleYear by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var appointmentTime by remember { mutableStateOf("") }
    var remark by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getServiceTypes()
                if (response.code == 200 && response.data != null) {
                    serviceTypes = response.data
                }
            } catch (e: Exception) {
            }
        }
    }

    fun submitAppointment() {
        if (userName.isEmpty()) {
            Toast.makeText(context, "请输入姓名", Toast.LENGTH_SHORT).show()
            return
        }
        if (userPhone.isEmpty()) {
            Toast.makeText(context, "请输入电话", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedType == null) {
            Toast.makeText(context, "请选择服务类型", Toast.LENGTH_SHORT).show()
            return
        }
        if (appointmentTime.isEmpty()) {
            Toast.makeText(context, "请选择预约时间", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                val userId = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.USER_ID_KEY]
                if (token != null && userId != null) {
                    val request = CreateServiceOrderRequest(
                        userId = userId,
                        userName = userName,
                        userPhone = userPhone,
                        serviceType = selectedType!!.name,
                        vehicleBrand = vehicleBrand.ifEmpty { null },
                        vehicleModel = vehicleModel.ifEmpty { null },
                        vehicleYear = vehicleYear.ifEmpty { null },
                        licensePlate = licensePlate.ifEmpty { null },
                        appointmentTime = appointmentTime,
                        remark = remark.ifEmpty { null }
                    )
                    val response = RetrofitClient.apiService.createServiceOrder("Bearer $token", request)
                    if (response.code == 200) {
                        Toast.makeText(context, "预约成功", Toast.LENGTH_SHORT).show()
                        onNavigateToMyAppointments()
                    } else {
                        Toast.makeText(context, response.message ?: "预约失败", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "预约失败", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预约修车") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "服务类型",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTypePicker = true }
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = selectedType?.name ?: "请选择服务类型",
                                fontSize = 14.sp,
                                color = if (selectedType == null) Color.Gray else Color.Black
                            )
                        }
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "联系信息",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            label = { Text("姓名") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userPhone,
                            onValueChange = { userPhone = it },
                            label = { Text("电话") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "车辆信息（选填）",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vehicleBrand,
                            onValueChange = { vehicleBrand = it },
                            label = { Text("品牌") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vehicleModel,
                            onValueChange = { vehicleModel = it },
                            label = { Text("车型") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = vehicleYear,
                            onValueChange = { vehicleYear = it },
                            label = { Text("年款") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = licensePlate,
                            onValueChange = { licensePlate = it },
                            label = { Text("车牌号") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "预约时间",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = appointmentTime,
                            onValueChange = { appointmentTime = it },
                            label = { Text("预约时间（格式：yyyy-MM-dd HH:mm）") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = remark,
                            onValueChange = { remark = it },
                            label = { Text("备注") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { submitAppointment() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("提交预约", fontSize = 16.sp)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showTypePicker) {
            AlertDialog(
                onDismissRequest = { showTypePicker = false },
                title = { Text("选择服务类型") },
                text = {
                    LazyColumn {
                        items(serviceTypes) { type ->
                            Text(
                                text = "${type.name} - ¥${type.price}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedType = type
                                        showTypePicker = false
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showTypePicker = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppointmentsScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var appointments by remember { mutableStateOf<List<ServiceOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                val userId = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.USER_ID_KEY]
                if (token != null && userId != null) {
                    val response = RetrofitClient.apiService.getMyServiceOrders("Bearer $token", userId)
                    if (response.code == 200 && response.data != null) {
                        appointments = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的预约") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
                if (appointments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无预约记录",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(appointments) { appointment ->
                        AppointmentCard(appointment = appointment)
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: ServiceOrder) {
    val statusColor = when (appointment.status) {
        "pending" -> Color(0xFFFFB74D)
        "confirmed" -> Color(0xFF64B5F6)
        "completed" -> Color(0xFF81C784)
        "cancelled" -> Color.Gray
        else -> Color.Gray
    }

    val statusText = when (appointment.status) {
        "pending" -> "待确认"
        "confirmed" -> "已确认"
        "completed" -> "已完成"
        "cancelled" -> "已取消"
        else -> "未知"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = appointment.serviceType ?: "服务预约",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "预约时间: ${appointment.appointmentTime}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "联系人: ${appointment.userName} ${appointment.userPhone}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (appointment.vehicleBrand != null || appointment.licensePlate != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val vehicleInfo = listOfNotNull(
                        appointment.vehicleBrand,
                        appointment.vehicleModel,
                        appointment.vehicleYear,
                        appointment.licensePlate?.let { "车牌: $it" }
                    ).joinToString(" ")
                    Text(
                        text = "车辆: $vehicleInfo",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (appointment.remark != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "备注: ${appointment.remark}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
