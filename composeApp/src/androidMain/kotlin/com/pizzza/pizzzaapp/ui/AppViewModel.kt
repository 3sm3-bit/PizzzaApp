package com.pizzza.pizzzaapp.ui

import android.util.Log
import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.core.ui.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.OrderUiState
import com.pizzza.pizzzaapp.core.ui.AppDataOrder
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.flow.StateFlow

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder
) : BaseViewModel() {

    val orderUiState: StateFlow<OrderUiState> = appDataOrder.state

    fun setNotificationsEnabled(enabled: Boolean) {
        appDataOrder.update { it.copy(notificationsEnabled = enabled) }
    }

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            try {
                println("$TAG_PIZZZA: Iniciando sincronización obligatoria...")
                io { dataUseCase.syncProducts() }
                onComplete(true)
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error crítico en sincronización: ${e.message}")
                onComplete(false)
            }
        }
    }

    fun resetOrderState() {
        appDataOrder.reset()
    }
}
