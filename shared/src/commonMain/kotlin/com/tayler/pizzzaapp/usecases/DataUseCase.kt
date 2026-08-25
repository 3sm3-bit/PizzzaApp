package com.tayler.pizzzaapp.usecases

import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.usecases.network.IDataNetwork


class DataUseCase(private val iDataNetwork: IDataNetwork) {

    suspend fun loadOrder() = iDataNetwork.loadOrder()

    suspend fun loadParentOrder(forceRefresh: Boolean = false) = iDataNetwork.loadParentOrder(forceRefresh)

    suspend fun updateOrder(data: ParentOrderModel) = iDataNetwork.updateOrder(data)

    suspend fun getProducts() = iDataNetwork.getProducts()

}