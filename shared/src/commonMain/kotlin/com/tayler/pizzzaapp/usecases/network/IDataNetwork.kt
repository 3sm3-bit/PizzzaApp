package com.tayler.pizzzaapp.usecases.network

import com.tayler.pizzzaapp.model.OrderModel
import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.model.ProductModel

interface IDataNetwork {

    suspend fun loadOrder(): List<OrderModel>

    suspend fun updateOrder(data: ParentOrderModel): String

    suspend fun loadParentOrder(forceRefresh: Boolean = false): List<ParentOrderModel>

    suspend fun getProducts(): List<ProductModel>

}
