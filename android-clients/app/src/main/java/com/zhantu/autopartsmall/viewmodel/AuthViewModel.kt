package com.zhantu.autopartsmall.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zhantu.autopartsmall.AutoPartsApp
import com.zhantu.autopartsmall.dataStore
import com.zhantu.autopartsmall.data.local.PreferencesKeys
import com.zhantu.autopartsmall.data.model.LoginRequest
import com.zhantu.autopartsmall.data.model.RegisterRequest
import com.zhantu.autopartsmall.data.model.User
import com.zhantu.autopartsmall.data.network.ApiService
import com.zhantu.autopartsmall.data.network.RetrofitClient
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    private val _resetState = MutableStateFlow<AuthState>(AuthState.Idle)
    val resetState: StateFlow<AuthState> = _resetState

    private val _sendCodeState = MutableStateFlow<AuthState>(AuthState.Idle)
    val sendCodeState: StateFlow<AuthState> = _sendCodeState

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(phone, password))
                if (response.code == 200 && response.data != null) {
                    val loginData = response.data
                    saveToken(loginData.token)
                    saveUserInfo(loginData.user)
                    saveUserRole(loginData.user.role)
                    _loginState.value = AuthState.Success(loginData)
                } else {
                    _loginState.value = AuthState.Error(response.message.ifEmpty { "登录失败" })
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "网络异常")
            }
        }
    }

    fun register(phone: String, password: String, nickname: String?, captcha: String, captchaKey: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(phone, password, nickname, captcha, captchaKey)
                )
                if (response.code == 200) {
                    _registerState.value = AuthState.Success(null)
                } else {
                    _registerState.value = AuthState.Error(response.message.ifEmpty { "注册失败" })
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(e.message ?: "网络异常")
            }
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            getApplication<AutoPartsApp>().dataStore.edit { preferences ->
                preferences[PreferencesKeys.TOKEN_KEY] = token
            }
        }
    }

    private fun saveUserInfo(user: User) {
        viewModelScope.launch {
            getApplication<AutoPartsApp>().dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_ID_KEY] = user.id ?: 0L
                user.username?.let { preferences[PreferencesKeys.USERNAME_KEY] = it }
                user.phone?.let { preferences[PreferencesKeys.PHONE_KEY] = it }
                user.nickname?.let { preferences[PreferencesKeys.NICKNAME_KEY] = it }
            }
        }
    }

    private fun saveUserRole(role: String?) {
        viewModelScope.launch {
            getApplication<AutoPartsApp>().dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_ADMIN_KEY] = (role == "ADMIN")
            }
        }
    }

    fun sendResetCode(phone: String) {
        viewModelScope.launch {
            _sendCodeState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.sendResetCode(ApiService.ResetCodeRequest(phone))
                if (response.code == 200) {
                    _sendCodeState.value = AuthState.Success(response.data)
                } else {
                    _sendCodeState.value = AuthState.Error(response.message.ifEmpty { "发送失败" })
                }
            } catch (e: Exception) {
                _sendCodeState.value = AuthState.Error(e.message ?: "网络异常")
            }
        }
    }

    fun resetPassword(phone: String, code: String, newPassword: String) {
        viewModelScope.launch {
            _resetState.value = AuthState.Loading
            try {
                val response = RetrofitClient.apiService.resetPassword(
                    ApiService.ResetPasswordRequest(phone, code, newPassword)
                )
                if (response.code == 200) {
                    _resetState.value = AuthState.Success(null)
                } else {
                    _resetState.value = AuthState.Error(response.message.ifEmpty { "重置失败" })
                }
            } catch (e: Exception) {
                _resetState.value = AuthState.Error(e.message ?: "网络异常")
            }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val data: Any?) : AuthState()
    data class Error(val message: String) : AuthState()
}
