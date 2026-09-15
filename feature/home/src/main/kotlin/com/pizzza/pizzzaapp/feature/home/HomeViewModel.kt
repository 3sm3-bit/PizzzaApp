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
            val response = io { dataUseCase.getProductsLocal() }
            appDataOrder.update { state ->
                state.copy(
                    products = response,
                    pizzaProducts = response.filter { product -> product.type == "1" },
                    extraProducts = response.filter { product -> product.type == "2" || product.type == "3" },
                    promotionsProducts = response.filter { product -> product.type == "4" },
                    deliveryProducts = response.filter { product -> product.type == "5" }
                )
            }
        }
    }

    fun selectProduct(product: ProductModel?) {
        appDataOrder.update { it.copy(selectedProduct = product) }
    }
}
