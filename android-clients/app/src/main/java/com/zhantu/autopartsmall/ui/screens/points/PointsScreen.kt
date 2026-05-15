package com.zhantu.autopartsmall.ui.screens.points

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.data.network.PointsLog
import com.zhantu.autopartsmall.data.network.RetrofitClient
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var pointsLogs by remember { mutableStateOf<List<PointsLog>>(emptyList()) }
    var totalPoints by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getPointsLog("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        pointsLogs = response.data.records ?: emptyList()
                        totalPoints = pointsLogs.sumOf { it.points }
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
                title = { Text("我的积分") },
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
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6B35)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "当前积分",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$totalPoints",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "积分记录",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (pointsLogs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无积分记录",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(pointsLogs) { log ->
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
                                        text = log.description ?: "积分变动",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.createTime ?: "",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                    text = if (log.points > 0) "+${log.points}" else "${log.points}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.points > 0) Color(0xFFFF6B35) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
