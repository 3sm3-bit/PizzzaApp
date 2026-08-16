package com.tayler.pizzzaapp.di

import com.tayler.pizzzaapp.utils.ConnectivityManager
import org.koin.dsl.module

val platformModule = module {
    single<ConnectivityManager> { 
        object : ConnectivityManager {
            override fun isConnected(): Boolean = true // TODO: Implement NWPathMonitor for iOS
        }
    }
}
