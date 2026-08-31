package com.pizzza.pizzzaapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzaapp.DispatcherProvider
import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.ui.orders.OrderUiState
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.withContext

class AppViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var orderUiState by mutableStateOf(OrderUiState())
        private set

    fun getGeneralOrderList(forceLoading: Boolean = false) {
        if (orderUiState.ordersLoaded && !forceLoading) {
            Log.d(TAG_PIZZZA, "getGeneralOrderList: Datos ya cargados, omitiendo llamada")
            return
        }
        
        Log.d(TAG_PIZZZA, "getGeneralOrderList: Iniciando ejecución (force=$forceLoading)")
        execute(loading = forceLoading) {
            try {
                val user = dataUseCase.getUserLocal()
                if (user != null) {
                    val response = dataUseCase.loadParentOrder(userId = user.uid)
                    updateStateWithOrders(response)
                } else {
                    Log.e(TAG_PIZZZA, "getGeneralOrderList: No hay usuario logueado")
                    updateStateWithOrders(emptyList())
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            }
        }
    }

    fun getOrderDetail(orderId: String) {
        execute(loading = false) {
            try {
                val updatedOrder = dataUseCase.getOrderById(orderId)
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(selectedOrder = updatedOrder)
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error al obtener detalle del pedido: ${e.message}")
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

        orderUiState = orderUiState.copy(
            orders = sortedOrders,
            ordersLoaded = true
        )
    }

    fun syncProducts(onComplete: (Boolean) -> Unit = {}) {
        execute(loading = false) {
            try {
                println("$TAG_PIZZZA: Iniciando sincronización obligatoria...")
                dataUseCase.syncProducts()
                // Cargar lo que el servidor acaba de mandar (y que ya está en DB)
                val updatedProducts = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(
                        products = updatedProducts,
                        pizzaProducts = updatedProducts.filter { it.type == "1" },
                        extraProducts = updatedProducts.filter { it.type == "2" || it.type == "3" },
                        deliveryProducts = updatedProducts.filter { it.type == "4" }
                    )
                    println("$TAG_PIZZZA: Sincronización exitosa. Total: ${updatedProducts.size}")
                    onComplete(true)
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error crítico en sincronización: ${e.message}")
                withContext(dispatchers.main) {
                    onComplete(false)
                }
            }
        }
    }

    fun getProductsList() {
        execute(loading = false) {
            try {
                val response = dataUseCase.getProducts()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(
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

    fun selectProduct(product: ProductModel?) {
        orderUiState = orderUiState.copy(selectedProduct = product)
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        orderUiState = orderUiState.copy(notificationsEnabled = enabled)
    }

    fun resetOrderState() {
        orderUiState = OrderUiState()
    }
}
