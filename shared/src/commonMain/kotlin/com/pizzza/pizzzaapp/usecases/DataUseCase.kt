package com.pizzza.pizzzaapp.usecases

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.UserModel
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.usecases.network.IDataDataBase
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork

class DataUseCase(private val iDataNetwork: IDataNetwork, private val iDataDBNetwork: IDataDataBase) {

    @Throws(Exception::class)
    suspend fun loadParentOrder(userId: String) = iDataNetwork.loadParentOrder(userId)

    @Throws(Exception::class)
    suspend fun getOrderById(orderId: String) = iDataNetwork.getOrderById(orderId)

    @Throws(Exception::class)
    suspend fun syncProducts(): List<ProductModel> {
        val response = iDataNetwork.syncProducts()
        iDataDBNetwork.getProducts()
        iDataDBNetwork.deleteAll()
        iDataDBNetwork.insertAll(response)
        return response
    }


    @Throws(Exception::class)
    suspend fun getProducts(): List<ProductModel> {
        return iDataNetwork.syncProducts()
    }

    @Throws(Exception::class)
    suspend fun getProductsLocal() = iDataDBNetwork.getProducts()

    @Throws(Exception::class)
    suspend fun createOrder(data: List<OrderResponse>) = iDataNetwork.createOrder(data)

    @Throws(Exception::class)
    suspend fun registerUser(data: UserResponse) = iDataNetwork.registerUser(data)

    @Throws(Exception::class)
    suspend fun login(data: LoginRequest) = iDataNetwork.login(data)

    @Throws(Exception::class)
    suspend fun saveUserLocal(user: UserModel) = iDataDBNetwork.saveUserLocal(user)

    @Throws(Exception::class)
    suspend fun getUserLocal() = iDataDBNetwork.getUserLocal()

    @Throws(Exception::class)
    suspend fun logout() {
        iDataDBNetwork.logout()
    }

    @Throws(Exception::class)
    suspend fun createPaymentSession(amount: Double, email: String, orderId: String) =
        iDataNetwork.createPaymentSession(amount, email, orderId)

    @Throws(Exception::class)
    suspend fun getBranch() = iDataNetwork.getBranches()
}
