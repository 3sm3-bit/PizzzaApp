package com.pizzza.pizzzaapp.ui

import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.model.OrderUiState
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseViewModel(ioDispatcher, defaultDispatcher) {
    private val appDataOrder = MutableStateFlow<OrderUiState?>(null)
    val orderUiState: StateFlow<OrderUiState?> = appDataOrder.asStateFlow()

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            try {
                io { dataUseCase.syncProducts() }
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

}
