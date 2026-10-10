package com.pizzza.pizzzaapp.feature.orders

import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderHistoryViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager
) : BaseViewModel() {

    private val _orders = MutableStateFlow<List<ParentOrderModel>>(emptyList())
    val orders: StateFlow<List<ParentOrderModel>> = _orders.asStateFlow()

    fun getOrderHistory(){
        execute(globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.getUserLocal() }
            if (user != null) {
                val response: List<ParentOrderModel> = io { dataUseCase.loadParentOrder(user.uid) }
                _orders.value = response
            } else {
                _orders.value = emptyList()
            }
        }
    }
}
