package com.pizzza.pizzzaapp.repository.di

import com.pizzza.pizzzaapp.repository.network.KmmService
import com.pizzza.pizzzaapp.repository.network.WebSocketManager
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
import io.ktor.client.plugins.websocket.WebSockets
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ""
                        }

                        val errorModel = try {
                            jsonLenient.decodeFromString<CompleteErrorModel>(errorText)
                        } catch (e: Exception) {
                            null
                        }

                        val finalTitle = errorModel?.title?.takeIf { it.isNotBlank() } ?: "Error $statusCode"
                        val finalMessage = errorModel?.errorMessage?.takeIf { it.isNotBlank() }
                            ?: errorText.takeIf { it.isNotBlank() }
                            ?: "Ocurrió un error inesperado"
                        val finalCode = errorModel?.effectiveCode?.takeIf { it != 0 } ?: statusCode

                        when {
                            statusCode == 401 || (finalCode == 17 && (finalTitle.contains("token", ignoreCase = true) || finalMessage.contains("token", ignoreCase = true))) -> {
                                throw com.pizzza.pizzzaapp.repository.network.exception.UnAuthorizedException()
                            }
                            else -> {
                                throw UiTayApiException(
                                    code = finalCode,
                                    title = finalTitle,
                                    messageApi = finalMessage
                                )
                            }
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

    single(named("wsClient")) {
        val connectivityManager: ConnectivityManager = get()
        HttpClient {
            install(WebSockets)
            install(Logging) {
                logger = requestLogger
                level = LogLevel.ALL
            }
            defaultRequest {
                if (!connectivityManager.isConnected()) throw ErrorNetwork()
            }
        }
    }

    single { KmmService(get(named("httpClient"))) }
    single { WebSocketManager(get(named("wsClient"))) }
}

