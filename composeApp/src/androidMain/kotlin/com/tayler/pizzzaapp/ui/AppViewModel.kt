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
    val selectedFilter: String = "TODOS",
    val countConfirmado: Int = 0,
    val countListo: Int = 0,
    val selectedOrder: ParentOrderModel? = null
)

class AppViewModel(
    private val dataUseCase: DataUseCase
) : BaseViewModel() {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    fun getGeneralOrderList() {
        Log.d("AppViewModel", "getGeneralOrderList: Iniciando ejecución")
        execute {
            try {
                val response = dataUseCase.loadParentOrder()
                updateStateWithOrders(response)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            }
        }
    }

    fun refresh() {
        Log.d("AppViewModel", "refresh: Forzando refresco")
        execute {
            try {
                val response = dataUseCase.loadParentOrder(forceRefresh = true)
                updateStateWithOrders(response)
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error en refresh: ${e.message}", e)
                throw e
            }
        }
    }

    private fun updateStateWithOrders(orders: List<ParentOrderModel>) {
        val sortedOrders = orders.sortedBy {
            when (it.state.trim().uppercase()) {
                "CONFIRMADO" -> 1
                "LISTO" -> 2
                else -> 3
            }
        }

        val countConfirmado = orders.count { it.state.trim().uppercase() == "CONFIRMADO" }
        val countListo = orders.count { it.state.trim().uppercase() == "LISTO" }

        orderUiState = orderUiState.copy(
            orders = sortedOrders,
            filteredOrders = sortedOrders, // Mostramos todos por defecto ya que no hay filtros
            countConfirmado = countConfirmado,
            countListo = countListo
        )
    }

    fun applyFilter(filter: String) {
        val filtered = if (filter == "TODOS") {
            orderUiState.orders
        } else {
            orderUiState.orders.filter { it.state.trim().uppercase() == filter.uppercase() }
        }
        orderUiState = orderUiState.copy(
            filteredOrders = filtered,
            selectedFilter = filter
        )
    }

    fun updateOrderState(order: ParentOrderModel, newState: String) {
        if (order.state.trim().uppercase() == newState.uppercase()) return

        // 1. Guardar estado previo para Reversión (Rollback) en caso de error
        val previousState = orderUiState

        // 2. Actualización Optimista: Actualizamos la UI inmediatamente
        Log.d("AppViewModel", "updateOrderState: Actualización optimista de ${order.uid} a $newState")
        val updatedOrders = orderUiState.orders.map { 
            if (it.uid == order.uid) it.copy(state = newState) else it 
        }
        updateStateWithOrders(updatedOrders)

        // 3. Sincronización en segundo plano
        // Usamos loading = false para que no aparezca el progreso global y la app se sienta "rápida"
        execute(loading = false) {
            try {
                dataUseCase.updateOrder(order.copy(state = newState))
                Log.d("AppViewModel", "updateOrderState: Sincronización exitosa con servidor")
            } catch (e: Exception) {
                // 4. Rollback: Si falla el servidor, devolvemos la UI a su estado anterior
                Log.e("AppViewModel", "updateOrderState: Error al sincronizar. Revirtiendo UI.", e)
                orderUiState = previousState
                throw e // Permitimos que BaseViewModel muestre el diálogo de error
            }
        }
    }

    fun avanzarEstado(order: ParentOrderModel) {
        val currentState = order.state.trim().uppercase()
        val nextState = when (currentState) {
            "CONFIRMADO" -> "LISTO"
            "LISTO" -> "ENTREGADO" // O el estado final que manejes
            else -> "CONFIRMADO"
        }
        updateOrderState(order, nextState)
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }
}
