package com.pizzza.pizzzaapp.repository.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val amount: Long,
    val currency: String,
    val orderId: String,
    val customerEmail: String,
    val successUrl: String,
    val cancelUrl: String,
    val appSuccessUrl: String,
    val appCancelUrl: String
)

@Serializable
data class PaymentResponse(
    val ok: Boolean,
    val url: String? = null,
    val sessionId: String? = null,
    val error: String? = null
)
