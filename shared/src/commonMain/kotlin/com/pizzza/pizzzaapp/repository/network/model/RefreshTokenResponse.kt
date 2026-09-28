package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    @SerialName("token")
    val token: String? = "",
    @SerialName("refreshToken")
    val refreshToken: String? = ""
)
