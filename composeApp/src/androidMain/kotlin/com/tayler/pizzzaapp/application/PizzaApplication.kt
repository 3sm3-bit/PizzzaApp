package com.tayler.pizzzaapp.application

import android.app.Application
import com.tayler.pizzzaapp.di.appModule
import com.tayler.pizzzaapp.di.networkModule
import com.tayler.pizzzaapp.di.platformModule
import com.tayler.pizzzaapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PizzaApplication)
            androidLogger()
            modules(viewModelModule, appModule, networkModule, platformModule)
        }
    }
}
