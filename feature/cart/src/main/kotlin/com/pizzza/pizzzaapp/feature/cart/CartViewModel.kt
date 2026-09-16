package com.pizzza.pizzzaapp.feature.cart

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.model.OrderItem
import com.pizzza.pizzzaapp.core.ui.model.OrderUiState
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.usecases.DataUseCase
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class CartViewModel(
    private val dataUseCase: DataUseCase,
    private val globalUiStateManager: GlobalUiStateManager,
    private val appDataOrder: AppDataOrder
) : BaseViewModel() {

    val cartUiState: StateFlow<OrderUiState> = appDataOrder.state

    fun removeCartItem(item: OrderItem) {
        appDataOrder.update { currentState ->
            val currentCart = currentState.cart.toMutableList()
            currentCart.removeAll { it.id == item.id }
            currentState.copy(cart = currentCart)
        }
    }

    fun setInitialTab(index: Int) {
        appDataOrder.update { it.copy(initialTab = index) }
    }

    fun setReceptionMode(mode: String, defaultDeliveryProduct: ProductModel?) {
        appDataOrder.update { 
            it.copy(
                receptionMode = mode,
                selectedDeliveryProduct = if (mode == "RECOJO") null else (it.selectedDeliveryProduct ?: defaultDeliveryProduct)
            )
        }
    }

    fun loadUserAddress() {
        execute(loading = false, globalUiStateManager = globalUiStateManager) {
            val user = io { dataUseCase.getUserLocal() }
            if (user != null && (cartUiState.value.deliveryAddress.isBlank() || cartUiState.value.deliveryAddress == "Selecciona dirección en el mapa")) {
                appDataOrder.update {
                    it.copy(
                        deliveryAddress = user.address,
                        latitude = user.latitude,
                        longitude = user.longitude
                    )
                }
            }
        }
    }

    fun startPayment(onUrlReady: (String) -> Unit) {
        execute(globalUiStateManager = globalUiStateManager) {
            val state = cartUiState.value
            if (state.cart.isEmpty()) return@execute

            val cartProductsTotal = state.cart.sumOf { item ->
                val basePrice = item.product.price.toDoubleOrNull() ?: 0.0
                val crustPrice = if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0
                (basePrice + crustPrice) * item.quantity
            }
            val deliveryPrice = if (state.receptionMode == "DELIVERY") kotlin.math.round(cartProductsTotal * 0.20) else 0.0
            val total = cartProductsTotal + deliveryPrice

            val user = io { dataUseCase.getUserLocal() }
            val orderId = UUID.randomUUID().toString() // Generamos ID de orden para el pago
            
            val paymentUrl = io { 
                dataUseCase.createPaymentSession(
                    amount = total,
                    email = user?.email ?: "",
                    orderId = orderId
                ) 
            }
            
            if (paymentUrl.isNotBlank()) {
                onUrlReady(paymentUrl)
            }
        }
    }

    private var isConfirmingOrder = false

    fun confirmOrder(statePay: String = "PENDIENTE", onComplete: () -> Unit) {
        if (isConfirmingOrder) return
        val state = cartUiState.value
        if (state.cart.isEmpty()) return

        isConfirmingOrder = true
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                val cartProductsTotal = state.cart.sumOf { item ->
                    val basePrice = item.product.price.toDoubleOrNull() ?: 0.0
                    val crustPrice = if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0
                    (basePrice + crustPrice) * item.quantity
                }

                val deliveryPrice = if (state.receptionMode == "DELIVERY") {
                    kotlin.math.round(cartProductsTotal * 0.20).toLong().toString()
                } else "0"

                io {
                    val user = dataUseCase.getUserLocal()
                    val idOrder = UUID.randomUUID().toString()

                    val orderRequest = state.cart.map { item ->
                        val isDelivery = state.receptionMode == "DELIVERY"

                        val itemPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                                (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)

                        OrderResponse(
                            uid = UUID.randomUUID().toString(),
                            nameClient = "${user?.names}",
                            quantity = item.quantity.toString(),
                            type = item.product.type,
                            symbol = item.product.currencySymbol,
                            nameProduct = item.product.nameProduct,
                            tamanio = item.product.tamanio,
                            typeDough = item.typeDough,
                            cheeseFilledCrust = if (item.cheeseFilledCrust) "SI" else "NO",
                            note = item.note,
                            phone = user?.phone ?: "",
                            price = item.product.price,
                            priceTotal = (itemPrice * item.quantity).toString(),
                            state = "CONFIRMADO",
                            address = if (isDelivery) state.deliveryAddress else "RECOJO EN LOCAL",
                            reception = state.receptionMode,
                            priceDelivery = deliveryPrice,
                            priceChosse = item.product.priceChosse,
                            idOrden = idOrder,
                            userId = user?.uid ?: "",
                            latitude = if (isDelivery) state.latitude else "0",
                            longitude = if (isDelivery) state.longitude else "0",
                            statePay = statePay
                        )
                    }

                    dataUseCase.createOrder(orderRequest)
                }

                appDataOrder.update {
                    it.copy(
                        cart = emptyList(),
                        initialTab = 3,
                        ordersLoaded = false
                    )
                }
                onComplete()
            } finally {
                isConfirmingOrder = false
            }
        }
    }

    fun resetState() {
        appDataOrder.reset()
    }
}
