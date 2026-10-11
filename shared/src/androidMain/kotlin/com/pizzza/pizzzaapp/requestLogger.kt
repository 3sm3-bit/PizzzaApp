package com.pizzza.pizzzaapp

import android.util.Log
import com.pizzza.pizzzaapp.shared.BuildConfig
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger = object : Logger {
    override fun log(message: String) {
        if (BuildConfig.IS_DEBUG) {
            Log.d(TAG_PIZZZA, message)
        }
    }
}
