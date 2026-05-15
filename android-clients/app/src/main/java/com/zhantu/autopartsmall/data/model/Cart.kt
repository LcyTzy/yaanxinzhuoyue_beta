package com.zhantu.autopartsmall.data.model

data class CartItem(
    val id: Long,
    val productId: Long,
    val productName: String,
    val productImage: String?,
    val price: Double,
    val quantity: Int,
    val spec: String?
)

data class CartAddRequest(
    val productId: Long,
    val quantity: Int
)
