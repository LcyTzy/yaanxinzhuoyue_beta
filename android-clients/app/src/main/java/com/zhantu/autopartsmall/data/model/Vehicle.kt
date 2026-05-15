package com.zhantu.autopartsmall.data.model

data class VehicleBrand(
    val id: Long,
    val name: String,
    val initial: String,
    val sort: Int,
    val status: Int
)

data class VehicleSeries(
    val id: Long,
    val brandId: Long,
    val name: String,
    val sort: Int,
    val status: Int
)

data class VehicleModel(
    val id: Long,
    val seriesId: Long,
    val name: String,
    val year: String?,
    val displacement: String?,
    val engine: String?,
    val transmission: String?,
    val sort: Int,
    val status: Int
)
