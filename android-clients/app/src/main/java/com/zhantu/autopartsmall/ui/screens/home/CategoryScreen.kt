package com.zhantu.autopartsmall.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhantu.autopartsmall.data.model.Category
import com.zhantu.autopartsmall.data.model.Product
import com.zhantu.autopartsmall.data.network.RetrofitClient

@Composable
fun CategoryScreen(
    initialCategoryName: String = "",
    onProductClick: (Long) -> Unit = {},
    onNavigateToProductList: (String?) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubCategory by remember { mutableStateOf<Category?>(null) }
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var productsLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.getCategoryTree()
            if (response.code == 200 && response.data != null) {
                categories = response.data
                if (initialCategoryName.isNotEmpty()) {
                    selectedCategory = categories.find { it.name == initialCategoryName } ?: categories.firstOrNull()
                } else {
                    selectedCategory = categories.firstOrNull()
                }
                selectedSubCategory = selectedCategory?.children?.firstOrNull()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(selectedSubCategory) {
        selectedSubCategory?.let { subCat ->
            println("加载分类商品: categoryId=${subCat.id}, name=${subCat.name}")
            productsLoading = true
            try {
                val response = RetrofitClient.apiService.getProductList(1, 50, null, subCat.id)
                println("商品接口响应: code=${response.code}, data=${response.data}")
                if (response.code == 200 && response.data != null) {
                    products = response.data.records
                    println("商品数量: ${products.size}")
                } else {
                    println("加载失败: ${response.message}")
                }
            } catch (e: Exception) {
                println("加载异常: ${e.message}")
                e.printStackTrace()
            } finally {
                productsLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(100.dp)
            ) {
                items(categories) { category ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategory = category
                                selectedSubCategory = category.children?.firstOrNull()
                            },
                        color = if (selectedCategory?.id == category.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = category.name,
                            fontSize = 13.sp,
                            fontWeight = if (selectedCategory?.id == category.id) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selectedCategory?.children?.let { subCategories ->
                        items(subCategories) { subCategory ->
                            FilterChip(
                                selected = selectedSubCategory?.id == subCategory.id,
                                onClick = { selectedSubCategory = subCategory },
                                label = { Text(subCategory.name, fontSize = 12.sp) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (productsLoading) {
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
                        Text("暂无商品", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products) { product ->
                            ProductGridCard(
                                product = product,
                                onClick = { onProductClick(product.id) }
                            )
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
            .padding(4.dp)
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
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.name,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "¥${product.price}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
