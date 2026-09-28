package com.pizzza.pizzzaapp.model

data class HomeDataModel(
    val products: List<ProductModel>,
    val pizzaProducts: List<ProductModel>,
    val extraProducts: List<ProductModel>,
    val promotionsProducts: List<ProductModel>,
    val deliveryProducts: List<ProductModel>,
    val branches: List<BranchModel>,
    val defaultBranchId: String
)
