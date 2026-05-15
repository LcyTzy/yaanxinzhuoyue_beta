package com.zhantu.autopartsmall.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.zhantu.autopartsmall.data.model.*
import com.zhantu.autopartsmall.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminVehicleScreen(
    onBack: () -> Unit = {},
    application: android.app.Application
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("品牌管理", "车系管理", "车型管理")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = { Text("车型管理", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
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

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 14.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> VehicleBrandTab(application = application)
            1 -> VehicleSeriesTab(application = application)
            2 -> VehicleModelTab(application = application)
        }
    }
}

@Composable
fun VehicleBrandTab(application: android.app.Application) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var brands by remember { mutableStateOf<List<VehicleBrand>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBrand by remember { mutableStateOf<VehicleBrand?>(null) }
    var brandName by remember { mutableStateOf("") }
    var brandInitial by remember { mutableStateOf("") }
    var brandSort by remember { mutableStateOf("0") }

    fun loadBrands() {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleBrands(
                        token = "Bearer $token"
                    )
                    if (response.code == 200 && response.data != null) {
                        brands = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载品牌失败", e)
                android.widget.Toast.makeText(context, "加载失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun saveBrand() {
        if (brandName.isEmpty()) {
            android.widget.Toast.makeText(context, "请输入品牌名称", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val brand = VehicleBrand(
                        id = editingBrand?.id ?: 0,
                        name = brandName,
                        initial = brandInitial,
                        sort = brandSort.toIntOrNull() ?: 0,
                        status = editingBrand?.status ?: 1
                    )
                    val response = if (editingBrand != null) {
                        RetrofitClient.apiService.updateVehicleBrand(
                            token = "Bearer $token",
                            id = editingBrand!!.id,
                            brand = brand
                        )
                    } else {
                        RetrofitClient.apiService.addVehicleBrand(
                            token = "Bearer $token",
                            brand = brand
                        )
                    }
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "操作成功", android.widget.Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        editingBrand = null
                        brandName = ""
                        brandInitial = ""
                        brandSort = "0"
                        loadBrands()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "保存品牌失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteBrand(id: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.deleteVehicleBrand(
                        token = "Bearer $token",
                        id = id
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "删除成功", android.widget.Toast.LENGTH_SHORT).show()
                        loadBrands()
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "删除失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "删除品牌失败", e)
                android.widget.Toast.makeText(context, "删除失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openEditDialog(brand: VehicleBrand) {
        editingBrand = brand
        brandName = brand.name
        brandInitial = brand.initial
        brandSort = brand.sort.toString()
        showAddDialog = true
    }

    LaunchedEffect(Unit) {
        loadBrands()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                editingBrand = null
                brandName = ""
                brandInitial = ""
                brandSort = "0"
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加品牌")
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
                if (brands.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无品牌数据",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(brands) { brand ->
                        VehicleBrandCard(
                            brand = brand,
                            onEdit = { openEditDialog(brand) },
                            onDelete = { deleteBrand(brand.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (editingBrand != null) "编辑品牌" else "添加品牌") },
            text = {
                Column {
                    OutlinedTextField(
                        value = brandName,
                        onValueChange = { brandName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("品牌名称") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = brandInitial,
                        onValueChange = { brandInitial = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("首字母") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = brandSort,
                        onValueChange = { brandSort = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("排序") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { saveBrand() }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun VehicleBrandCard(
    brand: VehicleBrand,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = brand.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "首字母: ${brand.initial} | 排序: ${brand.sort}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color(0xFF1A3A6B))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleSeriesTab(application: android.app.Application) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var brands by remember { mutableStateOf<List<VehicleBrand>>(emptyList()) }
    var seriesList by remember { mutableStateOf<List<VehicleSeries>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedBrandId by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSeries by remember { mutableStateOf<VehicleSeries?>(null) }
    var seriesName by remember { mutableStateOf("") }
    var seriesSort by remember { mutableStateOf("0") }

    fun loadBrands() {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleBrands(
                        token = "Bearer $token"
                    )
                    if (response.code == 200 && response.data != null) {
                        brands = response.data
                        if (brands.isNotEmpty() && selectedBrandId == null) {
                            selectedBrandId = brands[0].id
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载品牌失败", e)
            }
        }
    }

    fun loadSeries(brandId: Long?) {
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleSeries(
                        token = "Bearer $token",
                        brandId = brandId
                    )
                    if (response.code == 200 && response.data != null) {
                        seriesList = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载车系失败", e)
                android.widget.Toast.makeText(context, "加载失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun saveSeries() {
        if (seriesName.isEmpty()) {
            android.widget.Toast.makeText(context, "请输入车系名称", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedBrandId == null) {
            android.widget.Toast.makeText(context, "请先选择品牌", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val series = VehicleSeries(
                        id = editingSeries?.id ?: 0,
                        brandId = selectedBrandId!!,
                        name = seriesName,
                        sort = seriesSort.toIntOrNull() ?: 0,
                        status = editingSeries?.status ?: 1
                    )
                    val response = if (editingSeries != null) {
                        RetrofitClient.apiService.updateVehicleSeries(
                            token = "Bearer $token",
                            id = editingSeries!!.id,
                            series = series
                        )
                    } else {
                        RetrofitClient.apiService.addVehicleSeries(
                            token = "Bearer $token",
                            series = series
                        )
                    }
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "操作成功", android.widget.Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        editingSeries = null
                        seriesName = ""
                        seriesSort = "0"
                        loadSeries(selectedBrandId)
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "保存车系失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSeries(id: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.deleteVehicleSeries(
                        token = "Bearer $token",
                        id = id
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "删除成功", android.widget.Toast.LENGTH_SHORT).show()
                        loadSeries(selectedBrandId)
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "删除失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "删除车系失败", e)
                android.widget.Toast.makeText(context, "删除失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openEditDialog(series: VehicleSeries) {
        editingSeries = series
        seriesName = series.name
        seriesSort = series.sort.toString()
        showAddDialog = true
    }

    LaunchedEffect(Unit) {
        loadBrands()
    }

    LaunchedEffect(selectedBrandId) {
        if (selectedBrandId != null) {
            loadSeries(selectedBrandId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("品牌:", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                brands.forEach { brand ->
                    FilterChip(
                        selected = selectedBrandId == brand.id,
                        onClick = { selectedBrandId = brand.id },
                        label = { Text(brand.name, fontSize = 12.sp, maxLines = 1) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                if (selectedBrandId == null) {
                    android.widget.Toast.makeText(context, "请先选择品牌", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                editingSeries = null
                seriesName = ""
                seriesSort = "0"
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加车系")
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
                if (seriesList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无车系数据",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(seriesList) { series ->
                        VehicleSeriesCard(
                            series = series,
                            onEdit = { openEditDialog(series) },
                            onDelete = { deleteSeries(series.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (editingSeries != null) "编辑车系" else "添加车系") },
            text = {
                Column {
                    OutlinedTextField(
                        value = seriesName,
                        onValueChange = { seriesName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("车系名称") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = seriesSort,
                        onValueChange = { seriesSort = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("排序") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { saveSeries() }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun VehicleSeriesCard(
    series: VehicleSeries,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = series.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "排序: ${series.sort}",
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color(0xFF1A3A6B))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleModelTab(application: android.app.Application) {
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var brands by remember { mutableStateOf<List<VehicleBrand>>(emptyList()) }
    var seriesList by remember { mutableStateOf<List<VehicleSeries>>(emptyList()) }
    var models by remember { mutableStateOf<List<VehicleModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedBrandId by remember { mutableStateOf<Long?>(null) }
    var selectedSeriesId by remember { mutableStateOf<Long?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingModel by remember { mutableStateOf<VehicleModel?>(null) }
    var modelName by remember { mutableStateOf("") }
    var modelYear by remember { mutableStateOf("") }
    var modelDisplacement by remember { mutableStateOf("") }
    var modelEngine by remember { mutableStateOf("") }
    var modelTransmission by remember { mutableStateOf("") }
    var modelSort by remember { mutableStateOf("0") }

    fun loadBrands() {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleBrands(
                        token = "Bearer $token"
                    )
                    if (response.code == 200 && response.data != null) {
                        brands = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载品牌失败", e)
            }
        }
    }

    fun loadSeries(brandId: Long?) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleSeries(
                        token = "Bearer $token",
                        brandId = brandId
                    )
                    if (response.code == 200 && response.data != null) {
                        seriesList = response.data
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载车系失败", e)
            }
        }
    }

    fun loadModels(seriesId: Long?) {
        if (seriesId == null) {
            models = emptyList()
            isLoading = false
            return
        }
        isLoading = true
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.getAdminVehicleModels(
                        token = "Bearer $token",
                        seriesId = seriesId,
                        pageNum = 1,
                        pageSize = 100
                    )
                    if (response.code == 200 && response.data != null) {
                        models = response.data.records ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "加载车型失败", e)
                android.widget.Toast.makeText(context, "加载失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun saveModel() {
        if (modelName.isEmpty()) {
            android.widget.Toast.makeText(context, "请输入车型名称", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedSeriesId == null) {
            android.widget.Toast.makeText(context, "请先选择车系", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val model = VehicleModel(
                        id = editingModel?.id ?: 0,
                        seriesId = selectedSeriesId!!,
                        name = modelName,
                        year = modelYear.ifEmpty { null },
                        displacement = modelDisplacement.ifEmpty { null },
                        engine = modelEngine.ifEmpty { null },
                        transmission = modelTransmission.ifEmpty { null },
                        sort = modelSort.toIntOrNull() ?: 0,
                        status = editingModel?.status ?: 1
                    )
                    val response = if (editingModel != null) {
                        RetrofitClient.apiService.updateVehicleModel(
                            token = "Bearer $token",
                            id = editingModel!!.id,
                            model = model
                        )
                    } else {
                        RetrofitClient.apiService.addVehicleModel(
                            token = "Bearer $token",
                            model = model
                        )
                    }
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "操作成功", android.widget.Toast.LENGTH_SHORT).show()
                        showAddDialog = false
                        editingModel = null
                        modelName = ""
                        modelYear = ""
                        modelDisplacement = ""
                        modelEngine = ""
                        modelTransmission = ""
                        modelSort = "0"
                        loadModels(selectedSeriesId)
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "操作失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "保存车型失败", e)
                android.widget.Toast.makeText(context, "操作失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteModel(id: Long) {
        scope.launch {
            try {
                val dataStore = application.dataStore
                val preferences = dataStore.data.first()
                val token = preferences[com.zhantu.autopartsmall.data.local.PreferencesKeys.TOKEN_KEY]
                if (token != null) {
                    val response = RetrofitClient.apiService.deleteVehicleModel(
                        token = "Bearer $token",
                        id = id
                    )
                    if (response.code == 200) {
                        android.widget.Toast.makeText(context, "删除成功", android.widget.Toast.LENGTH_SHORT).show()
                        loadModels(selectedSeriesId)
                    } else {
                        android.widget.Toast.makeText(context, response.message ?: "删除失败", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AdminVehicle", "删除车型失败", e)
                android.widget.Toast.makeText(context, "删除失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openEditDialog(model: VehicleModel) {
        editingModel = model
        modelName = model.name
        modelYear = model.year ?: ""
        modelDisplacement = model.displacement ?: ""
        modelEngine = model.engine ?: ""
        modelTransmission = model.transmission ?: ""
        modelSort = model.sort.toString()
        showAddDialog = true
    }

    LaunchedEffect(Unit) {
        loadBrands()
    }

    LaunchedEffect(selectedBrandId) {
        if (selectedBrandId != null) {
            selectedSeriesId = null
            models = emptyList()
            loadSeries(selectedBrandId)
        }
    }

    LaunchedEffect(selectedSeriesId) {
        loadModels(selectedSeriesId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("品牌:", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                brands.forEach { brand ->
                    FilterChip(
                        selected = selectedBrandId == brand.id,
                        onClick = { selectedBrandId = brand.id },
                        label = { Text(brand.name, fontSize = 12.sp, maxLines = 1) }
                    )
                }
            }
        }

        if (seriesList.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("车系:", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(50.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    seriesList.forEach { series ->
                        FilterChip(
                            selected = selectedSeriesId == series.id,
                            onClick = { selectedSeriesId = series.id },
                            label = { Text(series.name, fontSize = 12.sp, maxLines = 1) }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                if (selectedSeriesId == null) {
                    android.widget.Toast.makeText(context, "请先选择车系", android.widget.Toast.LENGTH_SHORT).show()
                    return@Button
                }
                editingModel = null
                modelName = ""
                modelYear = ""
                modelDisplacement = ""
                modelEngine = ""
                modelTransmission = ""
                modelSort = "0"
                showAddDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加车型")
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
                if (models.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedSeriesId == null) "请先选择品牌和车系" else "暂无车型数据",
                                fontSize = 14.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                } else {
                    items(models) { model ->
                        VehicleModelCard(
                            model = model,
                            onEdit = { openEditDialog(model) },
                            onDelete = { deleteModel(model.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(if (editingModel != null) "编辑车型" else "添加车型") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = modelName,
                        onValueChange = { modelName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("车型名称") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelYear,
                        onValueChange = { modelYear = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("年款") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelDisplacement,
                        onValueChange = { modelDisplacement = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("排量") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelEngine,
                        onValueChange = { modelEngine = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("发动机") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelTransmission,
                        onValueChange = { modelTransmission = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("变速箱") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelSort,
                        onValueChange = { modelSort = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("排序") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = { saveModel() }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun VehicleModelCard(
    model: VehicleModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        model.year?.let {
                            Text(
                                text = "年款: $it",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        model.displacement?.let {
                            Text(
                                text = "排量: $it",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        model.engine?.let {
                            Text(
                                text = "发动机: $it",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                        model.transmission?.let {
                            Text(
                                text = "变速箱: $it",
                                fontSize = 12.sp,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑", tint = Color(0xFF1A3A6B))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color(0xFFE53935))
                    }
                }
            }
        }
    }
}
