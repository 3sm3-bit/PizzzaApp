package com.pizzza.pizzzaapp.core.ui

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalAppDataOrder = staticCompositionLocalOf<AppDataOrder> {
    error("No AppDataOrder provided")
}

class AppDataOrder {
    private val _state = MutableStateFlow(OrderUiState())
    val state: StateFlow<OrderUiState> = _state.asStateFlow()

    fun update(block: (OrderUiState) -> OrderUiState) {
        _state.update(block)
    }

    fun reset() {
        _state.update { OrderUiState() }
    }
}
