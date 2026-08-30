package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.pizzza.pizzzaapp.ui.StoreViewModel
import com.valu.uitaycompose.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCart(
    cartViewModel: CartViewModel,
    storeViewModel: StoreViewModel,
    onNavigateToAddressSelection: () -> Unit,
    onNavigateToSummary: () -> Unit
) {
    val uiState = cartViewModel.cartUiState
    val storeState = storeViewModel.storeUiState

    val isButtonEnabled = if (uiState.receptionMode == "RECOJO") {
        uiState.cart.isNotEmpty()
    } else {
        uiState.cart.isNotEmpty() && 
        uiState.deliveryAddress.isNotBlank() && 
        uiState.deliveryAddress != "Selecciona dirección en el mapa"
    }

    LaunchedEffect(Unit) {
        storeViewModel.getProductsList()
        cartViewModel.loadUserAddress()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.cart.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Selecciona modo de recojo", style = textB16, color = Color.Black)
                            Spacer(Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val modes = listOf("RECOJO" to "LOCAL", "DELIVERY" to "DOMICILIO")
                                modes.forEach { (value, label) ->
                                    val isSelected = uiState.receptionMode == value
                                    Surface(
                                        onClick = { 
                                            cartViewModel.setReceptionMode(
                                                mode = value, 
                                                defaultDeliveryProduct = storeState.deliveryProducts.firstOrNull()
                                            ) 
                                        },
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) tay_red_600 else Color.White,
                                        border = if (!isSelected) BorderStroke(1.dp, tay_red_600) else null
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = label, style = textB12, color = if (isSelected) Color.White else tay_red_600)
                                        }
                                    }
                                }
                            }

                            if (uiState.receptionMode == "DELIVERY") {
                                Spacer(Modifier.height(16.dp))
                                Text("Cambiar dirección de entrega", style = textS12, color = tay_red_600)
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    onClick = onNavigateToAddressSelection,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, tay_green_600),
                                    color = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (uiState.deliveryAddress.isBlank()) "Selecciona dirección en el mapa" else uiState.deliveryAddress,
                                            style = textM12,
                                            color = if (uiState.deliveryAddress.isBlank()) Color.Gray else tay_green_600,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = tay_green_600
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFDDDFE2), thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    items(uiState.cart) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = { cartViewModel.removeCartItem(item) }
                        )
                    }
                    
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        if (uiState.cart.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Surface(
                    onClick = { if (isButtonEnabled) onNavigateToSummary() },
                    color = if (isButtonEnabled) tay_green_600 else Color.LightGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    enabled = isButtonEnabled
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Continuar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
