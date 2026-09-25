package com.pizzza.pizzzaapp.feature.home.cart

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.feature.cart.CartViewModel
import com.pizzza.pizzzaapp.feature.cart.CartItemCard
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCart(
    onNavigateToAddressSelection: () -> Unit,
    onNavigateToSummary: () -> Unit,
) {
    val cartViewModel: CartViewModel = koinViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Dirección de domicilio",
                                            style = textM12,
                                            color = tay_red_600
                                        )
                                        Row(
                                            modifier = Modifier
                                                .clickable { onNavigateToAddressSelection() }
                                                .padding(vertical = 2.dp, horizontal = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = "Seleccionar en mapa",
                                                tint = tay_green_600,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Elegir en mapa",
                                                style = textM12,
                                                color = tay_green_600
                                            )
                                        }
                                    }

                                    UiTayEditLayout(
                                        value = uiState.deliveryAddress,
                                        onValueChange = { newAddress ->
                                            cartViewModel.updateDeliveryAddress(
                                                address = newAddress,
                                                lat = "0",
                                                lng = "0"
                                            )
                                        },
                                        hint = "Ejemplo: Calle : numero,Ciudad",
                                        imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                                        model = UiEditLayoutModel(
                                            uiStrokeActiveColor = tay_red_600,
                                            uiTextColor = tay_red_600,
                                            uiTextActiveColor = tay_red_600,
                                            uiTitleActiveColor = tay_red_600,
                                            uiHintColor = tay_grey_300,
                                            uiTextFont = textM14,
                                            uiTitleFont = textM12
                                        )
                                    )
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
                    onClick = { 
                        if (isButtonEnabled) {
                            scope.launch(Dispatchers.IO) {
                                val currentState = uiState
                                if (currentState.receptionMode == "DELIVERY" && (currentState.latitude.isBlank() || currentState.longitude.isBlank() || currentState.latitude == "0" || currentState.longitude == "0")) {
                                    try {
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val addressLower = currentState.deliveryAddress.lowercase()
                                        val addressQuery = if (!addressLower.contains("mexico") && !addressLower.contains("méxico") && !addressLower.contains("argentina") && !addressLower.contains("peru")) {
                                            "${currentState.deliveryAddress}, ${getCurrentCountryName()}"
                                        } else {
                                            currentState.deliveryAddress
                                        }
                                        val addresses = geocoder.getFromLocationName(addressQuery, 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            val lat = addresses[0].latitude.toString()
                                            val lng = addresses[0].longitude.toString()
                                            cartViewModel.updateDeliveryAddress(currentState.deliveryAddress, lat, lng)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("ScreenCart", "Error geocodificando dirección: ${e.message}")
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    onNavigateToSummary()
                                }
                            }
                        }
                    },
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

private fun getCurrentCountryName(): String {
    return try {
        val countryCode = Locale.getDefault().country
        when (countryCode.uppercase()) {
            "MX" -> "Mexico"
            "AR" -> "Argentina"
            "PE" -> "Peru"
            "CO" -> "Colombia"
            "CL" -> "Chile"
            else -> "Mexico"
        }
    } catch (_: Exception) {
        "Mexico"
    }
}
