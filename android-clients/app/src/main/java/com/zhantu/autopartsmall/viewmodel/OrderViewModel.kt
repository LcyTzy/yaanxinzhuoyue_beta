package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.model.Order
import com.zhantu.autopartsmall.data.network.RetrofitClient
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadOrders(status: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.getOrderList(
                        token = "Bearer $token",
                        current = 1,
                        size = 100,
                        status = status
                    )
                    if (response.code == 200 && response.data != null) {
                        _orders.value = response.data.records ?: emptyList()
                    } else {
                        _error.value = response.message.ifEmpty { "加载失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createOrder(
        cartIds: List<Long>,
        receiverName: String,
        receiverPhone: String,
        receiverAddress: String,
        remark: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val request = com.zhantu.autopartsmall.data.model.CreateOrderRequest(
                        cartIds = cartIds,
                        receiverName = receiverName,
                        receiverPhone = receiverPhone,
                        receiverAddress = receiverAddress,
                        remark = remark
                    )
                    val response = RetrofitClient.apiService.createOrder("Bearer $token", request)
                    if (response.code == 200) {
                        onSuccess()
                    } else {
                        onError(response.message.ifEmpty { "创建订单失败" })
                    }
                } else {
                    onError("未登录")
                }
            } catch (e: Exception) {
                onError(e.message ?: "网络异常")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getOrderDetail(orderId: Long, onSuccess: (Order) -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.getOrderDetail("Bearer $token", orderId)
                    if (response.code == 200 && response.data != null) {
                        onSuccess(response.data)
                    } else {
                        onError(response.message.ifEmpty { "获取订单详情失败" })
                    }
                } else {
                    onError("未登录")
                }
            } catch (e: Exception) {
                onError(e.message ?: "网络异常")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun payOrder(orderId: Long, onSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.payOrder("Bearer $token", orderId)
                    if (response.code == 200) {
                        loadOrders()
                        onSuccess()
                    } else {
                        onError(response.message.ifEmpty { "支付失败" })
                    }
                } else {
                    onError("未登录")
                }
            } catch (e: Exception) {
                onError(e.message ?: "网络异常")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmReceive(orderId: Long, onSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.confirmReceive("Bearer $token", orderId)
                    if (response.code == 200) {
                        loadOrders()
                        onSuccess()
                    } else {
                        onError(response.message.ifEmpty { "确认收货失败" })
                    }
                } else {
                    onError("未登录")
                }
            } catch (e: Exception) {
                onError(e.message ?: "网络异常")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelOrder(orderId: Long, onSuccess: () -> Unit, onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.cancelOrder("Bearer $token", orderId)
                    if (response.code == 200) {
                        loadOrders()
                        onSuccess()
                    } else {
                        onError(response.message.ifEmpty { "取消订单失败" })
                    }
                } else {
                    onError("未登录")
                }
            } catch (e: Exception) {
                onError(e.message ?: "网络异常")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getToken(): String? {
        return getApplication<AutoPartsApp>().dataStore.data.first()[PreferencesKeys.TOKEN_KEY]
    }
}
