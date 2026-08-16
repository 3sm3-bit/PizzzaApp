package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.entity.OrderModel
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.repository.exception.ErrorNetwork
import com.tayler.pizzzaapp.repository.model.OrderResponse.Companion.loadOrder
import com.tayler.pizzzaapp.repository.model.ParentOrderResponse.Companion.loadParentOrder
import com.tayler.pizzzaapp.usecases.network.IDataNetwork
import com.tayler.pizzzaapp.utils.ConnectivityManager

class DataNetwork(
    private val apiService: KmmService,
    private val connectivityManager: ConnectivityManager
) : IDataNetwork {

    override suspend fun loadOrder(): List<OrderModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.getOrder()
    }) {
        it.loadOrder()
    }

    override suspend fun updateOrder(data: ParentOrderModel): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.updateParentOrder(data.toParentOrderRequest())
    }

    override suspend fun loadParentOrder(): List<ParentOrderModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.getParentOrder()
    }) {
        it.loadParentOrder()
    }
}
