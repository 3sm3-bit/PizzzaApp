package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.DefaultDispatcherProvider
import com.tayler.pizzzaapp.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dispatcherModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
}