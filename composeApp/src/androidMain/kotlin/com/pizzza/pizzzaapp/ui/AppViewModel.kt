package com.pizzza.pizzzaapp.ui

import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.usecases.DataUseCase

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder
) : BaseViewModel() {

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
                io {
                    val data  = dataUseCase.getProducts()
                    val branch  = dataUseCase.getBranch()
                    val defaultBranchId = when {
                        branch.isEmpty() -> "1"
                        else -> branch.first().identifier
                    }
                    appDataOrder.update { state ->
                        state.copy(
                            products = data,
                            pizzaProducts = data.filter { product -> product.type == "1" },
                            extraProducts = data.filter { product -> product.type == "2" || product.type == "3" },
                            promotionsProducts = data.filter { product -> product.type == "4" },
                            deliveryProducts = data.filter { product -> product.type == "5" },
                            branches = branch,
                            branchId = defaultBranchId
                        )
                    }
                }
                onComplete(true)
        }
    }
}
