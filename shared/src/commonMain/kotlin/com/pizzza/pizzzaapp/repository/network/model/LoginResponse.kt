package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val userValid: UserResponse,
    val token: String
)
