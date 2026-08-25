package com.tayler.pizzzaapp.usecases.di

import com.tayler.pizzzaapp.usecases.DataUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCasesModule = module {
    factoryOf(::DataUseCase)
}