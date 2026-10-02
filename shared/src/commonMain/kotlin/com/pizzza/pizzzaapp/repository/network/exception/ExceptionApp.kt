package com.pizzza.pizzzaapp.repository.network.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class CompleteErrorModel(
    @SerialName("errorCode")
    val errorCode: Int? = null,
    @SerialName("code")
    val code: Int? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("errorMessage")
    val errorMessage: String? = null,
    @SerialName("errorMessageDetail")
    val errorMessageDetail: JsonElement? = null
) {
    fun extractMainMessage(): String? {
        return errorMessage?.takeIf { it.isNotBlank() }
    }

    fun extractDetails(): List<String> {
        return parseJsonElementToList(errorMessageDetail)
    }

    private fun parseJsonElementToList(element: JsonElement?): List<String> {
        if (element == null) return emptyList()
        return try {
            when (element) {
                is JsonArray -> element.mapNotNull {
                    if (it is JsonPrimitive) it.content else it.toString()
                }.filter { it.isNotBlank() && it != "null" }

                is JsonPrimitive -> {
                    val content = element.content
                    if (content.isNotBlank() && content != "null") listOf(content) else emptyList()
                }

                is JsonObject -> {
                    element.values.mapNotNull {
                        if (it is JsonPrimitive) it.content else null
                    }.filter { it.isNotBlank() && it != "null" }
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}

class UiTayApiException(
    val code: Int,
    val title: String,
    val messageApi: String
) : Exception(messageApi)

class UnAuthorizedException : Exception("Sesión expirada")
class ErrorNetwork : Exception("No hay conexión a internet")

fun Throwable.toAppException(): Exception {
    val message = this.message ?: "Error desconocido"
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
