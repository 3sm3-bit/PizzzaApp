package com.tayler.pizzzaapp.usecases

import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.model.BranchModel
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.repository.network.model.UserResponse
import com.tayler.pizzzaapp.usecases.network.IDataNetwork


class DataUseCase(private val iDataNetwork: IDataNetwork) {

    suspend fun loadOrder() = iDataNetwork.loadOrder()

    suspend fun loadParentOrder(forceRefresh: Boolean = false) = iDataNetwork.loadParentOrder(forceRefresh)

    suspend fun updateOrder(data: ParentOrderModel) = iDataNetwork.updateOrder(data)

    suspend fun syncProducts() = iDataNetwork.syncProducts()

    suspend fun getProducts() = iDataNetwork.getProducts()

    suspend fun updateProduct(data: ProductModel) = iDataNetwork.updateProduct(data)

    suspend fun getBranches() = iDataNetwork.getBranches()

    suspend fun updateBranch(data: BranchModel) = iDataNetwork.updateBranch(data)

    suspend fun registerUser(data: UserResponse) = iDataNetwork.registerUser(data)

    suspend fun login(data: com.tayler.pizzzaapp.repository.network.model.LoginRequest) = iDataNetwork.login(data)

    suspend fun saveUserLocal(user: com.tayler.pizzzaapp.repository.db.entity.UserEntity) = iDataNetwork.saveUserLocal(user)

    suspend fun getUserLocal() = iDataNetwork.getUserLocal()

    suspend fun logout() {
        iDataNetwork.logout()
    }

}