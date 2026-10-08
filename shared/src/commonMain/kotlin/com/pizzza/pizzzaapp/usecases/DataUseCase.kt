package com.pizzza.pizzzaapp.usecases

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.HomeDataModel
import com.pizzza.pizzzaapp.model.UserModel
import com.pizzza.pizzzaapp.repository.network.model.LoginRequest
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.repository.network.model.UserResponse
import com.pizzza.pizzzaapp.repository.network.model.ConfirmOrderRequest
import com.pizzza.pizzzaapp.usecases.network.IDataDataBase
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork

class DataUseCase(private val iDataNetwork: IDataNetwork, private val iDataDBNetwork: IDataDataBase) {

    @Throws(Exception::class)
    suspend fun loadParentOrder(userId: String) = iDataNetwork.loadParentOrder(userId)

    @Throws(Exception::class)
    suspend fun getOrderById(orderId: String) = iDataNetwork.getOrderById(orderId)

    @Throws(Exception::class)
    suspend fun deleteGeneralOrder(orderId: String) = iDataNetwork.deleteGeneralOrder(orderId)

    @Throws(Exception::class)
    suspend fun syncProducts(): List<ProductModel> {
        val response = iDataNetwork.syncProducts()
        if (response.isNotEmpty()) {
            iDataDBNetwork.deleteAll()
            iDataDBNetwork.insertAll(response)
        }
        return response
    }

    @Throws(Exception::class)
    suspend fun getProducts(): List<ProductModel> {
        return syncProducts()
    }

    @Throws(Exception::class)
    suspend fun getProductsLocal() = iDataDBNetwork.getProducts()

    @Throws(Exception::class)
    suspend fun loadHomeDataFromLocal(): HomeDataModel {
        var products = iDataDBNetwork.getProducts()
        if (products.isEmpty()) {
            products = try { syncProducts() } catch (e: Exception) { emptyList() }
        }
        val branches = try { iDataNetwork.getBranches() } catch (e: Exception) { emptyList() }
        val defaultBranchId = branches.firstOrNull()?.identifier ?: "1"

        return HomeDataModel(
            products = products,
            pizzaProducts = products.filter { it.type == "1" },
            extraProducts = products.filter { it.type == "2" || it.type == "3" },
            promotionsProducts = products.filter { it.type == "4" },
            deliveryProducts = products.filter { it.type == "5" },
            branches = branches,
            defaultBranchId = defaultBranchId
        )
    }

    @Throws(Exception::class)
    suspend fun createOrder(data: List<OrderResponse>) = iDataNetwork.createOrder(data)

    @Throws(Exception::class)
    suspend fun createOrderMobile(data: List<OrderResponse>) = iDataNetwork.createOrderMobile(data)

    @Throws(Exception::class)
    suspend fun confirmOrderMobile(ordenGeneralUid: String, request: ConfirmOrderRequest = ConfirmOrderRequest()) =
        iDataNetwork.confirmOrderMobile(ordenGeneralUid, request)

    @Throws(Exception::class)
    suspend fun registerUser(data: UserResponse) = iDataNetwork.registerUser(data)

    @Throws(Exception::class)
    suspend fun deleteUser(userId: String): String {
        val response = iDataNetwork.deleteUser(userId)
        iDataDBNetwork.logout()
        return response
    }

    @Throws(Exception::class)
    suspend fun login(data: LoginRequest) = iDataNetwork.login(data)

    @Throws(Exception::class)
    suspend fun refreshToken(refreshToken: String) = iDataNetwork.refreshToken(refreshToken)

    @Throws(Exception::class)
    suspend fun saveUserLocal(user: UserModel) = iDataDBNetwork.saveUserLocal(user)

    @Throws(Exception::class)
    suspend fun getUserLocal() = iDataDBNetwork.getUserLocal()

    @Throws(Exception::class)
    suspend fun checkSessionAndRefreshToken(): UserModel? {
        val localUser = iDataDBNetwork.getUserLocal() ?: return null
        if (localUser.token.isBlank()) return null
        return localUser
    }

    @Throws(Exception::class)
    suspend fun logout() {
        iDataDBNetwork.logout()
    }

    @Throws(Exception::class)
    suspend fun createPaymentSession(amount: Double, email: String, orderId: String) =
        iDataNetwork.createPaymentSession(amount, email, orderId)

    @Throws(Exception::class)
    suspend fun getBranch() = iDataNetwork.getBranches()
}
