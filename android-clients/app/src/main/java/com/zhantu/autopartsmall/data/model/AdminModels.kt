package com.zhantu.autopartsmall.data.model

data class AdminLoginResponse(
    val id: Long,
    val username: String,
    val token: String,
    val role: String
)

data class ServiceAppointment(
    val id: Long,
    val appointmentTime: String,
    val contactName: String,
    val contactPhone: String,
    val status: String,
    val remark: String?
)

data class AdminUser(
    val id: Long,
    val username: String?,
    val phone: String?,
    val nickname: String?,
    val email: String?,
    val status: Int?
)

data class AdminVehicleInfo(
    val brandName: String?,
    val seriesName: String?,
    val year: String?,
    val engineModel: String?
)
