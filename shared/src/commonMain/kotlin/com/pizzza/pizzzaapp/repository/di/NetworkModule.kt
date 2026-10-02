package com.pizzza.pizzzaapp.repository.di

import com.pizzza.pizzzaapp.repository.network.KmmService
import com.pizzza.pizzzaapp.repository.network.exception.CompleteErrorModel
import com.pizzza.pizzzaapp.repository.network.exception.ErrorNetwork
import com.pizzza.pizzzaapp.repository.network.exception.UiTayApiException
import com.pizzza.pizzzaapp.repository.network.manager.InstantSerializer
import com.pizzza.pizzzaapp.repository.utils.ConnectivityManager
import com.pizzza.pizzzaapp.requestLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.core.qualifier.named
import org.koin.dsl.module

val jsonLenient = Json { ignoreUnknownKeys = true }

val networkModule = module {
    single(named("httpClient")) {
        val connectivityManager: ConnectivityManager = get()
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        serializersModule = SerializersModule {
                            contextual(Instant::class, InstantSerializer)
                        }
                    },
                )
            }

            defaultRequest {
                if (!connectivityManager.isConnected()) throw ErrorNetwork()
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (!response.status.isSuccess()) {
                        val statusCode = response.status.value
                        val errorText = try {
                            response.bodyAsText()
                        } catch (_: Exception) {
                            ""
                        }

                        val errorModel = try {
                            jsonLenient.decodeFromString<CompleteErrorModel>(errorText)
                        } catch (_: Exception) {
                            null
                        }

                        val titleFromApi = errorModel?.title?.takeIf { it.isNotBlank() }
                        val mainMsgFromApi = errorModel?.extractMainMessage()
                        val detailsListFromApi = errorModel?.extractDetails() ?: emptyList()

                        val (finalTitle, finalMessage) = if (statusCode in 500..599) {
                            "Ocurrió un error" to "Estamos sufriendo inconvenientes en los servicios, inténtelo más tarde."
                        } else {
                            val defaultTitleForStatus = when (statusCode) {
                                400 -> "Solicitud Incorrecta"
                                401 -> "Sesión Expirada"
                                403 -> "Acceso Denegado"
                                404 -> "Recurso no Encontrado"
                                409 -> "Conflicto en la Solicitud"
                                else -> "Error en la Solicitud"
                            }

                            val defaultMessageForStatus = when (statusCode) {
                                400 -> "Los datos enviados son incorrectos."
                                401 -> "Tu sesión ha expirado. Por favor, vuelve a iniciar sesión."
                                403 -> "No tienes permisos para realizar esta acción."
                                404 -> "No se encontró el recurso solicitado."
                                409 -> "Ocurrió un conflicto al procesar la solicitud."
                                else -> "Ocurrió un error inesperado."
                            }

                            val title = titleFromApi ?: defaultTitleForStatus

                            val uniqueDetails = detailsListFromApi
                                .filter { it.isNotBlank() && it != mainMsgFromApi && it != title }
                                .distinct()

                            val detailsFormatted = if (uniqueDetails.isNotEmpty()) {
                                uniqueDetails.joinToString("\n• ")
                            } else {
                                ""
                            }

                            val message = when {
                                !mainMsgFromApi.isNullOrBlank() && detailsFormatted.isNotBlank() -> {
                                    if (mainMsgFromApi in uniqueDetails) {
                                        "• $detailsFormatted"
                                    } else {
                                        "$mainMsgFromApi:\n• $detailsFormatted"
                                    }
                                }
                                !mainMsgFromApi.isNullOrBlank() -> mainMsgFromApi
                                detailsFormatted.isNotBlank() -> "• $detailsFormatted"
                                else -> defaultMessageForStatus
                            }

                            title to message
                        }

                        val isUnauthorized = statusCode == 401 ||
                                (statusCode == 403 && (
                                    finalMessage.contains("token", ignoreCase = true) ||
                                    finalMessage.contains("expirad", ignoreCase = true) ||
                                    finalMessage.contains("sesion", ignoreCase = true) ||
                                    finalMessage.contains("sesión", ignoreCase = true)
                                )) ||
                                (errorModel?.errorCode == 17 && (
                                    finalMessage.contains("token", ignoreCase = true) ||
                                    finalTitle.contains("token", ignoreCase = true) ||
                                    finalMessage.contains("expirad", ignoreCase = true)
                                ))

                        if (isUnauthorized) {
                            throw com.pizzza.pizzzaapp.repository.network.exception.UnAuthorizedException()
                        } else {
                            throw UiTayApiException(
                                code = statusCode,
                                title = finalTitle,
                                messageApi = finalMessage
                            )
                        }
                    }
                }
            }

            install(Logging) {
                logger = requestLogger
                level = LogLevel.ALL
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 15_000
                socketTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
            }
        }
    }

    single { KmmService(get(named("httpClient"))) }
}

