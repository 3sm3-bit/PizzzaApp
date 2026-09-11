package com.pizzza.pizzzaapp.core.ui.singleton

import androidx.compose.runtime.staticCompositionLocalOf
import com.pizzza.pizzzaapp.core.ui.base.BaseUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalGlobalUiStateManager = staticCompositionLocalOf<GlobalUiStateManager> {
    error("No GlobalUiStateManager provided")
}

class GlobalUiStateManager {
    private val _uiState = MutableStateFlow(BaseUiState())
    val uiState: StateFlow<BaseUiState> = _uiState.asStateFlow()

    fun updateUiState(update: (BaseUiState) -> BaseUiState) {
        _uiState.update(update)
    }
}
