package com.pizzza.pizzzaapp

import android.util.Log
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger = object : Logger {
    override fun log(message: String) {
        Log.d(TAG_PIZZZA, message)
    }
}