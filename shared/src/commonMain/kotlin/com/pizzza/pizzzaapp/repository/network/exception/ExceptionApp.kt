package com.pizzza.pizzzaapp.repository.network.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompleteErrorModel(
    @SerialName("code")
    val code: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("errorMessage")
    val errorMessage: String? = null
)

class UiTayApiException(
    val code: Int,
    val title: String,
    val messageApi: String
) : Exception(messageApi)

class UnAuthorizedException : Exception("Sesión expirada")
class GenericException : Exception("Ocurrió un error inesperado")
class ErrorNetwork : Exception("No hay conexión a internet")

fun Throwable.toAppException(): Exception {
    val message = this.message ?: "Error desconocido"
    
    // Mapeo de errores de red comunes de Ktor/Plataforma
    if (message.contains("UnresolvedAddressException", ignoreCase = true) || 
        message.contains("ConnectException", ignoreCase = true) ||
        message.contains("socket timeout", ignoreCase = true)) {
        return ErrorNetwork()
    }

    return when (this) {
        is UiTayApiException -> this
        is UnAuthorizedException -> this
        is ErrorNetwork -> this
        else -> UiTayApiException(
            code = 0,
            title = "Error",
            messageApi = message
        )
    }
}
