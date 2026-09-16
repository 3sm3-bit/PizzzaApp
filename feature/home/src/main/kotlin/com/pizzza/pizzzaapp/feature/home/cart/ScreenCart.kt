package com.pizzza.pizzzaapp.feature.home.cart

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.feature.cart.CartItemCard
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.valu.uitaycompose.utils.*
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCart(
    onNavigateToAddressSelection: () -> Unit,
    onNavigateToSummary: () -> Unit,
) {
    val cartViewModel: CartViewModel = koinViewModel()
    val uiState by LocalAppDataOrder.current.state.collectAsStateWithLifecycle()
    val isButtonEnabled = if (uiState.receptionMode == "RECOJO") {
        uiState.cart.isNotEmpty()
    } else {
        (uiState.cart.isNotEmpty()) &&
                (uiState.deliveryAddress.isNotBlank()) &&
                (uiState.deliveryAddress != "Selecciona dirección en el mapa")
    }

    LaunchedEffect(Unit) {
        cartViewModel.loadUserAddress()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
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
                                val modes = listOf("DELIVERY" to "DOMICILIO", "RECOJO" to "LOCAL")
                                modes.forEach { (value, label) ->
                                    val isSelected = uiState.receptionMode == value
                                    Surface(
                                        onClick = { 
                                            cartViewModel.setReceptionMode(
                                                mode = value, 
                                                defaultDeliveryProduct = uiState.deliveryProducts.firstOrNull()
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
                                Text("Cambiar dirección de entrega", style = textS16, color = Color.Black)
                                Spacer(Modifier.height(4.dp))
                                Surface(
                                    onClick = onNavigateToAddressSelection,
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.Black),
                                    color = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = uiState.deliveryAddress.ifBlank { "Selecciona dirección en el mapa" },
                                            style = textM12,
                                            color = if (uiState.deliveryAddress.isBlank()) Color.Gray else Color.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color.Black
                                        )
                                    }
                                }
                            }
                            if (uiState.branches.size >= 2) {
                                Spacer(Modifier.height(16.dp))
                                Text("Elije sucursal", style = textB16, color = Color.Black)
                                Spacer(Modifier.height(8.dp))
                                
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(uiState.branches.size) { index ->
                                        val branch = uiState.branches[index]
                                        val isBranchSelected = uiState.branchId == branch.identifier
                                        Surface(
                                            onClick = { cartViewModel.selectBranch(branch.identifier) },
                                            modifier = Modifier.widthIn(min = 100.dp).height(32.dp),
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isBranchSelected) tay_green_600 else Color.White,
                                            border = if (!isBranchSelected) BorderStroke(1.dp, tay_green_600) else null
                                        ) {
                                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
                                                Text(
                                                    text = branch.nameBranch, 
                                                    style = textB10, 
                                                    color = if (isBranchSelected) Color.White else tay_green_600
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    items(uiState.cart) { item ->
                        CartItemCard(
                            item = item,
                            onRemove = {
                                cartViewModel.removeCartItem(item)
                            },
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
