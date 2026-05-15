package com.zhantu.autopartsmall.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Long,
    val name: String,
    val parentId: Long?,
    val level: Int,
    val sort: Int,
    val icon: String?,
    val status: Int,
    @SerializedName("createTime") val createTimeRaw: Any? = null,
    @SerializedName("updateTime") val updateTimeRaw: Any? = null,
    val children: List<Category>? = null
)
