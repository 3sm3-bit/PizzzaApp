package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderMobileResponse(
    @SerialName("ok")
    val ok: Boolean = false,
    @SerialName("mensaje")
    val mensaje: String = "",
    @SerialName("count")
    val count: Int = 0,
    @SerialName("ordenGeneral")
    val ordenGeneral: ParentOrderResponse? = null,
    @SerialName("orders")
    val orders: List<OrderResponse> = emptyList()
)
