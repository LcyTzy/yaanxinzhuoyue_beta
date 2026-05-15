package com.zhantu.autopartsmall.data.local

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val TOKEN_KEY = stringPreferencesKey("token")
    val USER_ID_KEY = longPreferencesKey("user_id")
    val USERNAME_KEY = stringPreferencesKey("username")
    val PHONE_KEY = stringPreferencesKey("phone")
    val NICKNAME_KEY = stringPreferencesKey("nickname")
    val VIN_HISTORY_KEY = stringPreferencesKey("vin_history")
    val IS_ADMIN_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("is_admin")
}
