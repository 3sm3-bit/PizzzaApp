package com.pizzza.pizzzaapp.repository.network

import com.pizzza.pizzzaapp.repository.network.model.ParentOrderResponse
import com.pizzza.pizzzaapp.repository.network.model.ProductResponse
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.BranchResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.LoginResponse
import com.pizzza.pizzzaapp.repository.network.model.RefreshTokenRequest
import com.pizzza.pizzzaapp.repository.network.model.RefreshTokenResponse
import com.pizzza.pizzzaapp.repository.network.model.ConfirmOrderRequest
import com.pizzza.pizzzaapp.repository.network.model.CreateOrderMobileResponse
import com.pizzza.pizzzaapp.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.pizzza.pizzzaapp.repository.network.model.PaymentRequest
import com.pizzza.pizzzaapp.repository.network.model.PaymentResponse

class KmmService(private val client: HttpClient) {

    companion object {
        val BASE_URL = if (BuildConfig.IS_DEBUG) {
            BuildConfig.BASE_URL_SERVICE_DEV
        } else {
            BuildConfig.BASE_URL_SERVICE
        }
    }

    private fun io.ktor.client.request.HttpRequestBuilder.addAuthToken(token: String?) {
        if (!token.isNullOrBlank()) {
            headers.append("x-token", token)
            headers.append("Authorization", "Bearer $token")
        }
    }

    suspend fun getParentOrder(userId: String, token: String? = null): List<ParentOrderResponse> {
        return client.get("${BASE_URL}/pizzzeria/order/generalOrder/user/hoy/$userId") {
            addAuthToken(token)
        }.body()
    }

    suspend fun getOrderById(orderId: String, token: String? = null): ParentOrderResponse {
        return client.get("${BASE_URL}/pizzzeria/order/generalOrder/$orderId") {
            addAuthToken(token)
        }.body()
    }

    suspend fun deleteGeneralOrder(orderId: String, token: String? = null): String {
        return client.delete("${BASE_URL}/pizzzeria/order/generalOrder/$orderId") {
            addAuthToken(token)
        }.bodyAsText()
    }

    suspend fun getProducts(): List<ProductResponse> {
        return client.get("${BASE_URL}/pizzzeria/products").body()
    }

    suspend fun createOrder(request: List<OrderResponse>, token: String? = null): String {
        return client.post("${BASE_URL}/pizzzeria/order") {
            addAuthToken(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun createOrderMobile(request: List<OrderResponse>, token: String? = null): CreateOrderMobileResponse {
        return client.post("${BASE_URL}/pizzzeria/order/mobile") {
            addAuthToken(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun confirmOrderMobile(ordenGeneralUid: String, request: ConfirmOrderRequest = ConfirmOrderRequest(), token: String? = null): CreateOrderMobileResponse {
        return client.put("${BASE_URL}/pizzzeria/order/mobile/$ordenGeneralUid/confirm") {
            addAuthToken(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun registerUser(request: UserResponse): String {
        val response = client.post("${BASE_URL}/services/user") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.bodyAsText()
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("${BASE_URL}/services/user/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun refreshToken(refreshToken: String): RefreshTokenResponse {
        return client.post("${BASE_URL}/services/user/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }.body()
    }

    suspend fun createPaymentSession(request: PaymentRequest, token: String? = null): PaymentResponse {
        return client.post("${BASE_URL}/services/payment/create") {
            addAuthToken(token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getBranches(): List<BranchResponse> {
        return client.get("${BASE_URL}/pizzzeria/branch").body()
    }

}
