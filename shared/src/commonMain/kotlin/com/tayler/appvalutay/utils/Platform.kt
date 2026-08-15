package com.tayler.appvalutay.utils

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
