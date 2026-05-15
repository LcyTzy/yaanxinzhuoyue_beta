package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.network.Address
import com.zhantu.autopartsmall.data.network.AddressAddRequest
import com.zhantu.autopartsmall.data.network.RetrofitClient
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddressViewModel(application: Application) : AndroidViewModel(application) {
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.getAddressList("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        _addresses.value = response.data
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

    fun addAddress(
        receiverName: String,
        receiverPhone: String,
        province: String?,
        city: String?,
        district: String?,
        detail: String,
        isDefault: Int?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.addAddress(
                        "Bearer $token",
                        AddressAddRequest(receiverName, receiverPhone, province, city, district, detail, isDefault)
                    )
                    if (response.code == 200) {
                        loadAddresses()
                        onSuccess()
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

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.deleteAddress("Bearer $token", id)
                    if (response.code == 200) {
                        loadAddresses()
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

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.setDefaultAddress("Bearer $token", id)
                    if (response.code == 200) {
                        loadAddresses()
                    } else {
                        _error.value = response.message.ifEmpty { "设置失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getToken(): String? {
        return getApplication<AutoPartsApp>().dataStore.data.first()[PreferencesKeys.TOKEN_KEY]
    }
}
