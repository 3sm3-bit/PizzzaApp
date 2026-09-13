package com.pizzza.pizzzaapp.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.pizzza.pizzzaapp.component.AppNavigation
import com.pizzza.pizzzaapp.core.ui.base.BaseActivity
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : BaseActivity() {

    private val cartViewModel: CartViewModel by inject()

    @Composable
    override fun SetScreenConfig() {
        val appDataOrder: AppDataOrder = koinInject()
        CompositionLocalProvider(LocalAppDataOrder provides appDataOrder) {
            AppNavigation()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "pizzitas" && data.host == "payment") {
            if (data.path == "/success") {
                cartViewModel.confirmOrder(statePay = "PAGADO") {
                    val newIntent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(newIntent)
                }
            }
        }
    }

    override fun setDataGlobal() {
        intent?.let { handleIntent(it) }
    }
}
