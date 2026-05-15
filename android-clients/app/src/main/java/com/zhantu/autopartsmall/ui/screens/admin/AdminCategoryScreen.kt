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
import com.zhantu.autopartsmall.data.model.Category
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCategoryScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var categoryName by remember { mutableStateOf("") }
    var categorySort by remember { mutableStateOf("0") }
    var parentId by remember { mutableStateOf(0L) }

    fun loadCategories() {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminCategories("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        categories = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminCategory", "加载分类失败", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun addCategory() {
        if (categoryName.isEmpty()) {
            android.widget.Toast.makeText(context, "请输入分类名称", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val newCategory = Category(
                        id = 0,
                        name = categoryName,
                        parentId = parentId,
                        level = 1,
                        sort = categorySort.toIntOrNull() ?: 0,
                        icon = null,
                        status = 1
                    )
                    val response = RetrofitClient.apiService.addCategory(
                        token = "Bearer $token",
                        category = newCategory
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "添加成功", android.widget.Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        categoryName = ""
                        categorySort = "0"
                        parentId = 0L
                        loadCategories()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "添加失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminCategory", "添加分类失败", e)
                android.widget.Toast.makeText(context, "添加失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateCategory() {
        if (categoryName.isEmpty() || editingCategory == null) {
            android.widget.Toast.makeText(context, "请输入分类名称", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val updatedCategory = editingCategory!!.copy(
                        name = categoryName,
                        sort = categorySort.toIntOrNull() ?: 0
                    )
                    val response = RetrofitClient.apiService.updateCategory(
                        token = "Bearer $token",
                        category = updatedCategory
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "更新成功", android.widget.Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                        editingCategory = null
                        categoryName = ""
                        categorySort = "0"
                        loadCategories()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "更新失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminCategory", "更新分类失败", e)
                android.widget.Toast.makeText(context, "更新失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.deleteCategory(
                        token = "Bearer $token",
                        id = categoryId
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "删除成功", android.widget.Toast.LENGTH_SHORT).show()
                        loadCategories()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "删除失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminCategory", "删除分类失败", e)
                android.widget.Toast.makeText(context, "删除失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCategories()
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("添加分类") },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("分类名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = categorySort,
                        onValueChange = { categorySort = it },
                        label = { Text("排序") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { addCategory() }) {
                    Text("添加")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showEditDialog && editingCategory != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("编辑分类") },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("分类名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = categorySort,
                        onValueChange = { categorySort = it },
                        label = { Text("排序") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { updateCategory() }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "添加", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A3A6B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
                if (categories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无分类",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(categories) { category ->
                        AdminCategoryCard(
                            category = category,
                            onEditClick = {
                                editingCategory = category
                                categoryName = category.name
                                categorySort = category.sort?.toString() ?: "0"
                                showEditDialog = true
                            },
                            onDeleteClick = { deleteCategory(category.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCategoryCard(
    category: Category,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1A3A6B).copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = null,
                    tint = Color(0xFF1A3A6B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (category.parentId == 0L || category.parentId == null) "一级分类" else "二级分类",
                    fontSize = 12.sp,
                    color = Color(0xFF999999)
                )
            }
            Text(
                text = "排序: ${category.sort}",
                fontSize = 12.sp,
                color = Color(0xFF666666)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color(0xFF1A3A6B), modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFF44336), modifier = Modifier.size(20.dp))
            }
        }
    }
}
