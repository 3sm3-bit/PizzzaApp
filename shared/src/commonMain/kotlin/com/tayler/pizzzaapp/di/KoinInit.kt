package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.repository.di.networkModule
import com.tayler.pizzzaapp.repository.di.dbModule
import com.tayler.pizzzaapp.repository.di.repositoryModule
import com.tayler.pizzzaapp.usecases.di.useCasesModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            dispatcherModule,
            repositoryModule,
            networkModule,
            dbModule,
            useCasesModule
        )
    }

// Llamada desde iOS
fun initKoin() = initKoin {}
