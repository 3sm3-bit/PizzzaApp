package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.manager.db.AppDataBase
import com.tayler.pizzzaapp.repository.exception.CompleteErrorModel
import com.tayler.pizzzaapp.repository.exception.UiTayApiException
import com.tayler.pizzzaapp.repository.exception.UnAuthorizedException
import com.tayler.pizzzaapp.repository.manager.InstantSerializer
import com.tayler.pizzzaapp.repository.network.KmmService
import com.tayler.pizzzaapp.requestLogger
import com.tayler.pizzzaapp.utils.parseJsonTo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        useAlternativeNames = false
                        serializersModule = SerializersModule {
                            contextual(Instant::class, InstantSerializer)
                        }
                    },
                )
            }

            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status != HttpStatusCode.OK) {
                        val statusCode = response.status.value
                        val errorText = response.bodyAsText()
                        
                        when (statusCode) {
                            401 -> throw UnAuthorizedException()
                            in 400..599 -> {
                                val errorModel = try {
                                    errorText.parseJsonTo<CompleteErrorModel>()
                                } catch (e: Exception) {
                                    null
                                }
                                throw UiTayApiException(
                                    code = statusCode,
                                    title = errorModel?.title ?: "Error de servidor",
                                    messageApi = errorModel?.message ?: "Ocurrió un error inesperado"
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
                socketTimeoutMillis = 60_000
                requestTimeoutMillis = 60_000
            }
        }
    }

    single { KmmService(get()) }
}

fun getDatabaseBuilder(): androidx.room.RoomDatabase.Builder<AppDataBase> {
    // Esta función debe ser implementada en cada plataforma (expect/actual)
    // O usar un factory de Koin que ya esté configurado.
    throw Exception("Use platform specific builder")
}
