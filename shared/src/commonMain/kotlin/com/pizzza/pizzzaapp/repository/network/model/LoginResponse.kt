package com.pizzza.pizzzaapp.repository.network.model

import com.pizzza.pizzzaapp.model.UserModel
import com.pizzza.pizzzaapp.repository.db.entity.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("userValid")
    val userValid: UserResponse,
    @SerialName("token")
    val token: String
){
    fun toUserModel() = UserModel(
        uid = userValid.uid ?: "",
        nameUser = userValid.nameUser ?: "",
        names = userValid.names ?: "",
        lastName = userValid.lastName ?: "",
        document = userValid.document ?: "",
        email = userValid.email ?: "",
        phone = userValid.phone ?: "",
        address = userValid.address ?: "",
        rol = userValid.rol ?: "CLIENTE",
        area = userValid.area ?: "1",
        longitude = userValid.longitude ?: "",
        latitude = userValid.latitude ?: "",
        token = token
    )
}
