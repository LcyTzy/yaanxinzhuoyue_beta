package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.data.network.*
import com.zhantu.autopartsmall.data.model.*
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var searchKeyword by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var currentPage by remember { mutableStateOf(1) }
    var totalCount by remember { mutableStateOf(0) }
    var hasMore by remember { mutableStateOf(true) }

    fun loadCategories() {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminCategories(
                        token = "Bearer $token"
                    )
                    if (response.code == 200 && response.data != null) {
                        categories = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminProduct", "加载分类失败", e)
            }
        }
    }

    fun loadProducts(page: Int = 1, keyword: String = "", categoryId: Long? = selectedCategoryId, append: Boolean = false) {
        if (append) {
            isLoadingMore = true
        } else {
            isLoading = true
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminProducts(
                        token = "Bearer $token",
                        pageNum = page,
                        pageSize = 20,
                        keyword = keyword.ifEmpty { null },
                        categoryId = categoryId
                    )
                    if (response.code == 200 && response.data != null) {
                        val newProducts = response.data.records ?: emptyList()
                        if (append) {
                            products = products + newProducts
                        } else {
                            products = newProducts
                        }
                        totalCount = response.data.total?.toInt() ?: 0
                        currentPage = page
                        hasMore = products.size < totalCount
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminProduct", "加载商品失败", e)
            } finally {
                isLoading = false
                isLoadingMore = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCategories()
        loadProducts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商品管理", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("搜索商品名称/OE号") },
                trailingIcon = {
                    IconButton(onClick = { loadProducts(1, searchKeyword) }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            if (categories.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = {
                            selectedCategoryId = null
                            loadProducts(1, searchKeyword, null)
                        },
                        label = { Text("全部", fontSize = 12.sp) }
                    )
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = {
                                selectedCategoryId = category.id
                                loadProducts(1, searchKeyword, category.id)
                            },
                            label = { Text(category.name, fontSize = 12.sp, maxLines = 1) }
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "共 $totalCount 个商品",
                            fontSize = 12.sp,
                            color = Color(0xFF999999),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(products) { product ->
                        AdminProductCard(product = product)
                    }

                    if (hasMore) {
                        item {
                            Button(
                                onClick = { loadProducts(currentPage + 1, searchKeyword, selectedCategoryId, append = true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                shape = RoundedCornerShape(8.dp),
                                enabled = !isLoadingMore
                            ) {
                                if (isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("加载更多")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name ?: "未知商品",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    product.oem?.let {
                        Text(
                            text = "OE: $it",
                            fontSize = 12.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }
                Text(
                    text = "¥${product.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6600)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "库存: ${product.stock}",
                    fontSize = 12.sp,
                    color = if ((product.stock ?: 0) > 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Text(
                    text = product.brand ?: "",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}
