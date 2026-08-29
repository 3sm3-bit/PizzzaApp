package com.tayler.pizzzaapp.ui.orders

import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.model.BranchModel

data class OrderItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val product: ProductModel,
    val quantity: Int,
    val typeDough: String = "TRADICIONAL",
    val cheeseFilledCrust: Boolean = false,
    val note: String = ""
)

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val filteredOrders: List<ParentOrderModel> = emptyList(),
    val selectedFilter: String = "TODOS",
    val countConfirmado: Int = 0,
    val countListo: Int = 0,
    val selectedOrder: ParentOrderModel? = null,
    val products: List<ProductModel> = emptyList(),
    val selectedProduct: ProductModel? = null,
    val branches: List<BranchModel> = emptyList(),
    val selectedBranch: BranchModel? = null,
    val notificationsEnabled: Boolean = false,
    val selectedCategory: String = "Pizza",
    val cart: List<OrderItem> = emptyList(),
    val pizzaProducts: List<ProductModel> = emptyList(),
    val extraProducts: List<ProductModel> = emptyList(),
    val deliveryProducts: List<ProductModel> = emptyList(),
    val receptionMode: String = "RECOJO", // "RECOJO" or "DELIVERY"
    val selectedDeliveryProduct: ProductModel? = null,
    val deliveryAddress: String = ""
)
