package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.usecases.DataUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object KoinHelper : KoinComponent {
    fun getDataUseCase(): DataUseCase = get()
}
