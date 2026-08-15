package com.tayler.pizzzaapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform