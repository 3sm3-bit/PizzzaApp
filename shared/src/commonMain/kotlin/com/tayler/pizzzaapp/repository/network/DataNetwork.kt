package com.tayler.pizzzaapp.repository.network

import com.tayler.pizzzaapp.entity.OrderModel
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.entity.ProductModel
import com.tayler.pizzzaapp.repository.exception.ErrorNetwork
import com.tayler.pizzzaapp.repository.model.loadOrder
import com.tayler.pizzzaapp.repository.model.loadParentOrder
import com.tayler.pizzzaapp.repository.model.toModelList
import com.tayler.pizzzaapp.usecases.network.IDataNetwork
import com.tayler.pizzzaapp.utils.ConnectivityManager
import com.tayler.pizzzaapp.manager.db.AppDataBase
import com.tayler.pizzzaapp.manager.db.toEntity
import com.tayler.pizzzaapp.manager.db.toEntityListFromResponse
import com.tayler.pizzzaapp.manager.db.toProductEntityList
import com.tayler.pizzzaapp.manager.db.toProductModelList
import com.tayler.pizzzaapp.manager.db.toModelList as toModelListFromDb

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

    override suspend fun getProducts(): List<ProductModel> {
        val dao = database.productDao()
        val localProducts = dao.getAll()
        
        if (localProducts.isNotEmpty()) {
            println("DataNetwork: Cargando productos desde DB Local")
            return localProducts.toProductModelList()
        }

        return apiCall({
            if (!connectivityManager.isConnected()) throw ErrorNetwork()
            println("DataNetwork: Cargando productos desde el servicio...")
            apiService.getProducts()
        }) { response ->
            val models = response.toModelList()
            dao.deleteAll()
            dao.insertAll(models.toProductEntityList())
            println("DataNetwork: Productos guardados en DB local")
            models
        }
    }

    override suspend fun updateOrder(data: ParentOrderModel): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("DataNetwork: Actualizando pedido ${data.uid} a estado ${data.state}...")
        val response = apiService.updateParentOrder(data.toParentOrderRequest())
        
        // En lugar de borrar todo, actualizamos o insertamos el pedido específico en la DB local
        // Nota: Como la entidad no guarda los productos individuales, la próxima carga
        // desde DB detectará que faltan productos y forzará un refresco completo de red.
        // Esto es correcto para mantener la integridad de los datos.
        database.parentOrderDao().insertAll(listOf(data.toEntity()))
        println("DataNetwork: Pedido actualizado en servidor y DB local")
        response
    }

    override suspend fun loadParentOrder(forceRefresh: Boolean): List<ParentOrderModel> {
        val dao = database.parentOrderDao()
        val localOrders = dao.getAll()
        
        println("DataNetwork: Iniciando loadParentOrder. forceRefresh=$forceRefresh, localCount=${localOrders.size}")

        if (localOrders.isNotEmpty() && !forceRefresh) {
            val models = localOrders.toModelListFromDb()
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
                return localOrders.toModelListFromDb()
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
