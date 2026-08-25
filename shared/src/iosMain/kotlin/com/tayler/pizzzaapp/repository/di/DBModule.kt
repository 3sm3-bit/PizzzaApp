package com.tayler.pizzzaapp.repository.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.tayler.pizzzaapp.repository.db.manager.AppDataBase
import com.tayler.pizzzaapp.repository.db.manager.AppDataBaseConstructor
import com.tayler.pizzzaapp.repository.utils.ConnectivityManager
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

actual val dbModule = module {
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
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .build()
    }
}
