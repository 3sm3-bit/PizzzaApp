package com.pizzza.pizzzaapp.usecases.network

import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse

interface IDataNetwork {

    suspend fun loadParentOrder(userId: String): List<ParentOrderModel>

    suspend fun getOrderById(orderId: String): ParentOrderModel

    suspend fun syncProducts(): List<ProductModel>

    suspend fun getProducts(): List<ProductModel>

    suspend fun createOrder(data: List<OrderResponse>): String

    suspend fun registerUser(data: UserResponse): String

    suspend fun login(data: com.pizzza.pizzzaapp.repository.network.model.LoginRequest): com.pizzza.pizzzaapp.repository.network.model.LoginResponse

    suspend fun saveUserLocal(user: com.pizzza.pizzzaapp.repository.db.entity.UserEntity)

    suspend fun getUserLocal(): com.pizzza.pizzzaapp.repository.db.entity.UserEntity?

    suspend fun logout()

}
