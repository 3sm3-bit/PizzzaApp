package com.pizzza.pizzzaapp.usecases

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork


class DataUseCase(private val iDataNetwork: IDataNetwork) {

    suspend fun loadParentOrder(userId: String) = iDataNetwork.loadParentOrder(userId)

    suspend fun getOrderById(orderId: String) = iDataNetwork.getOrderById(orderId)

    suspend fun syncProducts() = iDataNetwork.syncProducts()

    suspend fun getProducts() = iDataNetwork.getProducts()

    suspend fun createOrder(data: List<OrderResponse>) = iDataNetwork.createOrder(data)

    suspend fun registerUser(data: UserResponse) = iDataNetwork.registerUser(data)

    suspend fun login(data: com.pizzza.pizzzaapp.repository.network.model.LoginRequest) = iDataNetwork.login(data)

    suspend fun saveUserLocal(user: com.pizzza.pizzzaapp.repository.db.entity.UserEntity) = iDataNetwork.saveUserLocal(user)

    suspend fun getUserLocal() = iDataNetwork.getUserLocal()

    suspend fun logout() {
        iDataNetwork.logout()
    }

}