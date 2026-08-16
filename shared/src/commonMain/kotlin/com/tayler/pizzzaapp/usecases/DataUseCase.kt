package com.tayler.pizzzaapp.usecases

import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.usecases.network.IDataNetwork


class DataUseCase(private val iDataNetwork: IDataNetwork) {

    suspend fun loadOrder() = iDataNetwork.loadOrder()

    suspend fun loadParentOrder() = iDataNetwork.loadParentOrder()

    suspend fun updateOrder(data : ParentOrderModel) = iDataNetwork.updateOrder(data)

}