package com.pizzza.pizzzaapp.repository.db.manager

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor
import com.pizzza.pizzzaapp.repository.db.dao.ParentOrderDao
import com.pizzza.pizzzaapp.repository.db.dao.ProductDao
import com.pizzza.pizzzaapp.repository.db.dao.UserDao
import com.pizzza.pizzzaapp.repository.db.entity.ParentOrderEntity
import com.pizzza.pizzzaapp.repository.db.entity.ProductEntity
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity

@Database(entities = [ParentOrderEntity::class, ProductEntity::class, UserEntity::class], version = 4)
@ConstructedBy(AppDataBaseConstructor::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun parentOrderDao(): ParentOrderDao
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
}

// Room KMP constructor
expect object AppDataBaseConstructor : RoomDatabaseConstructor<AppDataBase>

