package com.tayler.pizzzaapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tayler.pizzzaapp.DispatcherProvider
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.tayler.pizzzaapp.ui.orders.OrderItem
import com.tayler.pizzzaapp.ui.orders.OrderUiState

class CartViewModel(
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

    fun updateCartItem(oldItem: OrderItem, newItem: OrderItem) {
        val currentCart = cartUiState.cart.toMutableList()
        val index = currentCart.indexOfFirst { it.id == oldItem.id }
        if (index != -1) {
            currentCart[index] = newItem
            cartUiState = cartUiState.copy(cart = currentCart)
        }
    }

    fun clearCart() {
        cartUiState = cartUiState.copy(
            cart = emptyList(),
            receptionMode = "RECOJO",
            selectedDeliveryProduct = null,
            deliveryAddress = ""
        )
    }

    fun setReceptionMode(mode: String, defaultDeliveryProduct: ProductModel? = null) {
        cartUiState = cartUiState.copy(
            receptionMode = mode,
            // Reset delivery data if switching to pickup, or set default if switching to delivery
            selectedDeliveryProduct = if (mode == "RECOJO") null else (cartUiState.selectedDeliveryProduct ?: defaultDeliveryProduct),
            deliveryAddress = if (mode == "RECOJO") "" else cartUiState.deliveryAddress
        )
    }

    fun setDeliveryProduct(product: ProductModel?) {
        cartUiState = cartUiState.copy(selectedDeliveryProduct = product)
    }

    fun setDeliveryAddress(address: String) {
        cartUiState = cartUiState.copy(deliveryAddress = address)
    }
}
