package com.zhantu.autopartsmall.data.model

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)

data class PageData<T>(
    val records: List<T>,
    val total: Long,
    val current: Long,
    val size: Long
)
