package com.pizzza.pizzzaapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Login

@Serializable
object Register

@Serializable
object ClientHome

@Serializable
object CartDetail

@Serializable
object OrderSummary

@Serializable
object OrderDetail

@Serializable
object AddressSelection

@Serializable
object Monitor

@Serializable
data class PaymentWebView(val url: String)
