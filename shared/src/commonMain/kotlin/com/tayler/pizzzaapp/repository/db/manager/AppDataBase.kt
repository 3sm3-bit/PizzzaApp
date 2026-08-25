package com.tayler.pizzzaapp.repository.db.manager

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor
import com.tayler.pizzzaapp.repository.db.dao.ParentOrderDao
import com.tayler.pizzzaapp.repository.db.dao.ProductDao
import com.tayler.pizzzaapp.repository.db.entity.ParentOrderEntity
import com.tayler.pizzzaapp.repository.db.entity.ProductEntity

@Database(entities = [ParentOrderEntity::class, ProductEntity::class], version = 2)
@ConstructedBy(AppDataBaseConstructor::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun parentOrderDao(): ParentOrderDao
    abstract fun productDao(): ProductDao
}

// Room KMP constructor
expect object AppDataBaseConstructor : RoomDatabaseConstructor<AppDataBase>

