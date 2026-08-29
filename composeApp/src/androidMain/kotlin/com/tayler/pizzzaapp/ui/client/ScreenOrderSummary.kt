package com.tayler.pizzzaapp.ui.client

import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tayler.pizzzaapp.ui.CartViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.tay_yellow_900
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenOrderSummary(
    cartViewModel: CartViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = cartViewModel.cartUiState
    val cartProductsTotal = uiState.cart.sumOf {
        val basePrice = it.product.price.toDoubleOrNull() ?: 0.0
        val crustPrice = if (it.cheeseFilledCrust) it.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0
        (basePrice + crustPrice) * it.quantity
    }
    
    val deliveryPrice = uiState.selectedDeliveryProduct?.price?.toDoubleOrNull() ?: 0.0
    val cartTotal = cartProductsTotal + deliveryPrice

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirma tu pedido", style = textB20 ,
                    color=tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = tay_red_600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = tay_red_600
                )
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Casi listo! Revisa tu orden",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val modeText = if (uiState.receptionMode == "RECOJO") {
                        "Recojo en Local"
                    } else {
                        "Envío a Domicilio $${String.format("%.2f", deliveryPrice)}"
                    }
                    
                    Text(
                        text = modeText,
                        style = textB16,
                        color = if (uiState.receptionMode == "RECOJO") tay_red_600 else tay_green_600
                    )
                    
                    if (uiState.receptionMode == "DELIVERY" && uiState.deliveryAddress.isNotBlank()) {
                        Text(
                            text = uiState.deliveryAddress,
                            style = textB12,
                            color = Color.Gray
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                }

                items(uiState.cart) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.product.nameProduct}",
                                    fontWeight = FontWeight.Bold
                                )
                                if (item.product.type == "1") {
                                    Text(
                                        text = "${item.typeDough}${if (item.cheeseFilledCrust) " + Orilla Queso" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                if (item.note.isNotBlank()) {
                                    Text(
                                        text = "Nota: ${item.note}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            val itemPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                                    (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)

                            Text(
                                text = "$${String.format("%.2f", itemPrice * item.quantity)}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA62626)
                            )
                        }
                    }
                }

                if (uiState.receptionMode == "DELIVERY" && uiState.selectedDeliveryProduct != null) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Costo de Envío (${uiState.selectedDeliveryProduct.description})",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", deliveryPrice)}",
                                    fontWeight = FontWeight.Bold,
                                    color = tay_green_600
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1E21)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total a pagar",   style = textB14,
                        color = Color.White)
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "$${String.format("%.2f", cartTotal)}",
                        style = textB16,
                        color = tay_yellow_900,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            UiTayButton(
                uiTayText = "Confirmar Pedido",
                uiTayClick = {
                    onConfirm.invoke()
                },
                uiTayBtnModifier = UiTayButtonModel(
                    uTBgColor=tay_red_600,
                    uTStrokeColor=tay_red_600,
                    uTBgSelectedColor =tay_red_600,
                    uTStrokeSelectedColor=tay_red_600,
                )
            )
        }
    }
}
