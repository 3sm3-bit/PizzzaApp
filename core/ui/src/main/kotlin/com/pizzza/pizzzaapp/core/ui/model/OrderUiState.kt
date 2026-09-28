package com.pizzza.pizzzaapp.core.ui.model

import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.model.HomeDataModel
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val products: List<ProductModel> = emptyList(),
    val pizzaProducts: List<ProductModel> = emptyList(),
    val extraProducts: List<ProductModel> = emptyList(),
    val promotionsProducts: List<ProductModel> = emptyList(),
    val deliveryProducts: List<ProductModel> = emptyList(),
    val branches: List<BranchModel> = emptyList(),
    val selectedOrder: ParentOrderModel? = null,
    val selectedProduct: ProductModel? = null,
    val cart: List<OrderItem> = emptyList(),
    val receptionMode: String = "DELIVERY",
    val selectedDeliveryProduct: ProductModel? = null,
    val deliveryAddress: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val initialTab: Int = 0,
    val branchId: String = "1",
    val ordersLoaded: Boolean = false,
    val pendingOrderUid: String = ""
)

fun OrderUiState.withHomeData(homeData: HomeDataModel) = copy(
    products = homeData.products,
    pizzaProducts = homeData.pizzaProducts,
    extraProducts = homeData.extraProducts,
    promotionsProducts = homeData.promotionsProducts,
    deliveryProducts = homeData.deliveryProducts,
    branches = if (homeData.branches.isNotEmpty()) homeData.branches else branches,
    branchId = if (homeData.branches.isNotEmpty()) homeData.defaultBranchId else branchId
)
