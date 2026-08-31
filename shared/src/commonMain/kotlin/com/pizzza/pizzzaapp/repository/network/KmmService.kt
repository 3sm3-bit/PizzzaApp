package com.pizzza.pizzzaapp.repository.network

import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.repository.network.model.ParentOrderResponse
import com.pizzza.pizzzaapp.repository.network.model.ProductResponse
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.BranchResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.LoginResponse
import com.pizzza.pizzzaapp.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class KmmService(private val client: HttpClient) {

    companion object {
        val BASE_URL = if (BuildConfig.IS_DEBUG) {
            BuildConfig.BASE_URL_SERVICE_DEV
        } else {
            BuildConfig.BASE_URL_SERVICE
        }
    }

    suspend fun getParentOrder(userId: String): List<ParentOrderResponse> {
        return client.get("${BASE_URL}/pizzzeria/order/generalOrder/user/hoy/$userId").body()
    }

    suspend fun getOrderById(orderId: String): ParentOrderResponse {
        return client.get("${BASE_URL}/pizzzeria/order/generalOrder/$orderId").body()
    }

    suspend fun getProducts(): List<ProductResponse> {
        return client.get("${BASE_URL}/pizzzeria/products").body()
    }

    suspend fun createOrder(request: List<OrderResponse>): String {
        return client.post("${BASE_URL}/pizzzeria/order") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun registerUser(request: UserResponse): String {
        println("$TAG_PIZZZA: KmmService: Enviando registro para ${request.email}...")
        val response = client.post("${BASE_URL}/services/user") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val result = response.bodyAsText()
        println("$TAG_PIZZZA: KmmService: Respuesta recibida (Status: ${response.status}): $result")
        return result
    }

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("${BASE_URL}/services/user/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
