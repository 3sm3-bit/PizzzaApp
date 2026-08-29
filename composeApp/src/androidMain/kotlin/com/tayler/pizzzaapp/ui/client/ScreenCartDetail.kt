package com.tayler.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tayler.pizzzaapp.ui.CartViewModel
import com.tayler.pizzzaapp.ui.StoreViewModel
import com.tayler.pizzzaapp.ui.orders.OrderItem
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB10
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textS12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCartDetail(
    cartViewModel: CartViewModel,
    storeViewModel: StoreViewModel,
    onNavigateToSummary: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = cartViewModel.cartUiState
    val storeState = storeViewModel.storeUiState

    LaunchedEffect(Unit) {
        storeViewModel.getProductsList()
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personaliza Tu Pedido", style = textB20 ,
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
            if (uiState.cart.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tu carrito está vacío", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
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
                                Text("Zona de entrega", style = textB14, color = Color.Gray)
                                Spacer(Modifier.height(8.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    items(storeState.deliveryProducts) { deliveryProd ->
                                        val isSelected = uiState.selectedDeliveryProduct?.uid == deliveryProd.uid
                                        Card(
                                            onClick = { cartViewModel.setDeliveryProduct(deliveryProd) },
                                            modifier = Modifier.widthIn(min = 100.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) tay_red_600 else Color.White
                                            ),
                                            border = if (!isSelected) BorderStroke(1.dp, tay_red_600) else null
                                        ) {
                                            Box(modifier =
                                                Modifier.fillMaxWidth().padding(horizontal = 4.dp,
                                                vertical = 10.dp), contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = deliveryProd.description, 
                                                    style = textB12, 
                                                    color = if (isSelected) Color.White else Color.Black,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(Modifier.height(4.dp))
                                UiTayEditLayout(
                                    value = uiState.deliveryAddress,
                                    onValueChange = { cartViewModel.setDeliveryAddress(it) },
                                    hint = "Ingresa dirección de entrega",
                                    model = UiEditLayoutModel(
                                        uiStrokeActiveColor = tay_red_600,
                                        uiTextActiveColor = tay_red_600,
                                        uiTextColor = tay_red_600,
                                        uiTitleFont = textM10
                                    )
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFDDDFE2), thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    items(uiState.cart) { item ->
                        CartItemCard(
                            item = item,
                            onUpdateItem = { updatedItem ->
                                cartViewModel.updateCartItem(item, updatedItem)
                            },
                            onRemove = { cartViewModel.removeCartItem(item) }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                UiTayButton(
                    uiTayText = "Continuar con el Pedido",
                    uiTayClick = {
                        onNavigateToSummary.invoke()
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
}

@Composable
fun CartItemCard(
    item: OrderItem,
    onUpdateItem: (OrderItem) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Cabecera: Nombre y Borrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.product.nameProduct.uppercase()} ${if (item.product.type == "1") item.product.tamanio else ""}",
                    style = textB14,
                    color = tay_red_600
                )
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close,
                        contentDescription = null, tint = tay_red_600, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            if (item.product.type == "1") {
                // Cuerpo para Pizza (Type 1): Cantidad Vertical | Masa | Orilla
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Contador Vertical (Proporción 1)
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { onUpdateItem(item.copy(quantity = item.quantity + 1)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = tay_red_600)
                        }
                        Text(
                            text = item.quantity.toString(),
                            style = textB18,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { if (item.quantity > 1) onUpdateItem(item.copy(quantity = item.quantity - 1)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = tay_red_600)
                        }
                    }

                    Spacer(Modifier.width(4.dp))
                    VerticalDivider(modifier = Modifier.height(70.dp), thickness = 1.dp, color = tay_red_600)
                    Spacer(Modifier.width(4.dp))

                    // Tipo de Masa (Proporción 4)
                    Column(modifier = Modifier.weight(4f)) {
                        Text(text = "Tipo de Masa", style = textB12, color = Color.Black)
                        Spacer(Modifier.height(8.dp))
                        listOf("TRADICIONAL", "CRUJIENTE").forEach { dough ->
                            val isSelected = item.typeDough == dough
                            Surface(
                                onClick = { onUpdateItem(item.copy(typeDough = dough)) },
                                color = if (isSelected) tay_red_600 else Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, tay_red_600),
                                modifier = Modifier.height(28.dp).fillMaxWidth().padding(bottom = 4.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = dough, style = textB10, color = if (isSelected) Color.White else tay_red_600)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.width(4.dp))
                    VerticalDivider(modifier = Modifier.height(70.dp), thickness = 1.dp, color = tay_red_600)
                    Spacer(Modifier.width(4.dp))

                    // Orilla de Queso (Proporción 2)
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Orilla/Queso\n${item.product.currencySymbol}${item.product.priceChosse}",
                            style = textB12,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Switch(
                            checked = item.cheeseFilledCrust,
                            onCheckedChange = { onUpdateItem(item.copy(cheeseFilledCrust = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = tay_red_600,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFDDDFE2),
                                uncheckedBorderColor = Color.Transparent
                            ),
                            modifier = Modifier.scale(0.6f)
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Contador Horizontal
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = { if (item.quantity > 1) onUpdateItem(item.copy(quantity = item.quantity - 1)) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = tay_red_600)
                        }
                        Text(
                            text = item.quantity.toString(),
                            style = textB20,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { onUpdateItem(item.copy(quantity = item.quantity + 1)) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = tay_red_600)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            var showNoteField by remember { mutableStateOf(item.note.isNotBlank()) }

            Text(
                text = if (showNoteField) "borrar nota" else "agregar nota",
                style = textB12.copy(textDecoration = TextDecoration.Underline),
                color = if (showNoteField) Color.Gray else tay_green_600,
                modifier = Modifier.clickable {
                    if (showNoteField) {
                        onUpdateItem(item.copy(note = ""))
                    }
                    showNoteField = !showNoteField
                }
            )

            if (showNoteField) {
                Spacer(Modifier.height(4.dp))
                UiTayEditLayout(
                    value = item.note,
                    onValueChange = { onUpdateItem(item.copy(note = it)) },
                    hint = "Nota para tu pedido",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextActiveColor= tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTitleFont = textM10
                    )
                )
            }

            Spacer(Modifier.height(12.dp))
            
            val unitPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                    (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull() ?: 0.0 else 0.0)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total ", style = textB12, color = Color.Gray)
                Text(
                    text = "$${String.format("%.2f", unitPrice * item.quantity)}",
                    style = textB14,
                    color = tay_red_600
                )
            }
        }
    }
}
