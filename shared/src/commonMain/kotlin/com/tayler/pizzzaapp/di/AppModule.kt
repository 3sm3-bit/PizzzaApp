package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.repository.network.DataNetwork
import com.tayler.pizzzaapp.usecases.DataUseCase
import com.tayler.pizzzaapp.usecases.network.IDataNetwork
import com.tayler.pizzzaapp.utils.DefaultDispatcherProvider
import com.tayler.pizzzaapp.utils.DispatcherProvider
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::DefaultDispatcherProvider) { bind<DispatcherProvider>() }
    singleOf(::DataNetwork) { bind<IDataNetwork>() }
    factoryOf(::DataUseCase)
}
