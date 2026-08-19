package com.tayler.pizzzaapp.manager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.ConstructedBy
import androidx.room.RoomDatabaseConstructor

@Entity(tableName = "parent_orders")
data class ParentOrderEntity(
    @PrimaryKey val uid: String,
    val nameClient: String,
    val description: String,
    val price: String,
    val phone: String,
    val date: String,
    val state: String,
    val address: String,
    val reception: String
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val uid: String,
    val nameProduct: String,
    val type: String,
    val price: String,
    val tamanio: String,
    val description: String,
    val priceChosse: String,
    val currency: String,
    val currencySymbol: String,
    val state: Boolean
)

@Dao
interface ParentOrderDao {
    @Query("SELECT * FROM parent_orders")
    suspend fun getAll(): List<ParentOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<ParentOrderEntity>)

    @Query("DELETE FROM parent_orders")
    suspend fun deleteAll()
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}

@Database(entities = [ParentOrderEntity::class, ProductEntity::class], version = 2)
@ConstructedBy(AppDataBaseConstructor::class)
abstract class AppDataBase : RoomDatabase() {
    abstract fun parentOrderDao(): ParentOrderDao
    abstract fun productDao(): ProductDao
}

// Room KMP constructor
expect object AppDataBaseConstructor : RoomDatabaseConstructor<AppDataBase>

