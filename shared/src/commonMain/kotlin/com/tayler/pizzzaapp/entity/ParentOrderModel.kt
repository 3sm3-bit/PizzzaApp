package com.tayler.pizzzaapp.entity

import com.tayler.pizzzaapp.repository.model.ParentOrderResponse
import kotlin.String

data class ParentOrderModel(
    val uid: String,
    val nameClient: String,
    val description: String,
    val price: String,
    val phone: String,
    val date: String,
    val state: String,
    val address: String,
    val reception: String,
    val orders: List<OrderModel>
) {
    fun toParentOrderRequest() =
        ParentOrderResponse(
            uid = uid,
            nameClient = nameClient,
            description = description,
            price = price,
            phone = phone,
            date = date,
            state = state,
            address = address,
            reception = reception,
            orders = emptyList() // Or map it back if needed, but usually for requests we might not need all orders
        )

}