package com.pizzza.pizzzaapp.feature.orders

import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.model.OrderUiState
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.core.ui.model.OrderItem
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

class OrdersViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : BaseViewModel(ioDispatcher, defaultDispatcher) {

    val orderUiState: StateFlow<OrderUiState> = appDataOrder.state

    fun getGeneralOrderList(forceLoading: Boolean = false) {
        val isAlreadyLoaded = appDataOrder.state.value.ordersLoaded
        println("$TAG_PIZZZA: OrdersViewModel: Solicitando lista (force=$forceLoading, yaCargado=$isAlreadyLoaded)")
        
        if (isAlreadyLoaded && !forceLoading) {
            println("$TAG_PIZZZA: OrdersViewModel: Ignorando carga, ya tenemos datos")
            return
        }
        
        execute(loading = forceLoading, globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.getUserLocal() }
            if (user != null) {
                val response = io { dataUseCase.loadParentOrder(userId = user.uid) }
                updateStateWithOrders(response)
            } else {
                updateStateWithOrders(emptyList())
            }
        }
    }

    fun getOrderDetail(orderId: String) {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val updatedOrder = io { dataUseCase.getOrderById(orderId) }
            appDataOrder.update { it.copy(selectedOrder = updatedOrder) }
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

        appDataOrder.update { 
            it.copy(
                orders = sortedOrders,
                ordersLoaded = true
            )
        }
    }
    
    fun selectOrder(order: ParentOrderModel?) {
        appDataOrder.update { it.copy(selectedOrder = order) }
    }

    fun selectProduct(product: ProductModel?) {
        appDataOrder.update { it.copy(selectedProduct = product) }
    }

    fun addToCart(product: ProductModel, quantity: Int, typeDough: String, cheeseFilledCrust: Boolean, note: String) {
        appDataOrder.update { currentState ->
            val currentCart = currentState.cart.toMutableList()
            val index = currentCart.indexOfFirst {
                (it.product.uid == product.uid) &&
                        (it.typeDough == typeDough) &&
                        (it.cheeseFilledCrust == cheeseFilledCrust) &&
                        (it.note == note)
            }
            
            if (index != -1) {
                val item = currentCart[index]
                currentCart[index] = item.copy(quantity = item.quantity + quantity)
            } else {
                currentCart.add(
                    OrderItem(
                        product = product,
                        quantity = quantity,
                        typeDough = typeDough,
                        cheeseFilledCrust = cheeseFilledCrust,
                        note = note,
                    )
                )
            }
            currentState.copy(cart = currentCart)
        }
    }

    fun resetOrderState() {
        appDataOrder.reset()
    }
}
