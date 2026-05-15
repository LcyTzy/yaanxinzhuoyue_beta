package com.zhantu.autopartsmall.data.model

data class Order(
    val id: Long,
    val orderNo: String,
    val userId: Long,
    val totalAmount: Double,
    val payAmount: Double,
    val status: Int,
    val receiverName: String?,
    val receiverPhone: String?,
    val receiverAddress: String?,
    val remark: String?,
    val logisticsCompany: String?,
    val logisticsNo: String?,
    val createTime: String?,
    val payTime: String?,
    val items: List<OrderItem>?
)

data class OrderItem(
    val id: Long,
    val orderId: Long,
    val productId: Long,
    val productName: String,
    val productImage: String?,
    val price: Double,
    val quantity: Int,
    val totalPrice: Double
)

data class CreateOrderRequest(
    val cartIds: List<Long>,
    val receiverName: String,
    val receiverPhone: String,
    val receiverAddress: String,
    val remark: String?
)
