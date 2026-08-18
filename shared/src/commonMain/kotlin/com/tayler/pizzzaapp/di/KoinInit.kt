package com.tayler.pizzzaapp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule,
            networkModule,
            platformModule
        )
    }

// Llamada desde iOS
fun initKoin() = initKoin {}
