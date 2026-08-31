package com.pizzza.pizzzaapp.ui.orders

import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val products: List<ProductModel> = emptyList(),
    val pizzaProducts: List<ProductModel> = emptyList(),
    val extraProducts: List<ProductModel> = emptyList(),
    val deliveryProducts: List<ProductModel> = emptyList(),
    val branches: List<com.pizzza.pizzzaapp.model.BranchModel> = emptyList(),
    val selectedOrder: ParentOrderModel? = null,
    val selectedProduct: ProductModel? = null,
    val cart: List<OrderItem> = emptyList(),
    val notificationsEnabled: Boolean = false,
    val receptionMode: String = "DELIVERY",
    val selectedDeliveryProduct: ProductModel? = null,
    val deliveryAddress: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val initialTab: Int = 0,
    val ordersLoaded: Boolean = false
)
