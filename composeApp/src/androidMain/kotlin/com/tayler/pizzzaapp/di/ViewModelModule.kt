package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.ui.AppViewModel
import com.tayler.pizzzaapp.ui.CartViewModel
import com.tayler.pizzzaapp.ui.StoreViewModel
import com.tayler.pizzzaapp.ui.auth.AuthViewModel
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AppViewModel(get(), get()) }
    viewModel { CartViewModel(get()) }
    viewModel { StoreViewModel(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { BaseViewModel(get()) }
}
