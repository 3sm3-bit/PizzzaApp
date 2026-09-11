package com.pizzza.pizzzaapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pizzza.pizzzaapp.component.AppNavigation
import com.pizzza.pizzzaapp.core.ui.base.BaseActivity
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
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
