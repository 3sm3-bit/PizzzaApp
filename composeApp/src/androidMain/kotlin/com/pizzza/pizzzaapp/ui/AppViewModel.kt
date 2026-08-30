package com.pizzza.pizzzaapp.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzaapp.DispatcherProvider
import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.model.BranchModel
import com.pizzza.pizzzaapp.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.ui.orders.OrderItem
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
                val response = dataUseCase.loadParentOrder()
                val filteredOrders = if (user != null) {
                    response.filter { it.userId == user.uid }
                } else {
                    response
                }
                updateStateWithOrders(filteredOrders)
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en getGeneralOrderList: ${e.message}", e)
                throw e
            }
        }
    }

    fun refresh() {
        Log.d(TAG_PIZZZA, "refresh: Forzando refresco")
        execute {
            try {
                val user = dataUseCase.getUserLocal()
                val response = dataUseCase.loadParentOrder(forceRefresh = true)
                val filteredOrders = if (user != null) {
                    response.filter { it.userId == user.uid }
                } else {
                    response
                }
                updateStateWithOrders(filteredOrders)
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en refresh: ${e.message}", e)
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
            countListo = countListo,
            ordersLoaded = true
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
        // Cargamos en segundo plano para no bloquear, ya que usualmente viene de DB local
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

    fun getBranchesList() {
        val hasData = orderUiState.branches.isNotEmpty()
        execute(loading = !hasData) {
            try {
                val response = dataUseCase.getBranches()
                withContext(dispatchers.main) {
                    orderUiState = orderUiState.copy(branches = response)
                }
            } catch (e: Exception) {
                Log.e(TAG_PIZZZA, "Error en getBranchesList: ${e.message}", e)
                throw e
            }
        }
    }

    fun selectOrder(order: ParentOrderModel?) {
        orderUiState = orderUiState.copy(selectedOrder = order)
    }

    fun selectBranch(branch: BranchModel?) {
        orderUiState = orderUiState.copy(selectedBranch = branch)
    }

    fun selectProduct(product: ProductModel?) {
        orderUiState = orderUiState.copy(selectedProduct = product)
    }

    fun setCategory(category: String) {
        orderUiState = orderUiState.copy(selectedCategory = category)
    }

    fun addToCart(
        product: ProductModel,
        quantity: Int = 1,
        typeDough: String = "TRADICIONAL",
        cheeseFilledCrust: Boolean = false,
        note: String = ""
    ) {
        val currentCart = orderUiState.cart.toMutableList()
        val productType = product.type.trim()
        
        // Si es pizza (tipo 1), siempre agregamos como item nuevo para permitir personalización individual
        if (productType == "1") {
            currentCart.add(OrderItem(
                product = product, 
                quantity = quantity, 
                typeDough = typeDough, 
                cheeseFilledCrust = cheeseFilledCrust, 
                note = note
            ))
        } else {
            // Para otros productos (bebidas, etc.), podemos agrupar si son el mismo producto
            val index = currentCart.indexOfFirst { it.product.uid == product.uid }
            if (index != -1) {
                val item = currentCart[index]
                currentCart[index] = item.copy(quantity = item.quantity + quantity)
            } else {
                currentCart.add(OrderItem(product = product, quantity = quantity))
            }
        }
        orderUiState = orderUiState.copy(cart = currentCart, selectedProduct = null)
    }

    fun removeCartItem(item: OrderItem) {
        val currentCart = orderUiState.cart.toMutableList()
        currentCart.removeAll { it.id == item.id }
        orderUiState = orderUiState.copy(cart = currentCart)
    }

    fun updateCartItem(oldItem: OrderItem, newItem: OrderItem) {
        val currentCart = orderUiState.cart.toMutableList()
        val index = currentCart.indexOfFirst { it.id == oldItem.id }
        if (index != -1) {
            currentCart[index] = newItem
            orderUiState = orderUiState.copy(cart = currentCart)
        }
    }

    fun clearCart() {
        orderUiState = orderUiState.copy(cart = emptyList())
    }

    fun toggleNotifications() {
        orderUiState = orderUiState.copy(notificationsEnabled = !orderUiState.notificationsEnabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        orderUiState = orderUiState.copy(notificationsEnabled = enabled)
    }

    fun resetOrderState() {
        orderUiState = OrderUiState()
    }
}
