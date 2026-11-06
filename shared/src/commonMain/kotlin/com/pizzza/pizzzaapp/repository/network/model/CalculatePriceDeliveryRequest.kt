package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CalculatePriceDeliveryRequest(
    @SerialName("branchId")
    val branchId: String,
    @SerialName("latitude")
    val latitude: String,
    @SerialName("longitude")
    val longitude: String
)
