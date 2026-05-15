package com.zhantu.autopartsmall.ui.screens.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.data.model.Category
import com.zhantu.autopartsmall.data.model.Product
import com.zhantu.autopartsmall.data.network.RetrofitClient
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    initialCategoryId: Long? = null,
    initialKeyword: String? = null,
    onProductClick: (Long) -> Unit = {},
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var productsLoading by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategory by remember { mutableStateOf<Category?>(null) }
    var searchKeyword by remember { mutableStateOf(initialKeyword ?: "") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("default") }
    var pageNum by remember { mutableIntStateOf(1) }
    var hasMore by remember { mutableStateOf(true) }
    val pageSize = 20

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getCategoryTree()
            if (response.code == 200 && response.data != null) {
                categories = response.data
                if (initialCategoryId != null) {
                    val cat = categories.find { it.id == initialCategoryId }
                    selectedCategory = cat
                    selectedSubCategory = cat?.children?.firstOrNull()
                } else {
                    selectedCategory = categories.firstOrNull()
                    selectedSubCategory = selectedCategory?.children?.firstOrNull()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    val scope = rememberCoroutineScope()

    fun loadProducts(page: Int, append: Boolean = false) {
        if (page > 1 && !hasMore) return
        productsLoading = true
        scope.launch {
            try {
                val sortParam = when (sortBy) {
                    "price-asc" -> "price" to "asc"
                    "price-desc" -> "price" to "desc"
                    "sales" -> "sales" to "desc"
                    else -> null to null
                }
                val response = RetrofitClient.apiService.getProductList(
                    current = page,
                    size = pageSize,
                    keyword = searchKeyword.takeIf { it.isNotBlank() },
                    categoryId = selectedSubCategory?.id ?: selectedCategory?.id,
                    sortBy = sortParam.first,
                    sortOrder = sortParam.second
                )
                if (response.code == 200 && response.data != null) {
                    val newProducts = response.data.records
                    if (append) {
                        products = products + newProducts
                    } else {
                        products = newProducts
                    }
                    hasMore = newProducts.size >= pageSize
                    pageNum = page
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                productsLoading = false
            }
        }
    }

    LaunchedEffect(selectedSubCategory, sortBy) {
        loadProducts(1, append = false)
    }

    fun handleSearch() {
        loadProducts(1, append = false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchKeyword,
                            onValueChange = { searchKeyword = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            placeholder = { Text("\u641c\u7d22\u5546\u54c1/OEM\u53f7/\u54c1\u724c", fontSize = 14.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { handleSearch() }),
                            shape = MaterialTheme.shapes.medium,
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                        )
                        IconButton(onClick = { handleSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "\u641c\u7d22")
                        }
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.Tune, contentDescription = "\u7b5b\u9009")
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "\u8fd4\u56de")
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
            Column(modifier = Modifier.padding(paddingValues)) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubCategory == null,
                            onClick = {
                                selectedSubCategory = null
                            },
                            label = { Text("\u5168\u90e8", fontSize = 12.sp) }
                        )
                    }
                    selectedCategory?.children?.let { subCategories ->
                        items(subCategories) { subCategory ->
                            FilterChip(
                                selected = selectedSubCategory?.id == subCategory.id,
                                onClick = { selectedSubCategory = subCategory },
                                label = { Text(subCategory.name, fontSize = 12.sp) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "default" to "\u9ed8\u8ba4",
                        "price-asc" to "\u4ef7\u683c\u2191",
                        "price-desc" to "\u4ef7\u683c\u2193",
                        "sales" to "\u9500\u91cf"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = sortBy == value,
                            onClick = { sortBy = value },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (productsLoading && products.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (products.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "\u6682\u65e0\u5546\u54c1",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(products) { product ->
                            ProductGridCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
                        }
                        if (hasMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (productsLoading) {
                                        CircularProgressIndicator()
                                    } else {
                                        TextButton(onClick = { loadProducts(pageNum + 1, append = true) }) {
                                            Text("\u52a0\u8f7d\u66f4\u591a")
                                        }
                                    }
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
fun ProductGridCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = product.getImageUrl(),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.brand ?: "",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "\u00a5${product.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "\u5e93\u5b58 ${product.stock}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
