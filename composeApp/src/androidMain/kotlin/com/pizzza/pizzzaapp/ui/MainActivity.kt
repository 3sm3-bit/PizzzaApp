package com.pizzza.pizzzaapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pizzza.pizzzaapp.component.AppNavigation
import com.pizzza.pizzzaapp.core.ui.BaseActivity
import com.pizzza.pizzzaapp.core.ui.LocalAppDataOrder
import com.pizzza.pizzzaapp.core.ui.AppDataOrder
import org.koin.compose.koinInject

class MainActivity : BaseActivity() {

    @Composable
    override fun SetScreenConfig() {
        val appDataOrder: AppDataOrder = koinInject()
        CompositionLocalProvider(LocalAppDataOrder provides appDataOrder) {
            AppNavigation()
        }
    }

    override fun setDataGlobal() {
        // Inicialización global si fuera necesaria
    }
}
