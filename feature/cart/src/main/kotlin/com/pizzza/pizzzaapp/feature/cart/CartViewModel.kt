package com.pizzza.pizzzaapp.feature.cart

import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.core.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.core.ui.singleton.GlobalUiStateManager
import com.pizzza.pizzzaapp.core.ui.model.OrderItem
import com.pizzza.pizzzaapp.core.ui.model.OrderUiState
import com.pizzza.pizzzaapp.core.ui.singleton.AppDataOrder
import com.pizzza.pizzzaapp.usecases.DataUseCase
import com.pizzza.pizzzaapp.repository.network.model.ConfirmOrderRequest
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

    fun selectBranch(branchId: String) {
        appDataOrder.update { it.copy(branchId = branchId) }
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

    fun updateDeliveryAddress(address: String, lat: String, lng: String) {
        appDataOrder.update {
            it.copy(
                deliveryAddress = address,
                latitude = lat,
                longitude = lng
            )
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
            val deliveryPriceDouble = if (state.receptionMode == "DELIVERY") kotlin.math.round(cartProductsTotal * 0.20) else 0.0
            val deliveryPriceStr = if (state.receptionMode == "DELIVERY") deliveryPriceDouble.toLong().toString() else "0"
            val total = cartProductsTotal + deliveryPriceDouble

            val user = io { dataUseCase.getUserLocal() }

            val orderRequest = state.cart.mapIndexed { index, item ->
                val isDelivery = state.receptionMode == "DELIVERY"
                val itemPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                        (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)
                val itemSubtotal = itemPrice * item.quantity
                val itemPriceTotal = if (isDelivery && index == 0) itemSubtotal + deliveryPriceDouble else itemSubtotal

                OrderResponse(
                    uid = UUID.randomUUID().toString().replace("-", "").substring(0, 16),
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
                    priceTotal = if (itemPriceTotal % 1.0 == 0.0) itemPriceTotal.toLong().toString() else String.format(java.util.Locale.US, "%.2f", itemPriceTotal),
                    state = "PENDIENTE",
                    address = if (isDelivery) state.deliveryAddress else "RECOJO EN LOCAL",
                    reception = state.receptionMode,
                    priceDelivery = deliveryPriceStr,
                    priceChosse = item.product.priceChosse,
                    idOrden = "",
                    branchId = state.branchId,
                    userId = user?.uid ?: "",
                    latitude = if (isDelivery) state.latitude else "0",
                    longitude = if (isDelivery) state.longitude else "0",
                    statePay = "PENDIENTE",
                    canal = "A1P9X2"
                )
            }

            val mobileResponse = io { dataUseCase.createOrderMobile(orderRequest) }
            val generalUid = mobileResponse.ordenGeneral?.uid ?: ""
            if (generalUid.isNotBlank()) {
                appDataOrder.update { it.copy(pendingOrderUid = generalUid) }
            }

            val orderId = generalUid.ifBlank { UUID.randomUUID().toString() }
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

        isConfirmingOrder = true
        execute(globalUiStateManager = globalUiStateManager) {
            try {
                val pendingUid = state.pendingOrderUid

                io {
                    if (pendingUid.isNotBlank()) {
                        dataUseCase.confirmOrderMobile(
                            ordenGeneralUid = pendingUid,
                            request = ConfirmOrderRequest(state = "CONFIRMADO", statePay = statePay)
                        )
                    } else if (state.cart.isNotEmpty()) {
                        val user = dataUseCase.getUserLocal()
                        val cartProductsTotal = state.cart.sumOf { item ->
                            val basePrice = item.product.price.toDoubleOrNull() ?: 0.0
                            val crustPrice = if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0
                            (basePrice + crustPrice) * item.quantity
                        }
                        val deliveryPriceDouble = if (state.receptionMode == "DELIVERY") {
                            kotlin.math.round(cartProductsTotal * 0.20)
                        } else 0.0
                        val deliveryPrice = deliveryPriceDouble.toLong().toString()

                        val orderRequest = state.cart.mapIndexed { index, item ->
                            val isDelivery = state.receptionMode == "DELIVERY"
                            val itemPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                                    (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)
                            val itemSubtotal = itemPrice * item.quantity
                            val itemPriceTotal = if (isDelivery && index == 0) itemSubtotal + deliveryPriceDouble else itemSubtotal

                            OrderResponse(
                                uid = UUID.randomUUID().toString().replace("-", "").substring(0, 16),
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
                                priceTotal = if (itemPriceTotal % 1.0 == 0.0) itemPriceTotal.toLong().toString() else String.format(java.util.Locale.US, "%.2f", itemPriceTotal),
                                state = "CONFIRMADO",
                                address = if (isDelivery) state.deliveryAddress else "RECOJO EN LOCAL",
                                reception = state.receptionMode,
                                priceDelivery = deliveryPrice,
                                priceChosse = item.product.priceChosse,
                                idOrden = "",
                                branchId = state.branchId,
                                userId = user?.uid ?: "",
                                latitude = if (isDelivery) state.latitude else "0",
                                longitude = if (isDelivery) state.longitude else "0",
                                statePay = statePay,
                                canal = "A1P9X2"
                            )
                        }

                        val mobileResponse = dataUseCase.createOrderMobile(orderRequest)
                        val generalUid = mobileResponse.ordenGeneral?.uid ?: ""
                        if (generalUid.isNotBlank()) {
                            dataUseCase.confirmOrderMobile(
                                ordenGeneralUid = generalUid,
                                request = ConfirmOrderRequest(state = "CONFIRMADO", statePay = statePay)
                            )
                        }
                    }
                }

                appDataOrder.update {
                    it.copy(
                        cart = emptyList(),
                        initialTab = 3,
                        ordersLoaded = false,
                        pendingOrderUid = ""
                    )
                }
                onComplete()
            } finally {
                isConfirmingOrder = false
            }
        }
    }

    fun cancelPendingOrder(onComplete: () -> Unit = {}) {
        val pendingUid = cartUiState.value.pendingOrderUid

        appDataOrder.update {
            it.copy(
                cart = emptyList(),
                pendingOrderUid = ""
            )
        }
        onComplete()

        if (pendingUid.isNotBlank()) {
            execute(loading = false) {
                io {
                    try {
                        dataUseCase.deleteGeneralOrder(pendingUid)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun resetState() {
        appDataOrder.reset()
    }
}
