package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.network.FavoriteItem
import com.zhantu.autopartsmall.data.network.RetrofitClient
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val _favorites = MutableStateFlow<List<FavoriteItem>>(emptyList())
    val favorites: StateFlow<List<FavoriteItem>> = _favorites

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFavorites()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.getFavoriteList("Bearer $token")
                    if (response.code == 200 && response.data != null) {
                        _favorites.value = response.data.records ?: emptyList()
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

    fun toggleFavorite(productId: Long, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.toggleFavorite("Bearer $token", mapOf("productId" to productId))
                    if (response.code == 200) {
                        loadFavorites()
                        checkFavorite(productId) { isFav ->
                            onSuccess(isFav)
                        }
                    } else {
                        _error.value = response.message.ifEmpty { "操作失败" }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkFavorite(productId: Long, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.checkFavorite("Bearer $token", productId)
                    if (response.code == 200 && response.data != null) {
                        callback(response.data["favorite"] ?: false)
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            }
        }
    }

    fun addFavorite(productId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.toggleFavorite("Bearer $token", mapOf("productId" to productId))
                    if (response.code == 200) {
                        loadFavorites()
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "网络异常"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeFavorite(productId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getToken()
                if (token != null) {
                    val response = RetrofitClient.apiService.toggleFavorite("Bearer $token", mapOf("productId" to productId))
                    if (response.code == 200) {
                        loadFavorites()
                        onSuccess()
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
