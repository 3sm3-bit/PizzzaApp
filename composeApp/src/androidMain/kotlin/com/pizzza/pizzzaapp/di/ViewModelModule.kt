package com.pizzza.pizzzaapp.di

import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.feature.orders.OrdersViewModel
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.feature.auth.AuthViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { GlobalUiStateManager() }
    single { AppDataOrder() }
    viewModel { AppViewModel(get(), get(),get()) }
    viewModel { OrdersViewModel(get(), get(), get()) }
    viewModel { CartViewModel(get(), get(), get()) }
    viewModel { AuthViewModel(get(), get(), get()) }
}
