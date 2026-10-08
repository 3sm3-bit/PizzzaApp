package com.pizzza.pizzzaapp.usecases.network

import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.LoginResponse
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.RefreshTokenResponse
import com.pizzza.pizzzaapp.repository.network.model.ConfirmOrderRequest
import com.pizzza.pizzzaapp.repository.network.model.CreateOrderMobileResponse

interface IDataNetwork {

    suspend fun loadParentOrder(userId: String): List<ParentOrderModel>

    suspend fun getOrderById(orderId: String): ParentOrderModel

    suspend fun deleteGeneralOrder(orderId: String): String

    suspend fun syncProducts(): List<ProductModel>

    suspend fun createOrder(data: List<OrderResponse>): String

    suspend fun createOrderMobile(data: List<OrderResponse>): CreateOrderMobileResponse

    suspend fun confirmOrderMobile(ordenGeneralUid: String, request: ConfirmOrderRequest = ConfirmOrderRequest()): CreateOrderMobileResponse

    suspend fun registerUser(data: UserResponse): String

    suspend fun deleteUser(userId: String): String

    suspend fun login(data: LoginRequest): LoginResponse

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse

    suspend fun createPaymentSession(amount: Double, email: String, orderId: String): String

    suspend fun getBranches(): List<BranchModel>

}
