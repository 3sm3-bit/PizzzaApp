package com.pizzza.pizzzaapp.core.navigation

import kotlinx.serialization.Serializable



@Serializable
sealed class ScreenInitNav {

    @Serializable
    object Splash : ScreenInitNav()

    @Serializable
    object Login : ScreenInitNav()

    @Serializable
    object Register : ScreenInitNav()

    @Serializable
    object ClientHome : ScreenInitNav()

    @Serializable
    object OrderSummary : ScreenInitNav()

    @Serializable
    object OrderDetail : ScreenInitNav()


    @Serializable
    data class AddressSelection(val fromRegister: Boolean = false) : ScreenInitNav()

    @Serializable
    object Monitor : ScreenInitNav()

    @Serializable
    data class PaymentWebView(val url: String) : ScreenInitNav()

}


