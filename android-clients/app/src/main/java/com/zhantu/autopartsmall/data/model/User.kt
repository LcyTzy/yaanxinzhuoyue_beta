package com.zhantu.autopartsmall.data.model

data class User(
    val id: Long?,
    val username: String?,
    val phone: String?,
    val nickname: String?,
    val avatar: String?,
    val role: String?
)

data class LoginRequest(
    val phone: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class CaptchaResponse(
    val image: String,
    val captchaKey: String
)

data class RegisterRequest(
    val phone: String,
    val password: String,
    val nickname: String?,
    val captcha: String,
    val captchaKey: String
)
