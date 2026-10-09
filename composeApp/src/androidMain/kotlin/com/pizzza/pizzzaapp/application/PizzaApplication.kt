package com.pizzza.pizzzaapp.application

import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.pizzza.pizzzaapp.R
import com.pizzza.pizzzaapp.di.initKoin
import com.pizzza.pizzzaapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PizzaApplication: Application()  {

    override fun onCreate() {
        super.onCreate()

        try {
            val apiKey = try {
                getString(R.string.maps_api_key)
            } catch (_: Exception) {
                val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                appInfo.metaData?.getString("com.google.android.geo.API_KEY") ?: ""
            }
            
            if (apiKey.isNotBlank()) {
                if (!Places.isInitialized()) {
                    Places.initialize(applicationContext, apiKey)
                    Log.i("PIZZZA_PLACES", "✅ Places SDK inicializado correctamente con clave: ${apiKey.take(8)}...")
                } else {
                    Log.i("PIZZZA_PLACES", "✅ Places SDK ya estaba inicializado.")
                }
            } else {
                Log.e("PIZZZA_PLACES", "❌ MAPS_API_KEY está vacía o no se encontró en R.string o Manifest.")
            }
        } catch (e: Exception) {
            Log.e("PIZZZA_PLACES", "❌ Error al inicializar Places SDK: ${e.message}", e)
        }

        initKoin {
            androidContext(this@PizzaApplication)
            androidLogger(org.koin.core.logger.Level.ERROR)
            modules(viewModelModule)
        }
    }
}
