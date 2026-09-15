package com.pizzza.pizzzaapp.repository.network

import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.loadParentOrder
import com.pizzza.pizzzaapp.repository.network.model.toModelList
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork

class DataNetwork(
    private val apiService: KmmService
) : IDataNetwork {

    override suspend fun syncProducts(): List<ProductModel> = apiCall({
        val response = apiService.getProducts()
        val models = response.toModelList()
        models
    }) { it }

    override suspend fun createOrder(data: List<OrderResponse>): String = apiCall {
        apiService.createOrder(data)
    }

    override suspend fun loadParentOrder(userId: String): List<ParentOrderModel> {
        return apiCall({
            val response = apiService.getParentOrder(userId)
            response
        }) { response ->
            response.loadParentOrder()
        }
    }

    override suspend fun getOrderById(orderId: String): ParentOrderModel = apiCall({
        apiService.getOrderById(orderId)
    }) { response ->
        listOf(response).loadParentOrder().first()
    }

    override suspend fun registerUser(data: UserResponse): String = apiCall {
        apiService.registerUser(data)
    }

    override suspend fun login(data: com.pizzza.pizzzaapp.repository.network.model.LoginRequest): com.pizzza.pizzzaapp.repository.network.model.LoginResponse = apiCall {
        apiService.login(data)
    }

    override suspend fun createPaymentSession(amount: Double, email: String, orderId: String): String {
        val request = com.pizzza.pizzzaapp.repository.network.model.PaymentRequest(
            amount = (amount * 100).toLong(),
            currency = "mxn",
            orderId = orderId,
            customerEmail = email,
            successUrl = "https://pizzzaapp.com/success?orderId=$orderId",
            cancelUrl = "https://pizzzaapp.com/cancel?orderId=$orderId",
            appSuccessUrl = "pizzitas://payment/success",
            appCancelUrl = "pizzitas://payment/cancel"
        )
        val response = apiService.createPaymentSession(request)
        return response.url ?: ""
    }
}
