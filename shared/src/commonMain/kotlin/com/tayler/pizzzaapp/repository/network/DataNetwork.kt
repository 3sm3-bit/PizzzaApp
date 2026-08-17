package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.entity.OrderModel
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.repository.exception.ErrorNetwork
import com.tayler.pizzzaapp.repository.model.OrderResponse.Companion.loadOrder
import com.tayler.pizzzaapp.repository.model.ParentOrderResponse.Companion.loadParentOrder
import com.tayler.pizzzaapp.usecases.network.IDataNetwork
import com.tayler.pizzzaapp.utils.ConnectivityManager
import com.tayler.pizzzaapp.manager.db.AppDataBase
import com.tayler.pizzzaapp.manager.db.toEntityListFromResponse
import com.tayler.pizzzaapp.manager.db.toModelList

class DataNetwork(
    private val apiService: KmmService,
    private val connectivityManager: ConnectivityManager,
    private val database: AppDataBase
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

    override suspend fun loadParentOrder(forceRefresh: Boolean): List<ParentOrderModel> {
        val dao = database.parentOrderDao()
        val localOrders = dao.getAll()

        if (localOrders.isNotEmpty() && !forceRefresh) {
            println("DataNetwork: Loading from Local DB")
            return localOrders.toModelList()
        }

        if (!connectivityManager.isConnected() && localOrders.isNotEmpty()) {
            println("DataNetwork: No connection, falling back to Local DB")
            return localOrders.toModelList()
        }

        return apiCall({
            if (!connectivityManager.isConnected()) throw ErrorNetwork()
            println("DataNetwork: Loading from Service")
            apiService.getParentOrder()
        }) { response ->
            // Guardar en DB para la próxima vez
            dao.deleteAll()
            dao.insertAll(response.toEntityListFromResponse())
            response.loadParentOrder()
        }
    }
}
