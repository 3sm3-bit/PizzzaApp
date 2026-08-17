package com.tayler.pizzzaapp.manager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_orders")
data class ParentOrderEntity(
    @PrimaryKey val uid: String,
    val nameClient: String,
    val description: String,
    val price: String,
    val phone: String,
    val date: String,
    val state: String
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

@Database(entities = [ParentOrderEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun parentOrderDao(): ParentOrderDao
}
