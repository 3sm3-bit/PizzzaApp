package com.pizzza.pizzzaapp.di

import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.pizzza.pizzzaapp.ui.StoreViewModel
import com.pizzza.pizzzaapp.ui.auth.AuthViewModel
import com.pizzza.pizzzaapp.ui.base.BaseViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AppViewModel(get(), get()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { StoreViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { BaseViewModel(get()) }
}
