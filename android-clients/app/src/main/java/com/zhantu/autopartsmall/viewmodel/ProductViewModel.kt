package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.data.model.Product
import com.zhantu.autopartsmall.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _hotProducts = MutableStateFlow<List<Product>>(emptyList())
    val hotProducts: StateFlow<List<Product>> = _hotProducts

    private val _productDetail = MutableStateFlow<Product?>(null)
    val productDetail: StateFlow<Product?> = _productDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProducts(keyword: String? = null, categoryId: Long? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getProductList(1, 20, keyword, categoryId)
                if (response.code == 200 && response.data != null) {
                    _products.value = response.data.records
                } else {
                    _error.value = response.message.ifEmpty { "加载失败" }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadHotProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getHotProducts(1, 10)
                if (response.code == 200 && response.data != null) {
                    _hotProducts.value = response.data.records
                } else {
                    _error.value = response.message.ifEmpty { "加载失败" }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.apiService.getProductDetail(productId)
                if (response.code == 200 && response.data != null) {
                    _productDetail.value = response.data.product
                } else {
                    _error.value = response.message.ifEmpty { "加载失败" }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
