package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.entity.OrderModel
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.repository.exception.ErrorNetwork
import com.tayler.pizzzaapp.repository.model.loadOrder
import com.tayler.pizzzaapp.repository.model.loadParentOrder
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
        val response = apiService.updateParentOrder(data.toParentOrderRequest())
        // Borramos la base de datos local para que la próxima carga traiga los datos actualizados
        database.parentOrderDao().deleteAll()
        response
    }

    override suspend fun loadParentOrder(forceRefresh: Boolean): List<ParentOrderModel> {
        val dao = database.parentOrderDao()
        val localOrders = dao.getAll()
        
        println("DataNetwork: Iniciando loadParentOrder. forceRefresh=$forceRefresh, localCount=${localOrders.size}")

        if (localOrders.isNotEmpty() && !forceRefresh) {
            val models = localOrders.toModelList()
            // Si por alguna razón no tienen productos asociados, forzamos refresco para obtener data completa
            if (models.any { it.orders.isEmpty() }) {
                println("DataNetwork: Datos locales incompletos (sin productos). Forzando refresco de red.")
            } else {
                println("DataNetwork: Cargando desde DB Local con datos completos")
                return models
            }
        }

        if (!connectivityManager.isConnected()) {
            if (localOrders.isNotEmpty()) {
                println("DataNetwork: Sin conexión, usando DB Local (aunque falten productos)")
                return localOrders.toModelList()
            } else {
                println("DataNetwork: Sin conexión y sin datos locales. Lanzando error.")
                throw ErrorNetwork()
            }
        }

        return apiCall({
            println("DataNetwork: Llamando al servicio getParentOrder...")
            val response = apiService.getParentOrder()
            println("DataNetwork: Servicio respondió con ${response.size} pedidos")
            response
        }) { response ->
            // Guardar en DB para la próxima vez
            dao.deleteAll()
            dao.insertAll(response.toEntityListFromResponse())
            println("DataNetwork: Datos guardados en DB local")
            response.loadParentOrder()
        }
    }
}
