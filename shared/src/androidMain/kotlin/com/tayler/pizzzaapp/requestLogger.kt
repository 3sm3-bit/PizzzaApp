package com.tayler.pizzzaapp

import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger

actual val requestLogger: Logger
    get() = Logger.ANDROID