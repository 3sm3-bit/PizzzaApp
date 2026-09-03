package com.pizzza.pizzzaapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.pizzza.pizzzaapp.DispatcherProvider
import com.pizzza.pizzzaapp.TAG_PIZZZA
import com.pizzza.pizzzaapp.model.ProductModel
import com.pizzza.pizzzaapp.repository.network.model.OrderResponse
import com.pizzza.pizzzaapp.ui.base.BaseViewModel
import com.pizzza.pizzzaapp.ui.orders.OrderItem
import com.pizzza.pizzzaapp.ui.orders.OrderUiState
import com.pizzza.pizzzaapp.usecases.DataUseCase
import kotlinx.coroutines.withContext
import java.util.Locale

class CartViewModel(
    private val dataUseCase: DataUseCase,
    private val dispatchers: DispatcherProvider
) : BaseViewModel(dispatchers) {

    var cartUiState by mutableStateOf(OrderUiState())
        private set

    fun addToCart(
        product: ProductModel,
        quantity: Int = 1,
        typeDough: String = "TRADICIONAL",
        cheeseFilledCrust: Boolean = false,
        note: String = ""
    ) {
        val currentCart = cartUiState.cart.toMutableList()
        val productType = product.type.trim()
        
        if (productType == "1") {
            currentCart.add(OrderItem(
                product = product, 
                quantity = quantity, 
                typeDough = typeDough, 
                cheeseFilledCrust = cheeseFilledCrust, 
                note = note
            ))
        } else {
            val index = currentCart.indexOfFirst { it.product.uid == product.uid }
            if (index != -1) {
                val item = currentCart[index]
                currentCart[index] = item.copy(quantity = item.quantity + quantity)
            } else {
                currentCart.add(OrderItem(product = product, quantity = quantity))
            }
        }
        cartUiState = cartUiState.copy(cart = currentCart)
    }

    fun removeCartItem(item: OrderItem) {
        val currentCart = cartUiState.cart.toMutableList()
        currentCart.removeAll { it.id == item.id }
        cartUiState = cartUiState.copy(cart = currentCart)
    }


    fun clearCart() {
        cartUiState = cartUiState.copy(
            cart = emptyList(),
            receptionMode = "DELIVERY",
            selectedDeliveryProduct = null,
            deliveryAddress = ""
        )
    }

    fun setInitialTab(index: Int) {
        cartUiState = cartUiState.copy(initialTab = index)
    }

    fun setReceptionMode(mode: String, defaultDeliveryProduct: ProductModel? = null) {
        cartUiState = cartUiState.copy(
            receptionMode = mode,
            selectedDeliveryProduct = if (mode == "RECOJO") null else (cartUiState.selectedDeliveryProduct ?: defaultDeliveryProduct),
            deliveryAddress = if (mode == "RECOJO") "" else cartUiState.deliveryAddress
        )
    }

    fun setAddressSelection(address: String, lat: String, lng: String) {
        cartUiState = cartUiState.copy(
            deliveryAddress = address,
            latitude = lat,
            longitude = lng
        )
    }

    fun loadUserAddress() {
        execute(loading = false) {
            val user = dataUseCase.getUserLocal()
            user?.let {
                if (cartUiState.deliveryAddress.isBlank()) {
                    withContext(dispatchers.main) {
                        cartUiState = cartUiState.copy(
                            deliveryAddress = it.address,
                            latitude = it.latitude,
                            longitude = it.longitude
                        )
                    }
                }
            }
        }
    }

    fun confirmOrder(onSuccess: () -> Unit) {
        execute {
            try {
                val user = dataUseCase.getUserLocal() ?: return@execute
                
                // Asegurar que tenemos un producto de delivery si es necesario
                val deliveryProduct = if (cartUiState.receptionMode == "DELIVERY") {
                    cartUiState.selectedDeliveryProduct ?: dataUseCase.getProducts().firstOrNull { it.type == "4" }
                } else null

                val orders = cartUiState.cart.map { item ->
                    OrderResponse(
                        userId = user.uid,
                        driverId = "0",
                        nameClient = user.nameUser,
                        quantity = item.quantity.toString(),
                        type = item.product.type,
                        symbol = item.product.currencySymbol,
                        nameProduct = item.product.nameProduct,
                        tamanio = if (item.product.type == "1") item.product.tamanio else "",
                        typeDough = if (item.product.type == "1") item.typeDough else "",
                        cheeseFilledCrust = if (item.cheeseFilledCrust) "SI" else "NO",
                        note = item.note,
                        phone = user.phone,
                        price = item.product.price,
                        priceTotal = String.format(Locale.US, "%.2f", ((item.product.price.toDoubleOrNull() ?: 0.0) + (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)) * item.quantity),
                        state = "CONFIRMADO",
                        date = "",
                        address = if (cartUiState.receptionMode == "DELIVERY") cartUiState.deliveryAddress else "",
                        reception = cartUiState.receptionMode,
                        priceDelivery = if (cartUiState.receptionMode == "DELIVERY") deliveryProduct?.price ?: "0" else "0",
                        priceChosse = item.product.priceChosse,
                        idOrden = "",
                        branchId = "1",
                        stage = "1",
                        latitude = "21.852697463621574",//cartUiState.latitude,
                        longitude ="-102.33635533601046",// cartUiState.longitude
                    )
                }
                
                dataUseCase.createOrder(orders)
                
                withContext(dispatchers.main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                println("$TAG_PIZZZA: Error al confirmar pedido: ${e.message}")
                throw e
            }
        }
    }
}
