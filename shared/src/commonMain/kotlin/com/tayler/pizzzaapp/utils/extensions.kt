package com.tayler.pizzzaapp.utils

import kotlinx.serialization.json.Json.Default.decodeFromString

internal inline fun <reified R : Any> String.parseJsonTo() =
    decodeFromString<R>(this)
