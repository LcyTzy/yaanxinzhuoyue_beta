package com.zhantu.autopartsmall.ui.screens.vin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.network.MatchedPart
import com.zhantu.autopartsmall.data.network.RetrofitClient
import com.zhantu.autopartsmall.data.network.VehicleInfo
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

data class VinHistoryItem(
    val vin: String,
    val vehicleDesc: String,
    val timestamp: Long
) {
    val timeAgo: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < 60000 -> "刚刚"
                diff < 3600000 -> "${diff / 60000}分钟前"
                diff < 86400000 -> "${diff / 3600000}小时前"
                else -> SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(timestamp))
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinQueryScreen(
    onProductClick: (Long) -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var vin by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var vehicleInfo by remember { mutableStateOf<VehicleInfo?>(null) }
    var categories by remember { mutableStateOf<Map<String, List<MatchedPart>>>(emptyMap()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var queryHistory by remember { mutableStateOf<List<VinHistoryItem>>(emptyList()) }
    var showResult by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        queryHistory = loadHistory(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text("VIN查配件", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF1A3A6B),
                titleContentColor = Color.White
            ),
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White
                        )
                    }
                }
            }
        )

        if (!showResult) {
            VinQueryInput(
                vin = vin,
                onVinChange = { vin = it },
                isLoading = isLoading,
                errorMessage = errorMessage,
                history = queryHistory,
                onQuery = {
                    if (vin.length != 17) {
                        errorMessage = "VIN码必须为17位"
                        return@VinQueryInput
                    }
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.decodeVin(vin)
                            if (response.code == 200) {
                                vehicleInfo = response.vehicle
                                if (!response.categories.isNullOrEmpty()) {
                                    categories = response.categories
                                } else if (!response.matchedParts.isNullOrEmpty()) {
                                    categories = mapOf("全部配件" to response.matchedParts)
                                }
                                selectedCategory = categories.keys.firstOrNull()
                                showResult = true
                                saveHistory(context, vin, response.vehicle)
                                queryHistory = loadHistory(context)
                            } else {
                                errorMessage = response.message ?: "查询失败"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "网络异常"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onHistoryClick = { item ->
                    vin = item.vin
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val response = RetrofitClient.apiService.decodeVin(item.vin)
                            if (response.code == 200) {
                                vehicleInfo = response.vehicle
                                if (!response.categories.isNullOrEmpty()) {
                                    categories = response.categories
                                } else if (!response.matchedParts.isNullOrEmpty()) {
                                    categories = mapOf("全部配件" to response.matchedParts)
                                }
                                selectedCategory = categories.keys.firstOrNull()
                                showResult = true
                            } else {
                                errorMessage = response.message ?: "查询失败"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "网络异常"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                onDeleteHistory = {
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[PreferencesKeys.VIN_HISTORY_KEY] = ""
                        }
                        queryHistory = emptyList()
                    }
                }
            )
        } else {
            VinQueryResult(
                vehicleInfo = vehicleInfo,
                vin = vin,
                categories = categories,
                selectedCategory = selectedCategory,
                onCategorySelect = { selectedCategory = it },
                onProductClick = onProductClick,
                onBack = {
                    showResult = false
                    vehicleInfo = null
                    categories = emptyMap()
                    selectedCategory = null
                }
            )
        }
    }
}

@Composable
fun VinQueryInput(
    vin: String,
    onVinChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    history: List<VinHistoryItem>,
    onQuery: () -> Unit,
    onHistoryClick: (VinHistoryItem) -> Unit,
    onDeleteHistory: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = vin,
                        onValueChange = { if (it.length <= 17) onVinChange(it.uppercase()) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("这里输入17位车架号", fontSize = 14.sp) },
                        singleLine = true,
                        maxLines = 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A3A6B),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onQuery,
                        enabled = vin.length == 17 && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1A3A6B),
                            disabledContainerColor = Color(0xFF1A3A6B).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("查询", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        if (history.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "查询历史",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3A6B)
                    )
                    TextButton(onClick = onDeleteHistory) {
                        Text("清空历史", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                history.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { onHistoryClick(item) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.vin,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = item.vehicleDesc,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = item.timeAgo,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VinQueryResult(
    vehicleInfo: VehicleInfo?,
    vin: String,
    categories: Map<String, List<MatchedPart>>,
    selectedCategory: String?,
    onCategorySelect: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val categoryList = categories.keys.toList()
    val categoryState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A3A6B))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vehicleInfo?.let { "${it.brand ?: ""} ${it.model ?: ""} ${it.year ?: ""}" } ?: "",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vin,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "复制",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .size(16.dp)
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("VIN", vin)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "VIN码已复制", Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }
            TextButton(onClick = onBack) {
                Text("切换", fontSize = 12.sp, color = Color.White)
            }
        }

        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF5F5F5))
            ) {
                LazyColumn(state = categoryState) {
                    items(categoryList) { category ->
                        val isSelected = category == selectedCategory
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategorySelect(category) }
                                .background(
                                    if (isSelected) Color.White else Color(0xFFF5F5F5)
                                )
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF1A3A6B) else Color(0xFF666666),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(2.dp)
                                            .background(Color(0xFF1A3A6B))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val parts = selectedCategory?.let { categories[it] } ?: emptyList()
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(parts) { part ->
                    VinPartCard(part = part, onProductClick = onProductClick)
                }
            }
        }
    }
}

@Composable
fun VinPartCard(part: MatchedPart, onProductClick: (Long) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFEEEEEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = part.name ?: "未知配件",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        part.oeNumber?.let {
                            Text(
                                text = "OE: $it",
                                fontSize = 12.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }
                Row {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "复制",
                        tint = Color(0xFF999999),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                part.oeNumber?.let { oe ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("OE号", oe)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "OE号已复制", Toast.LENGTH_SHORT).show()
                                }
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color(0xFF999999),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            part.price?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "战途 ${part.oeNumber ?: ""}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A3A6B)
                    )
                    Text(
                        text = "¥$it",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF6600)
                    )
                }
            }

            if (part.compatibleInfo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "适配车型",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = part.compatibleInfo,
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0))
                        .padding(8.dp)
                )
            }
        }
    }
}

private suspend fun loadHistory(context: Context): List<VinHistoryItem> {
    return try {
        val json = context.dataStore.data.first()[PreferencesKeys.VIN_HISTORY_KEY] ?: ""
        if (json.isNotEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<VinHistoryItem>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}

private suspend fun saveHistory(context: Context, vin: String, vehicle: VehicleInfo?) {
    try {
        val history = loadHistory(context).toMutableList()
        val desc = vehicle?.let { "${it.brand ?: ""} ${it.model ?: ""} ${it.year ?: ""}" } ?: "未知车型"
        history.removeAll { it.vin == vin }
        history.add(0, VinHistoryItem(vin, desc, System.currentTimeMillis()))
        if (history.size > 10) {
            history.subList(10, history.size).clear()
        }
        val gson = Gson()
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIN_HISTORY_KEY] = gson.toJson(history)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
