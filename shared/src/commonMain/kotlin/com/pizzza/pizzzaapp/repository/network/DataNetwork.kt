package com.pizzza.pizzzaapp.repository.network

import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.LoginResponse
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.loadParentOrder
import com.pizzza.pizzzaapp.repository.network.model.toModelList
import com.pizzza.pizzzaapp.usecases.network.IDataDataBase
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork

import com.pizzza.pizzzaapp.repository.network.model.RefreshTokenResponse

class DataNetwork(
    private val apiService: KmmService,
    private val dataBase: IDataDataBase
) : IDataNetwork {

    override suspend fun syncProducts(): List<ProductModel> = apiCall({
        val response = apiService.getProducts()
        val models = response.toModelList()
        models
    }) { it }

    override suspend fun createOrder(data: List<OrderResponse>): String = apiCall {
        val token = dataBase.getUserLocal()?.token
        apiService.createOrder(data, token = token)
    }

    override suspend fun loadParentOrder(userId: String): List<ParentOrderModel> {
        return apiCall({
            val token = dataBase.getUserLocal()?.token
            val response = apiService.getParentOrder(userId, token = token)
            response
        }) { response ->
            response.loadParentOrder()
        }
    }

    override suspend fun getOrderById(orderId: String): ParentOrderModel = apiCall({
        val token = dataBase.getUserLocal()?.token
        apiService.getOrderById(orderId, token = token)
    }) { response ->
        listOf(response).loadParentOrder().first()
    }

    override suspend fun registerUser(data: UserResponse): String = apiCall {
        apiService.registerUser(data)
    }

    override suspend fun login(data: LoginRequest): LoginResponse = apiCall {
        apiService.login(data)
    }

    override suspend fun refreshToken(refreshToken: String): RefreshTokenResponse = apiCall {
        apiService.refreshToken(refreshToken)
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
        val token = dataBase.getUserLocal()?.token
        val response = apiService.createPaymentSession(request, token = token)
        return response.url ?: ""
    }

    override suspend fun getBranches(): List<BranchModel> = apiCall{
        apiService.getBranches().toModelList()
    }
}
