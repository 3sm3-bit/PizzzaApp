package com.tayler.pizzzaapp.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tayler.pizzzaapp.manager.db.AppDataBase
import com.tayler.pizzzaapp.manager.db.AppDataBaseConstructor
import com.tayler.pizzzaapp.utils.ConnectivityManager
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

actual val platformModule = module {
    single<ConnectivityManager> { 
        object : ConnectivityManager {
            override fun isConnected(): Boolean = true
        }
    }

    single<AppDataBase> {
        val dbFilePath = NSHomeDirectory() + "/pizzza_app.db"
        Room.databaseBuilder<AppDataBase>(
            name = dbFilePath,
            factory = { AppDataBaseConstructor.initialize() }
        )
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
