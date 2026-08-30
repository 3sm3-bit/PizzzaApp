package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pizzza.pizzzaapp.R
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.swipe.UiTayUrlImage
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM12

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
        topBar = {},
        containerColor = Color.White,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                UiTayUrlImage(
                    modifier = Modifier
                        .fillMaxSize(),
                    url = product.urlImg,
                    drawable = R.drawable.peperoni)

                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_star),
                    contentDescription = "Volver",
                    modifier = Modifier
                        .padding(top = 32.dp, start = 16.dp)
                        .size(33.dp)
                        .align(Alignment.TopStart)
                        .clickable { onBack.invoke() }
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Nombre, Tamaño y Precio (Movido aquí)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.nameProduct.uppercase(),
                            style = textB20,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "TAMAÑO: ${product.tamanio}",
                            style = textM12,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = "${product.currencySymbol}${product.price}",
                        style = textB20,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .background(Color(0xFFF0F2F5), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = tay_green_600)
                        }
                        Text(
                            text = quantity.toString(),
                            style = textB18,
                            color = Color.Black
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = tay_green_600)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total a pagar", style = textM12, color = Color.Gray)
                        Text(
                            text = "${product.currencySymbol}${String.format("%.2f", totalPrice)}",
                            style = textB20,
                            color = tay_green_600
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Columna Izquierda: Descripción
                    Column(modifier = Modifier.weight(1.3f)) {
                        Text("Descripción", style = textB16,
                            color = tay_red_600)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = textM12,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nota", style = textB14, color = tay_red_600) },
                            placeholder = { Text("Instrucciones especiales (opcional)", style =
                                textM12.copy(color = Color.Gray)) },
                            minLines = 4,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = tay_red_600,
                                unfocusedBorderColor = tay_red_600,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            ),
                            textStyle = textM12.copy(color = Color.Black)
                        )
                    }

                    if (product.type == "1") {
                        VerticalDivider(
                            modifier = Modifier.height(180.dp),
                            thickness = 1.dp,
                            color = Color(0xFFF0F2F5)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Masa", style = textB14, color = tay_red_600)
                            Spacer(Modifier.height(4.dp))
                            listOf("TRADICIONAL", "CRUJIENTE").forEach { dough ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { typeDough = dough }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = dough, style = textM12,
                                        color = Color.DarkGray)
                                    RadioButton(
                                        selected = typeDough == dough,
                                        onClick = { typeDough = dough },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = tay_red_600,
                                            unselectedColor = Color.Gray
                                        ),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Orilla / Queso", style = textB14, color = tay_red_600)
                                    Text(
                                        modifier = Modifier.padding(top=4.dp),
                                        text = "+ ${product.currencySymbol}${product.priceChosse}",
                                        style = textM10,
                                        color = tay_green_600
                                    )
                                }
                                Switch(
                                    checked = cheeseFilledCrust,
                                    onCheckedChange = { cheeseFilledCrust = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = tay_red_600,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFDDDFE2),
                                        uncheckedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier.scale(0.7f)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Spacer(Modifier.fillMaxHeight())
            }
        }
    }
}

