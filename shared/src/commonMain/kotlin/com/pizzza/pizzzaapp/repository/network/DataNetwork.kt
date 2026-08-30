package com.pizzza.pizzzaapp.repository.network

import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.repository.network.exception.ErrorNetwork
import com.pizzza.pizzzaapp.repository.network.model.loadOrder
import com.pizzza.pizzzaapp.repository.network.model.loadParentOrder
import com.pizzza.pizzzaapp.repository.network.model.toModelList
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.toResponse
import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork
import com.pizzza.pizzzaapp.repository.utils.ConnectivityManager
import com.pizzza.pizzzaapp.repository.db.manager.AppDataBase
import com.pizzza.pizzzaapp.repository.db.toEntity
import com.pizzza.pizzzaapp.repository.db.toEntityListFromResponse
import com.pizzza.pizzzaapp.repository.db.toProductEntityList
import com.pizzza.pizzzaapp.repository.db.toProductModelList
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.db.toModelList as toModelListFromDb

class DataNetwork(
    private val apiService: KmmService,
    private val connectivityManager: ConnectivityManager,
    private val database: AppDataBase
) : IDataNetwork {

    override suspend fun getProducts(): List<ProductModel> {
        val dao = database.productDao()
        val localProducts = dao.getAll()
        println("$TAG_PIZZZA: DataNetwork: Obteniendo ${localProducts.size} productos desde DB Local")
        return localProducts.toProductModelList()
    }

    override suspend fun syncProducts(): List<ProductModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Sincronizando productos desde el servicio...")
        val response = apiService.getProducts()
        val models = response.toModelList()
        
        val dao = database.productDao()
        dao.deleteAll()
        dao.insertAll(models.toProductEntityList())
        println("$TAG_PIZZZA: DataNetwork: Sincronización completa. ${models.size} productos guardados.")
        models
    }) { it }

    override suspend fun getBranches(): List<BranchModel> = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Cargando sucursales desde el servicio...")
        apiService.getBranches()
    }) { response ->
        response.toModelList()
    }

    override suspend fun createOrder(data: List<OrderResponse>): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Enviando lista de pedidos (${data.size} items)...")
        apiService.createOrder(data)
    }

    override suspend fun loadParentOrder(forceRefresh: Boolean): List<ParentOrderModel> {
        val dao = database.parentOrderDao()
        val localOrders = dao.getAll()
        
        println("$TAG_PIZZZA: DataNetwork: Iniciando loadParentOrder. forceRefresh=$forceRefresh, localCount=${localOrders.size}")

        if (localOrders.isNotEmpty() && !forceRefresh) {
            val models = localOrders.toModelListFromDb()
            // Si por alguna razón no tienen productos asociados, forzamos refresco para obtener data completa
            if (models.any { it.orders.isEmpty() }) {
                println("$TAG_PIZZZA: DataNetwork: Datos locales incompletos (sin productos). Forzando refresco de red.")
            } else {
                println("$TAG_PIZZZA: DataNetwork: Cargando desde DB Local con datos completos")
                return models
            }
        }

        if (!connectivityManager.isConnected()) {
            if (localOrders.isNotEmpty()) {
                println("$TAG_PIZZZA: DataNetwork: Sin conexión, usando DB Local (aunque falten productos)")
                return localOrders.toModelListFromDb()
            } else {
                println("$TAG_PIZZZA: DataNetwork: Sin conexión y sin datos locales. Lanzando error.")
                throw ErrorNetwork()
            }
        }

        return apiCall({
            println("$TAG_PIZZZA: DataNetwork: Llamando al servicio getParentOrder...")
            val response = apiService.getParentOrder()
            println("$TAG_PIZZZA: DataNetwork: Servicio respondió con ${response.size} pedidos")
            response
        }) { response ->
            // Guardar en DB para la próxima vez
            dao.deleteAll()
            dao.insertAll(response.toEntityListFromResponse())
            println("$TAG_PIZZZA: DataNetwork: Datos guardados en DB local")
            response.loadParentOrder()
        }
    }

    override suspend fun registerUser(data: UserResponse): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Registrando usuario ${data.email}...")
        apiService.registerUser(data)
    }

    override suspend fun login(data: com.pizzza.pizzzaapp.repository.network.model.LoginRequest): com.pizzza.pizzzaapp.repository.network.model.LoginResponse = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        apiService.login(data)
    }

    override suspend fun saveUserLocal(user: com.pizzza.pizzzaapp.repository.db.entity.UserEntity) {
        database.userDao().logout()
        database.userDao().insertUser(user)
    }

    override suspend fun getUserLocal(): com.pizzza.pizzzaapp.repository.db.entity.UserEntity? {
        return database.userDao().getUser()
    }

    override suspend fun logout() {
        database.userDao().logout()
    }
}
