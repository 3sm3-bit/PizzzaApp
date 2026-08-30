package com.pizzza.pizzzaapp.di

import com.pizzza.pizzzaapp.DefaultDispatcherProvider
import com.pizzza.pizzzaapp.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dispatcherModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
}