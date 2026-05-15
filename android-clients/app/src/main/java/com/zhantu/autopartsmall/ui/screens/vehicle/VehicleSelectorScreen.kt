package com.zhantu.autopartsmall.ui.screens.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhantu.autopartsmall.data.model.VehicleBrand
import com.zhantu.autopartsmall.data.model.VehicleModel
import com.zhantu.autopartsmall.data.model.VehicleSeries
import com.zhantu.autopartsmall.data.network.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSelectorScreen(
    onVehicleSelected: (VehicleBrand, VehicleSeries, VehicleModel) -> Unit = { _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var brands by remember { mutableStateOf<List<VehicleBrand>>(emptyList()) }
    var series by remember { mutableStateOf<List<VehicleSeries>>(emptyList()) }
    var models by remember { mutableStateOf<List<VehicleModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedBrand by remember { mutableStateOf<VehicleBrand?>(null) }
    var selectedSeries by remember { mutableStateOf<VehicleSeries?>(null) }
    var selectedModel by remember { mutableStateOf<VehicleModel?>(null) }
    var brandsLoading by remember { mutableStateOf(false) }
    var seriesLoading by remember { mutableStateOf(false) }
    var modelsLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        brandsLoading = true
        try {
            val response = RetrofitClient.apiService.getVehicleBrands()
            if (response.code == 200 && response.data != null) {
                brands = response.data
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            brandsLoading = false
            isLoading = false
        }
    }

    fun loadSeries(brand: VehicleBrand) {
        selectedBrand = brand
        selectedSeries = null
        selectedModel = null
        seriesLoading = true
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getVehicleSeries(brand.id)
                if (response.code == 200 && response.data != null) {
                    series = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                seriesLoading = false
            }
        }
    }

    fun loadModels(seriesItem: VehicleSeries) {
        selectedSeries = seriesItem
        selectedModel = null
        modelsLoading = true
        scope.launch {
            try {
                val response = RetrofitClient.apiService.getVehicleModels(seriesItem.id)
                if (response.code == 200 && response.data != null) {
                    models = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                modelsLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Xuanze chexing") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Fan hui")
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "1. Xuanze pinpai",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                        if (brandsLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(brands) { brand ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { loadSeries(brand) },
                                        color = if (selectedBrand?.id == brand.id)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = brand.name,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "2. Xuanze chexi",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                        if (seriesLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (series.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Qing xuanze pinpai",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(series) { seriesItem ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { loadModels(seriesItem) },
                                        color = if (selectedSeries?.id == seriesItem.id)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = seriesItem.name,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "3. Xuanze chexing",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                        if (modelsLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else if (models.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Qing xuanze chexi",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(models) { model ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedModel = model
                                                selectedBrand?.let { b ->
                                                    selectedSeries?.let { s ->
                                                        onVehicleSelected(b, s, model)
                                                    }
                                                }
                                            },
                                        color = if (selectedModel?.id == model.id)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surface
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = model.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                model.year?.let {
                                                    Text(
                                                        text = "$it kuan",
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                model.displacement?.let {
                                                    Text(
                                                        text = it,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
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
    }
}
