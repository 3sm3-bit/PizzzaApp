package com.pizzza.pizzzaapp.repository.di

import com.pizzza.pizzzaapp.repository.db.DataDataBase
import com.pizzza.pizzzaapp.repository.network.DataNetwork
import com.pizzza.pizzzaapp.usecases.network.IDataDataBase
import com.pizzza.pizzzaapp.usecases.network.IDataNetwork
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::DataNetwork) { bind<IDataNetwork>() }
    singleOf(::DataDataBase) { bind<IDataDataBase>() }
}
