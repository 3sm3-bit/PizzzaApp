package com.tayler.pizzzaapp.repository.network.model

import com.tayler.pizzzaapp.model.ParentOrderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.String

@Serializable
class ParentOrderResponse(
    @SerialName("uid")
    val uid: String? = "",
    @SerialName("nameClient")
    val nameClient: String? = "",
    @SerialName("description")
    val description: String? = "",
    @SerialName("priceTotal")
    val price: String? = "",
    @SerialName("phone")
    val phone: String? = "",
    @SerialName("date")
    val date: String? = "",
    @SerialName("state")
    val state: String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("reception")
    val reception: String? = "",
    @SerialName("orders")
    val orders: List<OrderResponse>? = emptyList()
)

fun List<ParentOrderResponse>.loadParentOrder() = this.map {
    ParentOrderModel(
        uid = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        description = it.description ?: "",
        price = it.price ?: "",
        phone = it.phone ?: "",
        date = it.date ?: "",
        state = it.state ?: "",
        address = it.address ?: "",
        reception = it.reception ?: "",
        orders = it.orders?.loadOrder() ?: emptyList()
    )
}
