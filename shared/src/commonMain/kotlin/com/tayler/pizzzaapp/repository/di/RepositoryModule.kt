package com.tayler.pizzzaapp.repository.di

import com.tayler.pizzzaapp.repository.network.DataNetwork
import com.tayler.pizzzaapp.usecases.network.IDataNetwork
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::DataNetwork) { bind<IDataNetwork>() }
}
