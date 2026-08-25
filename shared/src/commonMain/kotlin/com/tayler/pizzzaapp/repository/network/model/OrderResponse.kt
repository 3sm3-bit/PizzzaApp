package com.tayler.pizzzaapp.repository.network.model

import com.tayler.pizzzaapp.model.OrderModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class OrderResponse (
    @SerialName("uid")
    val uid : String? = "",
    @SerialName("nameClient")
    val nameClient : String? = "",
    @SerialName("quantity")
    val quantity : String? = "",
    @SerialName("nameProduct")
    val nameProduct : String? = "",
    @SerialName("tamanio")
    val tamanio : String? = "",
    @SerialName("typeDough")
    val typeDough : String? = "",
    @SerialName("cheeseFilledCrust")
    val cheeseFilledCrust : String? = "",
    @SerialName("note")
    val note : String? = "",
    @SerialName("phone")
    val phone : String? = "",
    @SerialName("price")
    val price : String? = "",
    @SerialName("priceTotal")
    val priceTotal : String? = "",
    @SerialName("state")
    val state : String? = "",
    @SerialName("date")
    val date : String? = "",
    @SerialName("idOrder")
    val idOrder: String? = "",
    @SerialName("address")
    val address: String? = "",
    @SerialName("reception")
    val reception: String? = "",
    @SerialName("priceDelivery")
    val priceDelivery: String? = "",
    @SerialName("priceChosse")
    val priceChosse: String? = ""
)

fun List<OrderResponse>.loadOrder() = this.map {
    OrderModel(
        ui = it.uid ?: "",
        nameClient = it.nameClient ?: "",
        quantity = it.quantity ?: "",
        nameProduct = it.nameProduct ?: "",
        tamanio = it.tamanio ?: "",
        typeDough = it.typeDough ?: "",
        cheeseFilledCrust = it.cheeseFilledCrust ?: "",
        note = it.note ?: "",
        phone = it.phone ?: "",
        price = it.price ?: "",
        priceTotal = it.priceTotal ?: "",
        state = it.state ?: "",
        date = it.date ?: "",
        idOrder = it.idOrder ?: "",
        address = it.address ?: "",
        reception = it.reception ?: "",
        priceDelivery = it.priceDelivery ?: "",
        priceChosse = it.priceChosse ?: ""
    )
}
