package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM12
import com.valu.uitaycompose.utils.textM14

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenOrderSummary(
    cartViewModel: CartViewModel,
    onConfirm: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
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
                title = { Text("Resumen de Pedido", style = textB20, color = tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = tay_red_600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Detalles de tu Orden",
                            style = textB18,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Lista de Productos (Ya no necesita peso ni scroll propio)
                        uiState.cart.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.quantity}x ${item.product.nameProduct.uppercase()}",
                                        style = textB14,
                                        color = Color.Black
                                    )
                                    if (item.product.type == "1") {
                                        Text(
                                            text = "Masa: ${item.typeDough}${if (item.cheeseFilledCrust) " + Orilla Queso" else ""}",
                                            style = textM12,
                                            color = Color.Gray
                                        )
                                    }
                                    if (item.note.isNotBlank()) {
                                        Text(
                                            text = "Nota: ${item.note}",
                                            style = textM12,
                                            color = Color.Gray,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                                val itemPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                                        (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)
                                Text(
                                    text = "$${String.format("%.2f", itemPrice * item.quantity)}",
                                    style = textB14,
                                    color = Color.Black
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 1.dp)
                        }

                        if (uiState.receptionMode == "DELIVERY" && uiState.selectedDeliveryProduct != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Envío:",
                                    style = textM14,
                                    color = tay_green_600
                                )
                                Text(
                                    text = "$${String.format("%.2f", deliveryPrice)}",
                                    style = textB14,
                                    color = tay_green_600
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Información de Entrega
                        Surface(
                            color = Color(0xFFF0F2F5),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (uiState.receptionMode == "RECOJO") "RECOJO EN LOCAL" else "ENTREGA A DOMICILIO",
                                    style = textB12,
                                    color = tay_red_600
                                )
                                if (uiState.receptionMode == "DELIVERY") {
                                    Text(
                                        text = uiState.deliveryAddress,
                                        style = textM12,
                                        color = Color.Gray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Total a Pagar", style = textB16, color = Color.Black)
                            Text(
                                text = "$${String.format("%.2f", cartTotal)}",
                                style = textB20,
                                color = tay_red_600
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            UiTayButton(
                uiTayText = "Confirmar y Enviar Pedido",
                uiTayClick = {
                    cartViewModel.confirmOrder {
                        Toast.makeText(context, "Orden generada", Toast.LENGTH_LONG).show()
                        onConfirm(3) // Navegar a la pestaña de Pedidos (index 3)
                    }
                },
                uiTayBtnModifier = UiTayButtonModel(
                    uTBgColor = tay_red_600,
                    uTStrokeColor = tay_red_600,
                    uTBgSelectedColor = tay_red_600,
                    uTStrokeSelectedColor = tay_red_600,
                )
            )
        }
    }
}
