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
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork
import com.pizzza.pizzzaapp.repository.utils.ConnectivityManager
import com.pizzza.pizzzaapp.repository.db.manager.AppDataBase
import com.pizzza.pizzzaapp.repository.db.toProductEntityList
import com.pizzza.pizzzaapp.repository.db.toProductModelList
import com.pizzza.pizzzaapp.repository.network.model.UserResponse

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

    override suspend fun createOrder(data: List<OrderResponse>): String = apiCall {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Enviando lista de pedidos (${data.size} items)...")
        apiService.createOrder(data)
    }

    override suspend fun loadParentOrder(userId: String): List<ParentOrderModel> {
        if (!connectivityManager.isConnected()) throw ErrorNetwork()

        println("$TAG_PIZZZA: DataNetwork: Iniciando loadParentOrder para usuario $userId desde red")

        return apiCall({
            println("$TAG_PIZZZA: DataNetwork: Llamando al servicio getParentOrder($userId)...")
            val response = apiService.getParentOrder(userId)
            println("$TAG_PIZZZA: DataNetwork: Servicio respondió con ${response.size} pedidos")
            response
        }) { response ->
            response.loadParentOrder()
        }
    }

    override suspend fun getOrderById(orderId: String): ParentOrderModel = apiCall({
        if (!connectivityManager.isConnected()) throw ErrorNetwork()
        println("$TAG_PIZZZA: DataNetwork: Obteniendo pedido $orderId...")
        apiService.getOrderById(orderId)
    }) { response ->
        // Solo hay uno
        listOf(response).loadParentOrder().first()
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
