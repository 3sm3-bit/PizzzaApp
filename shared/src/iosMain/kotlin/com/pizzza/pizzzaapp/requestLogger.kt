package com.pizzza.pizzzaapp

import com.pizzza.pizzzaapp.shared.BuildConfig
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger = object : Logger {
    override fun log(message: String) {
        if (BuildConfig.IS_DEBUG) {
            Logger.DEFAULT.log(message)
        }
    }
}
