package com.pizzza.pizzzaapp.di

import com.pizzza.pizzzaapp.usecases.DataUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object KoinHelper : KoinComponent {
    fun getDataUseCase(): DataUseCase = get()
}
