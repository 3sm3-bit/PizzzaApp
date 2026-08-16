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
    val state: String
) {
    fun toParentOrderRequest() =
        ParentOrderResponse(
            uid = uid,
            nameClient = nameClient,
            description = description,
            price = price,
            phone = phone,
            date = date,
            state = state
        )

}