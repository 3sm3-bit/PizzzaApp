package com.tayler.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tayler.pizzzaapp.R
import com.tayler.pizzzaapp.ui.AppViewModel
import com.tayler.pizzzaapp.ui.CartViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.swipe.UiTayUrlImage
import com.valu.uitaycompose.utils.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenDetailOrder(
    viewModel: AppViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit
) {
    val product = viewModel.orderUiState.selectedProduct ?: return
    
    var quantity by remember { mutableIntStateOf(1) }
    var typeDough by remember { mutableStateOf("TRADICIONAL") }
    var cheeseFilledCrust by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }

    val basePrice = product.price.toDoubleOrNull() ?: 0.0
    val crustPrice = if (cheeseFilledCrust) product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0
    val totalPrice = (basePrice + crustPrice) * quantity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.nameProduct.uppercase(), style = textB20, color = tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = tay_red_600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Imagen del Producto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                UiTayUrlImage(url = product.urlImg, drawable = R.drawable.peperoni)
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Tamaño y Precio Unitario
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFF0F2F5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "TAMAÑO: ${product.tamanio}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = textB12,
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        text = "${product.currencySymbol}${product.price}",
                        style = textB20,
                        color = tay_green_600
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Selector de Cantidad
                Text("Cantidad", style = textB16, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier.size(40.dp).background(Color(0xFFF0F2F5), RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = tay_red_600)
                    }
                    Text(text = quantity.toString(), style = textB20, fontWeight = FontWeight.ExtraBold)
                    IconButton(
                        onClick = { quantity++ },
                        modifier = Modifier.size(40.dp).background(Color(0xFFF0F2F5), RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = tay_red_600)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Tipo de Masa
                Text("Tipo de Masa", style = textB16, color = Color.Black)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf("TRADICIONAL", "CRUJIENTE").forEach { dough ->
                        val isSelected = typeDough == dough
                        Surface(
                            onClick = { typeDough = dough },
                            modifier = Modifier.weight(1f).height(45.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) tay_red_600 else Color.White,
                            border = if (!isSelected) BorderStroke(1.dp, tay_red_600) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = dough, style = textB14, color = if (isSelected) Color.White else tay_red_600)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Orilla de Queso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Orilla rellena de Queso", style = textB16, color = Color.Black)
                        Text(
                            text = "+ ${product.currencySymbol}${product.priceChosse}",
                            style = textM12,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = cheeseFilledCrust,
                        onCheckedChange = { cheeseFilledCrust = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = tay_red_600
                        )
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Nota
                UiTayEditLayout(
                    value = note,
                    onValueChange = { note = it },
                    hint = "Instrucciones especiales (opcional)",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleFont = textM10
                    )
                )

                Spacer(Modifier.height(32.dp))

                // Total y Botón Agregar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total", style = textM12, color = Color.Gray)
                        Text(
                            text = "${product.currencySymbol}${String.format("%.2f", totalPrice)}",
                            style = textB20,
                            color = tay_red_600
                        )
                    }
                    
                    Box(modifier = Modifier.width(200.dp)) {
                        UiTayButton(
                            uiTayText = "Agregar al Carrito",
                            uiTayClick = {
                                cartViewModel.addToCart(
                                    product = product,
                                    quantity = quantity,
                                    typeDough = typeDough,
                                    cheeseFilledCrust = cheeseFilledCrust,
                                    note = note
                                )
                                onBack()
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
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
