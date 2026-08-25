package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.repository.network.model.OrderResponse
import com.tayler.pizzzaapp.repository.network.model.ParentOrderResponse
import com.tayler.pizzzaapp.repository.network.model.ProductResponse
import com.tayler.pizzzaapp.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
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

    suspend fun getOrder(): List<OrderResponse> {
        return client.get("${BASE_URL}/pizza/order").body()
    }

    suspend fun getParentOrder(): List<ParentOrderResponse> {
        return client.get("${BASE_URL}/order/generalOrder").body()
    }

    suspend fun getProducts(): List<ProductResponse> {
        return client.get("${BASE_URL}/products").body()
    }

    suspend fun updateParentOrder(request: ParentOrderResponse): String {
        return client.put("${BASE_URL}/order/generalOrder") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
