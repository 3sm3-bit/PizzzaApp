package com.pizzza.pizzzaapp.ui.orders

import com.pizzza.pizzzaapp.model.ProductModel
import java.util.UUID

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val product: ProductModel,
    val quantity: Int = 1,
    val typeDough: String = "TRADICIONAL",
    val cheeseFilledCrust: Boolean = false,
    val note: String = ""
)
