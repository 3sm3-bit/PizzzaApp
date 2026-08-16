package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.repository.model.OrderResponse
import com.tayler.pizzzaapp.repository.model.ParentOrderResponse
import com.tayler.pizzzaapp.shared.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
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
        return client.get("${BASE_URL}/parentOrder").body()
    }

    suspend fun updateParentOrder(request: ParentOrderResponse): String {
        return client.post("${BASE_URL}/parentOrder") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
