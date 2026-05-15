package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.model.CartItem
import com.zhantu.autopartsmall.data.network.RetrofitClient
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedItems = MutableStateFlow<Set<Long>>(emptySet())
    val selectedItems: StateFlow<Set<Long>> = _selectedItems

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.getCartList("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        _cartItems.value = response.data
                        _selectedItems.value = response.data.map { it.id }.toSet()
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

    fun addToCart(productId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.addToCart("Bearer $token", productId, quantity)
                    if (response.code == 200) {
                        loadCart()
                    } else {
                        _error.value = response.message.ifEmpty { "添加失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateQuantity(cartId: Long, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.updateCartQuantity("Bearer $token", cartId, quantity)
                    if (response.code == 200) {
                        loadCart()
                    } else {
                        _error.value = response.message.ifEmpty { "更新失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFromCart(cartId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.removeFromCart("Bearer $token", cartId)
                    if (response.code == 200) {
                        loadCart()
                    } else {
                        _error.value = response.message.ifEmpty { "删除失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSelectItem(cartId: Long) {
        val current = _selectedItems.value.toMutableSet()
        if (current.contains(cartId)) {
            current.remove(cartId)
        } else {
            current.add(cartId)
        }
        _selectedItems.value = current
    }

    fun selectAll() {
        _selectedItems.value = _cartItems.value.map { it.id }.toSet()
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun getTotalPrice(): Double {
        return _cartItems.value
            .filter { _selectedItems.value.contains(it.id) }
            .sumOf { it.price * it.quantity }
    }

    fun getSelectedCount(): Int {
        return _selectedItems.value.size
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.clearCart("Bearer $token")
                    if (response.code == 200) {
                        _cartItems.value = emptyList()
                        _selectedItems.value = emptySet()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun getToken(): String? {
        return getApplication<AutoPartsApp>().dataStore.data.first()[PreferencesKeys.TOKEN_KEY]
    }
}
