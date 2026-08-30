package com.pizzza.pizzzaapp.di

import com.pizzza.pizzzaapp.repository.di.networkModule
import com.pizzza.pizzzaapp.repository.di.dbModule
import com.pizzza.pizzzaapp.repository.di.repositoryModule
import com.pizzza.pizzzaapp.usecases.di.useCasesModule
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
