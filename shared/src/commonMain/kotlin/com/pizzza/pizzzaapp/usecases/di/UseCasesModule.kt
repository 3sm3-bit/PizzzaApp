package com.pizzza.pizzzaapp.usecases.di

import com.pizzza.pizzzaapp.usecases.DataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule = module {
    factoryOf(::DataUseCase)
}