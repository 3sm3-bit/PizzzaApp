package com.pizzza.pizzzaapp.repository.network.model

import com.pizzza.pizzzaapp.model.UserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("user")
    val user: UserResponse? = null,
    @SerialName("userValid")
    val userValid: UserResponse? = null,
    @SerialName("accessToken")
    val accessToken: String? = "",
    @SerialName("token")
    val token: String? = "",
    @SerialName("refreshToken")
    val refreshToken: String? = ""
){
    val finalUser: UserResponse
        get() = user ?: userValid ?: UserResponse()

    val finalToken: String
        get() = accessToken?.takeIf { it.isNotBlank() } ?: token ?: ""

    fun toUserModel() = UserModel(
        uid = finalUser.uid ?: "",
        nameUser = finalUser.nameUser ?: "",
        names = finalUser.names ?: "",
        lastName = finalUser.lastName ?: "",
        document = finalUser.document ?: "",
        email = finalUser.email ?: "",
        phone = finalUser.phone ?: "",
        address = finalUser.address ?: "",
        rol = finalUser.rol ?: "CLIENTE",
        area = finalUser.area ?: "1",
        longitude = finalUser.longitude ?: "",
        latitude = finalUser.latitude ?: "",
        token = finalToken,
        refreshToken = refreshToken ?: ""
    )
}
