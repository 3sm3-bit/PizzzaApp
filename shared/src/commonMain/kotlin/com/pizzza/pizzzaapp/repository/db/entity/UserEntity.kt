package com.pizzza.pizzzaapp.repository.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pizzza.pizzzaapp.model.UserModel

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val uid: String,
    val nameUser: String,
    val names: String,
    val lastName: String,
    val document: String,
    val email: String,
    val phone: String,
    val address: String,
    val rol: String,
    val area: String,
    val longitude: String,
    val latitude: String,
    val token: String
){

    fun toModel() = UserModel(
        uid,
        nameUser,
        names,
        lastName,
        document,
        email,
        phone,
        address,
        rol,
        area,
        longitude,
        latitude,
        token
    )
    companion object{
        fun toEntity(user : UserModel) = UserEntity(
            user.uid,
            user.nameUser,
            user.names,
            user.lastName,
            user.document,
            user.email,
            user.phone,
            user.address,
            user.rol,
            user.area,
            user.longitude,
            user.latitude,
            user.token
        )
    }
}
