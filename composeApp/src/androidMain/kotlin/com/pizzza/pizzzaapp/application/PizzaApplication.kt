package com.pizzza.pizzzaapp.application

import android.app.Application
import com.pizzza.pizzzaapp.BuildConfig
import com.pizzza.pizzzaapp.di.initKoin
import com.pizzza.pizzzaapp.di.viewModelModule
import com.pizzza.pizzzaapp.utils.SecurityUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@PizzaApplication)
            androidLogger(org.koin.core.logger.Level.ERROR)
            modules(viewModelModule)
        }
    }
}
