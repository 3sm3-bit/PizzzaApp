package com.tayler.pizzzaapp.application

import android.app.Application
import com.tayler.pizzzaapp.di.dispatcherModule
import com.tayler.pizzzaapp.repository.di.networkModule
import com.tayler.pizzzaapp.repository.di.dbModule
import com.tayler.pizzzaapp.di.viewModelModule
import com.tayler.pizzzaapp.repository.di.repositoryModule
import com.tayler.pizzzaapp.usecases.di.useCasesModule
import com.tayler.pizzzaapp.utils.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        startKoin {
            androidContext(this@PizzaApplication)
            androidLogger()
            modules( dispatcherModule,viewModelModule,
                repositoryModule, useCasesModule, networkModule, dbModule)
        }
    }
}
