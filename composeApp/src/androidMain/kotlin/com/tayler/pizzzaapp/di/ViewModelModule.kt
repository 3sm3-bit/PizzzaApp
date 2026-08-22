package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.ui.AppViewModel
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AppViewModel(get(), get()) }
    viewModel { BaseViewModel(get()) }
}
