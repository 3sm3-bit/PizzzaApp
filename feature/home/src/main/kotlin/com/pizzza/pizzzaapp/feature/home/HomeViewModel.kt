package com.pizzza.pizzzaapp.feature.home

import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.model.OrderUiState
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder
) : BaseViewModel() {

    val homeUiState: StateFlow<OrderUiState> = appDataOrder.state

    fun getProductsList() {
        if (homeUiState.value.products.isNotEmpty()) return // Evita recargas innecesarias

        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val response = io { dataUseCase.getProducts() }
            appDataOrder.update {
                it.copy(
                    products = response,
                    pizzaProducts = response.filter { it.type == "1" },
                    extraProducts = response.filter { it.type == "2" || it.type == "3" },
                    deliveryProducts = response.filter { it.type == "4" }
                )
            }
        }
    }

    fun selectProduct(product: ProductModel?) {
        appDataOrder.update { it.copy(selectedProduct = product) }
    }
}
