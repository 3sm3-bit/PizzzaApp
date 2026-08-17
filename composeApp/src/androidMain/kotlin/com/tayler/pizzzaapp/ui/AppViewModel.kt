package com.tayler.pizzzaapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.tayler.pizzzaapp.usecases.DataUseCase

data class OrderUiState(
    val orders: List<ParentOrderModel> = emptyList(),
    val filteredOrders: List<ParentOrderModel> = emptyList(),
    val selectedFilter: String = "TODOS"
)

class AppViewModel(
    private val dataUseCase: DataUseCase
) : BaseViewModel() {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    fun getGeneralOrderList() {
        execute {
            val response = dataUseCase.loadParentOrder()
            orderUiState = orderUiState.copy(
                orders = response,
                filteredOrders = applyFilter(response, orderUiState.selectedFilter)
            )
            Log.d("dataservice", response.toString())
        }
    }

    fun refresh() {
        execute {
            val response = dataUseCase.loadParentOrder(forceRefresh = true)
            orderUiState = orderUiState.copy(
                orders = response,
                filteredOrders = applyFilter(response, orderUiState.selectedFilter)
            )
        }
    }

    fun updateFilter(filter: String) {
        orderUiState = orderUiState.copy(
            selectedFilter = filter,
            filteredOrders = applyFilter(orderUiState.orders, filter)
        )
    }

    private fun applyFilter(orders: List<ParentOrderModel>, filter: String): List<ParentOrderModel> {
        val cleanFilter = filter.trim().uppercase()
        return if (cleanFilter == "TODOS") {
            orders
        } else {
            orders.filter { 
                it.state.trim().uppercase() == cleanFilter
            }
        }
    }
}
