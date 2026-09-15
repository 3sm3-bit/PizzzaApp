package com.pizzza.pizzzaapp.repository.db

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.UserModel
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity
import com.pizzza.pizzzaapp.repository.db.manager.AppDataBase
import com.pizzza.pizzzaapp.usecases.network.IDataDataBase

class DataDataBase(
    private val database: AppDataBase
) : IDataDataBase {

    override suspend fun getProducts(): List<ProductModel> {
        return database.productDao().getAll().toProductModelList()
    }

    override suspend fun deleteAll() {
        database.productDao().deleteAll()
    }

    override suspend fun insertAll(list: List<ProductModel>) {
        database.productDao().insertAll(list.toProductEntityList())
    }

    override suspend fun saveUserLocal(user: UserModel) {
        database.userDao().insertUser(UserEntity.toEntity(user))
    }

    override suspend fun getUserLocal(): UserModel? {
        return database.userDao().getUser()?.toModel()
    }

    override suspend fun logout() {
        database.userDao().logout()
    }

}
