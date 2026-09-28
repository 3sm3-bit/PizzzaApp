package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmOrderRequest(
    @SerialName("state")
    val state: String = "CONFIRMADO",
    @SerialName("statePay")
    val statePay: String = "PAGADO"
)
