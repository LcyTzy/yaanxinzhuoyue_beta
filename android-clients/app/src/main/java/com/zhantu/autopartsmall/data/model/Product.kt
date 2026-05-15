package com.zhantu.autopartsmall.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Long,
    val code: String,
    val name: String,
    val subName: String?,
    val oem: String?,
    val price: Double,
    val stock: Int,
    val categoryId: Long?,
    val series: String?,
    val qualityGrade: String?,
    val viscosity: String?,
    val spec: String?,
    val unit: String?,
    val brand: String?,
    val image: String?,
    val status: Int,
    val sales: Int?,
    @SerializedName("createTime") val createTimeRaw: Any?
) {
    val createTime: String?
        get() = when (createTimeRaw) {
            is String -> createTimeRaw
            is List<*> -> createTimeRaw.joinToString("-") { it.toString() }
            else -> null
        }

    fun getImageUrl(): String? {
        if (image.isNullOrEmpty()) return null
        val firstImage = image.split(",").firstOrNull() ?: return null
        return if (firstImage.startsWith("http")) firstImage else "http://10.178.93.31:8081$firstImage"
    }
}
