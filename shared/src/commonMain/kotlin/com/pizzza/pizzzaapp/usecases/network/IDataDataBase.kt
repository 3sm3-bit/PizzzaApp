package com.pizzza.pizzzaapp.usecases.network


import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.UserModel

interface IDataDataBase {

    suspend fun getProducts(): List<ProductModel>

    suspend fun deleteAll()

    suspend fun insertAll(list : List<ProductModel>)

    suspend fun saveUserLocal(user: UserModel)

    suspend fun getUserLocal(): UserModel?

    suspend fun logout()

}
