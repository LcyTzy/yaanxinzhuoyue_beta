package com.zhantu.autopartsmall.data.network

import com.zhantu.autopartsmall.data.model.*
import retrofit2.http.*

interface ApiService {

    @POST("/api/user/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("/api/user/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<User>

    @GET("/api/user/captcha")
    suspend fun getCaptcha(): ApiResponse<CaptchaResponse>

    @POST("/api/user/sendResetCode")
    suspend fun sendResetCode(@Body request: ResetCodeRequest): ApiResponse<String>

    @POST("/api/user/resetPassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ApiResponse<String>

    data class ResetCodeRequest(val phone: String)
    data class ResetPasswordRequest(val phone: String, val code: String, val newPassword: String)

    @GET("/api/product/category/tree")
    suspend fun getCategoryTree(): ApiResponse<List<Category>>

    @GET("/api/product/page")
    suspend fun getProductList(
        @Query("pageNum") current: Int,
        @Query("pageSize") size: Int,
        @Query("keyword") keyword: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("brand") brand: String? = null,
        @Query("vehicleModelId") vehicleModelId: Long? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = "desc"
    ): ApiResponse<PageData<Product>>

    @GET("/api/product/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): ApiResponse<ProductDetailResponse>

    data class ProductDetailResponse(
        val product: Product,
        val applicableVehicles: List<Any>?
    )

    @GET("/api/cart/list")
    suspend fun getCartList(@Header("Authorization") token: String): ApiResponse<List<CartItem>>

    @POST("/api/cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Query("productId") productId: Long,
        @Query("quantity") quantity: Int = 1
    ): ApiResponse<Unit>

    @PUT("/api/cart/update")
    suspend fun updateCartQuantity(
        @Header("Authorization") token: String,
        @Query("cartId") cartId: Long,
        @Query("quantity") quantity: Int
    ): ApiResponse<Unit>

    @DELETE("/api/cart/{id}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @DELETE("/api/cart/clear")
    suspend fun clearCart(
        @Header("Authorization") token: String
    ): ApiResponse<Unit>

    @POST("/api/order/create")
    suspend fun createOrder(
        @Header("Authorization") token: String,
        @Body request: CreateOrderRequest
    ): ApiResponse<Long>

    @GET("/api/order/page")
    suspend fun getOrderList(
        @Header("Authorization") token: String,
        @Query("pageNum") current: Int,
        @Query("pageSize") size: Int,
        @Query("status") status: Int? = null
    ): ApiResponse<PageData<Order>>

    @GET("/api/order/{orderId}")
    suspend fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long
    ): ApiResponse<Order>

    @POST("/api/order/pay/{orderId}")
    suspend fun payOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long
    ): ApiResponse<Unit>

    @PUT("/api/order/{orderId}/confirm")
    suspend fun confirmReceive(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long
    ): ApiResponse<Unit>

    @PUT("/api/order/{orderId}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("orderId") orderId: Long
    ): ApiResponse<Unit>

    @GET("/api/product/hot")
    suspend fun getHotProducts(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") limit: Int = 10
    ): ApiResponse<PageData<Product>>

    @GET("/api/product/page")
    suspend fun getRecommendProducts(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") limit: Int = 10
    ): ApiResponse<PageData<Product>>

    @GET("/api/address/list")
    suspend fun getAddressList(@Header("Authorization") token: String): ApiResponse<List<Address>>

    @POST("/api/address")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Body request: AddressAddRequest
    ): ApiResponse<Unit>

    @PUT("/api/address")
    suspend fun updateAddress(
        @Header("Authorization") token: String,
        @Body request: AddressUpdateRequest
    ): ApiResponse<Unit>

    @DELETE("/api/address/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @PUT("/api/address/{id}/default")
    suspend fun setDefaultAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @GET("/api/favorite/page")
    suspend fun getFavoriteList(
        @Header("Authorization") token: String,
        @Query("pageNum") current: Int = 1,
        @Query("pageSize") size: Int = 100
    ): ApiResponse<PageData<FavoriteItem>>

    @POST("/api/favorite/toggle")
    suspend fun toggleFavorite(
        @Header("Authorization") token: String,
        @Body request: Map<String, Long>
    ): ApiResponse<Unit>

    @GET("/api/favorite/check")
    suspend fun checkFavorite(
        @Header("Authorization") token: String,
        @Query("productId") productId: Long
    ): ApiResponse<Map<String, Boolean>>

    @GET("/api/vehicle/decode-with-parts")
    suspend fun decodeVin(
        @Query("vin") vin: String,
        @Query("source") source: String = "auto"
    ): VinDecodeResult

    @GET("/api/service-order/types")
    suspend fun getServiceTypes(): ApiResponse<List<ServiceType>>

    @POST("/api/service-order")
    suspend fun createServiceOrder(
        @Header("Authorization") token: String,
        @Body request: CreateServiceOrderRequest
    ): ApiResponse<ServiceOrder>

    @GET("/api/service-order/my")
    suspend fun getMyServiceOrders(
        @Header("Authorization") token: String,
        @Query("userId") userId: Long,
        @Query("status") status: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageData<ServiceOrder>>

    @GET("/api/user/info")
    suspend fun getUserInfo(@Header("Authorization") token: String): ApiResponse<UserInfo>

    @GET("/api/points/log")
    suspend fun getPointsLog(
        @Header("Authorization") token: String,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): ApiResponse<PageData<PointsLog>>

    @GET("/api/coupon/list")
    suspend fun getAvailableCoupons(): ApiResponse<List<Coupon>>

    @POST("/api/coupon/receive")
    suspend fun receiveCoupon(
        @Header("Authorization") token: String,
        @Body request: ReceiveCouponRequest
    ): ApiResponse<Unit>

    @GET("/api/coupon/my")
    suspend fun getMyCoupons(
        @Header("Authorization") token: String,
        @Query("status") status: Int? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageData<UserCoupon>>

    @GET("/api/vehicle/brands")
    suspend fun getVehicleBrands(): ApiResponse<List<com.zhantu.autopartsmall.data.model.VehicleBrand>>

    @GET("/api/vehicle/series/{brandId}")
    suspend fun getVehicleSeries(@Path("brandId") brandId: Long): ApiResponse<List<com.zhantu.autopartsmall.data.model.VehicleSeries>>

    @GET("/api/vehicle/models/{seriesId}")
    suspend fun getVehicleModels(@Path("seriesId") seriesId: Long): ApiResponse<List<com.zhantu.autopartsmall.data.model.VehicleModel>>

    // 管理员接口
    @POST("/api/admin/login")
    suspend fun adminLogin(
        @Body request: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    @POST("/api/admin/register")
    suspend fun adminRegister(
        @Body request: Map<String, String>
    ): ApiResponse<Map<String, Any>>

    @GET("/api/admin/stats")
    suspend fun getAdminStats(@Header("Authorization") token: String): ApiResponse<Map<String, Any>>

    @GET("/api/admin/product/page")
    suspend fun getAdminProducts(
        @Header("Authorization") token: String,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
        @Query("keyword") keyword: String? = null,
        @Query("categoryId") categoryId: Long? = null
    ): ApiResponse<PageData<Product>>

    @GET("/api/admin/category/tree")
    suspend fun getAdminCategories(@Header("Authorization") token: String): ApiResponse<List<Category>>

    @POST("/api/admin/category")
    suspend fun addCategory(
        @Header("Authorization") token: String,
        @Body category: Category
    ): ApiResponse<Unit>

    @PUT("/api/admin/category")
    suspend fun updateCategory(
        @Header("Authorization") token: String,
        @Body category: Category
    ): ApiResponse<Unit>

    @DELETE("/api/admin/category/{id}")
    suspend fun deleteCategory(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @GET("/api/admin/order/page")
    suspend fun getAdminOrders(
        @Header("Authorization") token: String,
        @Query("status") status: Int? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageData<Order>>

    @PUT("/api/admin/order/{id}/ship")
    suspend fun adminShipOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long,
        @Query("logisticsCompany") expressCompany: String,
        @Query("logisticsNo") expressNo: String
    ): ApiResponse<Unit>

    @PUT("/api/admin/order/{id}/status")
    suspend fun updateOrderStatus(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long,
        @Body params: Map<String, Any>
    ): ApiResponse<Unit>

    @GET("/api/admin/user/page")
    suspend fun getAdminUsers(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageData<UserInfo>>

    @PUT("/api/admin/user/status/{id}")
    suspend fun updateUserStatus(
        @Header("Authorization") token: String,
        @Path("id") userId: Long,
        @Body params: Map<String, Int>
    ): ApiResponse<Unit>

    @PUT("/api/admin/user/reset-password/{id}")
    suspend fun resetUserPassword(
        @Header("Authorization") token: String,
        @Path("id") userId: Long
    ): ApiResponse<Unit>

    @GET("/api/admin/service-order/page")
    suspend fun getAdminServiceAppointments(
        @Header("Authorization") token: String,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 50
    ): ApiResponse<PageData<ServiceOrder>>

    @PUT("/api/admin/service-order/{id}/confirm")
    suspend fun confirmServiceOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): ApiResponse<Unit>

    @PUT("/api/admin/service-order/{id}/complete")
    suspend fun completeServiceOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): ApiResponse<Unit>

    @PUT("/api/admin/service-order/{id}/cancel")
    suspend fun cancelServiceOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: Long
    ): ApiResponse<Unit>

    @GET("/api/admin/vehicle/brands")
    suspend fun getAdminVehicleBrands(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String? = null
    ): ApiResponse<List<VehicleBrand>>

    @POST("/api/admin/vehicle/brand")
    suspend fun addVehicleBrand(
        @Header("Authorization") token: String,
        @Body brand: VehicleBrand
    ): ApiResponse<Unit>

    @PUT("/api/admin/vehicle/brand/{id}")
    suspend fun updateVehicleBrand(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body brand: VehicleBrand
    ): ApiResponse<Unit>

    @DELETE("/api/admin/vehicle/brand/{id}")
    suspend fun deleteVehicleBrand(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @GET("/api/admin/vehicle/series")
    suspend fun getAdminVehicleSeries(
        @Header("Authorization") token: String,
        @Query("brandId") brandId: Long? = null
    ): ApiResponse<List<VehicleSeries>>

    @POST("/api/admin/vehicle/series")
    suspend fun addVehicleSeries(
        @Header("Authorization") token: String,
        @Body series: VehicleSeries
    ): ApiResponse<Unit>

    @PUT("/api/admin/vehicle/series/{id}")
    suspend fun updateVehicleSeries(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body series: VehicleSeries
    ): ApiResponse<Unit>

    @DELETE("/api/admin/vehicle/series/{id}")
    suspend fun deleteVehicleSeries(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>

    @GET("/api/admin/vehicle/models")
    suspend fun getAdminVehicleModels(
        @Header("Authorization") token: String,
        @Query("seriesId") seriesId: Long? = null,
        @Query("keyword") keyword: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PageData<VehicleModel>>

    @POST("/api/admin/vehicle/model")
    suspend fun addVehicleModel(
        @Header("Authorization") token: String,
        @Body model: VehicleModel
    ): ApiResponse<Unit>

    @PUT("/api/admin/vehicle/model/{id}")
    suspend fun updateVehicleModel(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body model: VehicleModel
    ): ApiResponse<Unit>

    @DELETE("/api/admin/vehicle/model/{id}")
    suspend fun deleteVehicleModel(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Unit>
}

data class VinDecodeResult(
    val code: Int,
    val message: String?,
    val vehicle: VehicleInfo?,
    val categories: Map<String, List<MatchedPart>>?,
    val matchedParts: List<MatchedPart>?,
    val source: String?
)

data class VehicleInfo(
    val brand: String?,
    val model: String?,
    val year: Int?,
    val engine: String?,
    val transmission: String?,
    val driveType: String?
)

data class MatchedPart(
    val id: Long?,
    val name: String?,
    val category: String?,
    val price: Double?,
    val oeNumber: String?,
    val compatibleInfo: String?
)

data class Address(
    val id: Long,
    val userId: Long,
    val receiverName: String,
    val receiverPhone: String,
    val province: String?,
    val city: String?,
    val district: String?,
    val detail: String,
    val isDefault: Int?
)

data class AddressAddRequest(
    val receiverName: String,
    val receiverPhone: String,
    val province: String?,
    val city: String?,
    val district: String?,
    val detail: String,
    val isDefault: Int?
)

data class AddressUpdateRequest(
    val id: Long,
    val receiverName: String,
    val receiverPhone: String,
    val province: String?,
    val city: String?,
    val district: String?,
    val detail: String,
    val isDefault: Int?
)

data class FavoriteItem(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val productName: String?,
    val productImage: String?,
    val price: Double?,
    val createTime: String?
)

data class ServiceType(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double?,
    val durationMinutes: Int?,
    val sort: Int?,
    val status: Int?
)

data class ServiceOrder(
    val id: Long,
    val orderNo: String?,
    val userId: Long?,
    val userName: String?,
    val userPhone: String?,
    val serviceType: String?,
    val vehicleBrand: String?,
    val vehicleModel: String?,
    val vehicleYear: String?,
    val licensePlate: String?,
    val appointmentTime: String?,
    val status: String?,
    val remark: String?,
    val relatedProductId: Long?,
    val relatedOrderId: Long?,
    val createTime: String?,
    val updateTime: String?
)

data class CreateServiceOrderRequest(
    val userId: Long,
    val userName: String,
    val userPhone: String,
    val serviceType: String,
    val vehicleBrand: String?,
    val vehicleModel: String?,
    val vehicleYear: String?,
    val licensePlate: String?,
    val appointmentTime: String,
    val remark: String?
)

data class UserInfo(
    val id: Long,
    val username: String?,
    val phone: String?,
    val nickname: String?,
    val points: Int?,
    val role: String?,
    val status: Int?
)

data class PointsLog(
    val id: Long,
    val userId: Long,
    val points: Int,
    val description: String?,
    val createTime: String?
)

data class Coupon(
    val id: Long,
    val name: String,
    val type: Int,
    val discountAmount: Double?,
    val discountRate: Double?,
    val minAmount: Double?,
    val totalCount: Int,
    val receiveCount: Int,
    val startTime: String?,
    val endTime: String?
)

data class UserCoupon(
    val id: Long,
    val userId: Long,
    val couponId: Long,
    val status: Int,
    val useTime: String?,
    val coupon: Coupon?
)

data class ReceiveCouponRequest(
    val couponId: Long
)
