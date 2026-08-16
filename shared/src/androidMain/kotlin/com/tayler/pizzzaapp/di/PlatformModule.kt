package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.utils.AndroidConnectivityManager
import com.tayler.pizzzaapp.utils.ConnectivityManager
import org.koin.dsl.module

val platformModule = module {
    single<ConnectivityManager> { AndroidConnectivityManager(get()) }
}
