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
fun AdminUserScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var users by remember { mutableStateOf<List<UserInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showActionDialog by remember { mutableStateOf(false) }
    var actionUser by remember { mutableStateOf<UserInfo?>(null) }
    var actionType by remember { mutableStateOf("") }

    fun loadUsers() {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminUsers("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        users = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminUser", "加载用户失败", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUserStatus(userId: Long, status: Int) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.updateUserStatus(
                        token = "Bearer $token",
                        userId = userId,
                        params = mapOf("status" to status)
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "操作成功", android.widget.Toast.LENGTH_SHORT).show()
                        loadUsers()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminUser", "更新用户状态失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun resetPassword(userId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.resetUserPassword(
                        token = "Bearer $token",
                        userId = userId
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "密码已重置为 123456", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "重置失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminUser", "重置密码失败", e)
                android.widget.Toast.makeText(context, "重置失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadUsers()
    }

    if (showActionDialog && actionUser != null) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("用户操作") },
            text = {
                Column {
                    Text("用户: ${actionUser?.username ?: "未知"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("当前状态: ${if (actionUser?.status == 1) "正常" else "禁用"}")
                }
            },
            confirmButton = {
                Row {
                    TextButton(
                        onClick = {
                            actionUser?.let {
                                val newStatus = if (it.status == 1) 0 else 1
                                updateUserStatus(it.id, newStatus)
                            }
                            showActionDialog = false
                        }
                    ) {
                        Text(if (actionUser?.status == 1) "禁用" else "启用")
                    }
                    TextButton(
                        onClick = {
                            actionUser?.let { resetPassword(it.id) }
                            showActionDialog = false
                        }
                    ) {
                        Text("重置密码")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("用户管理", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
                if (users.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无用户",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(users) { user ->
                        AdminUserCard(
                            user = user,
                            onActionClick = {
                                actionUser = user
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
fun AdminUserCard(
    user: UserInfo,
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFF6B6B).copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nickname ?: user.username ?: user.phone ?: "未知用户",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user.phone ?: user.username ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
                Text(
                    text = if (user.status == 1) "正常" else "禁用",
                    fontSize = 12.sp,
                    color = if (user.status == 1) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
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
