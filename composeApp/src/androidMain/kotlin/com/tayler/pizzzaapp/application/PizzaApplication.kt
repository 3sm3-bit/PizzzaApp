package com.tayler.pizzzaapp.application

import android.app.Application
import com.tayler.pizzzaapp.di.initKoin
import com.tayler.pizzzaapp.di.viewModelModule
import com.tayler.pizzzaapp.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        initKoin {
            androidContext(this@PizzaApplication)
            androidLogger()
            modules(viewModelModule)
        }
    }
}
