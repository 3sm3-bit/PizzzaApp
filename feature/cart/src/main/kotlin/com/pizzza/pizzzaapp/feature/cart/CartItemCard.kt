package com.pizzza.pizzzaapp.feature.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.ui.OrderItem
import com.valu.uitaycompose.utils.*
import java.util.Locale

@Composable
fun CartItemCard(
    item: OrderItem,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Cabecera: Cantidad, Nombre y Borrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = tay_red_600,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${item.quantity}x",
                            style = textB12,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = item.product.nameProduct.uppercase(),
                        style = textB14,
                        color = Color.Black
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = tay_red_600, modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Resumen de personalización (Solo si aplica)
            if (item.product.type == "1" || item.note.isNotBlank()) {
                Column {
                    if (item.product.type == "1") {
                        Row {
                            Text(
                                text = "Masa: ${item.typeDough}, ",
                                style = textM12,
                                color = Color.Gray
                            )
                            if (item.cheeseFilledCrust) {
                                Text(
                                    text = "Con Orilla de Queso",
                                    style = textM12,
                                    color = tay_green_600
                                )
                            }
                        }
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
            }

            Spacer(Modifier.height(12.dp))

            val unitPrice = (item.product.price.toDoubleOrNull() ?: 0.0) +
                    (if (item.cheeseFilledCrust) item.product.priceChosse.toDoubleOrNull()
                        ?: 0.0 else 0.0)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total ", style = textM12, color = Color.Gray)
                Text(
                    text = "$${String.format(Locale.US, "%.2f", unitPrice * item.quantity)}",
                    style = textB16,
                    color = tay_red_600
                )
            }
        }
    }
}
