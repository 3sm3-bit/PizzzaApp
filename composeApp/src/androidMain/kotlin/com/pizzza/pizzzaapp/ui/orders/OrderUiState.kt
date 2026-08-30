package com.pizzza.pizzzaapp.ui.orders

import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val filteredOrders: List<ParentOrderModel> = emptyList(),
    val countConfirmado: Int = 0,
    val countListo: Int = 0,
    val selectedFilter: String = "TODOS",
    val products: List<ProductModel> = emptyList(),
    val pizzaProducts: List<ProductModel> = emptyList(),
    val extraProducts: List<ProductModel> = emptyList(),
    val deliveryProducts: List<ProductModel> = emptyList(),
    val branches: List<BranchModel> = emptyList(),
    val selectedOrder: ParentOrderModel? = null,
    val selectedBranch: BranchModel? = null,
    val selectedProduct: ProductModel? = null,
    val selectedCategory: String = "TODOS",
    val cart: List<OrderItem> = emptyList(),
    val notificationsEnabled: Boolean = false,
    val receptionMode: String = "RECOJO",
    val selectedDeliveryProduct: ProductModel? = null,
    val deliveryAddress: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val initialTab: Int = 0
)
