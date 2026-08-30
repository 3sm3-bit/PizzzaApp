package com.pizzza.pizzzaapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzaapp.DispatcherProvider
import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.ui.orders.OrderUiState
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.withContext

class StoreViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var storeUiState by mutableStateOf(OrderUiState())
        private set

    fun getProductsList() {
        val hasData = storeUiState.products.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    storeUiState = storeUiState.copy(
                        products = response,
                        pizzaProducts = response.filter { it.type == "1" },
                        extraProducts = response.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = response.filter { it.type == "4" }
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en getProductsList: ${e.message}", e)
                throw e
            }
        }
    }

    fun getBranchesList() {
        val hasData = storeUiState.branches.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getBranches()
                withContext(dispatchers.main) {
                    storeUiState = storeUiState.copy(branches = response)
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en getBranchesList: ${e.message}", e)
                throw e
            }
        }
    }

    fun selectBranch(branch: BranchModel?) {
        storeUiState = storeUiState.copy(selectedBranch = branch)
    }

    fun selectProduct(product: ProductModel?) {
        storeUiState = storeUiState.copy(selectedProduct = product)
    }

    fun setCategory(category: String) {
        storeUiState = storeUiState.copy(selectedCategory = category)
    }
}
