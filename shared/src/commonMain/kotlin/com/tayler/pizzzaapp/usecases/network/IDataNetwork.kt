package com.tayler.pizzzaapp.usecases.network

import com.tayler.pizzzaapp.model.OrderModel
import com.tayler.pizzzaapp.model.ParentOrderModel
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.model.BranchModel
import com.tayler.pizzzaapp.repository.network.model.UserResponse

interface IDataNetwork {

    suspend fun loadOrder(): List<OrderModel>

    suspend fun updateOrder(data: ParentOrderModel): String

    suspend fun loadParentOrder(forceRefresh: Boolean = false): List<ParentOrderModel>

    suspend fun syncProducts(): List<ProductModel>

    suspend fun getProducts(): List<ProductModel>

    suspend fun updateProduct(data: ProductModel): String

    suspend fun getBranches(): List<BranchModel>

    suspend fun updateBranch(data: BranchModel): String

    suspend fun registerUser(data: UserResponse): String

    suspend fun login(data: com.tayler.pizzzaapp.repository.network.model.LoginRequest): com.tayler.pizzzaapp.repository.network.model.LoginResponse

    suspend fun saveUserLocal(user: com.tayler.pizzzaapp.repository.db.entity.UserEntity)

    suspend fun getUserLocal(): com.tayler.pizzzaapp.repository.db.entity.UserEntity?

    suspend fun logout()

}
