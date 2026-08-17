package com.tayler.pizzzaapp.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tayler.pizzzaapp.manager.db.AppDataBase
import com.tayler.pizzzaapp.utils.AndroidConnectivityManager
import com.tayler.pizzzaapp.utils.ConnectivityManager
import org.koin.dsl.module

val platformModule = module {
    single<ConnectivityManager> { AndroidConnectivityManager(get()) }
    
    single<AppDataBase> {
        val context: Context = get()
        val dbFile = context.getDatabasePath("pizzza_app.db")
        Room.databaseBuilder<AppDataBase>(
            context = context,
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
