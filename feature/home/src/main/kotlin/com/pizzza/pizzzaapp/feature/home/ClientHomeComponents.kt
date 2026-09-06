package com.pizzza.pizzzaapp.feature.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.model.ProductModel
import com.valu.uitaycompose.swipe.UiTayUrlImage
import com.valu.uitaycompose.utils.*
import java.util.Locale

@Composable
fun FilterChipSurface(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) tay_red_600 else Color.White,
        shape = RoundedCornerShape(12.dp),
        border = if (!isSelected) BorderStroke(1.dp, tay_red_600) else null,
        modifier = Modifier
            .height(35.dp)
            .widthIn(min = 100.dp),
        shadowElevation = if (isSelected) 4.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.White else tay_red_400,
                style = textS12
            )
        }
    }
}

@Composable
fun ProductCard(product: ProductModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(120.dp)

            ) {
                UiTayUrlImage(
                    url = product.urlImg, drawable = R.drawable.peperoni
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = product.nameProduct,
                    style = textGabbiB18,
                    color = Color.Black
                )


                Text(
                    modifier = Modifier
                        .padding(end = 24.dp, top = 4.dp),
                    text = product.description,
                    style = textSe10,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${product.currencySymbol}${product.price}",
                        style = textB20,
                        color = tay_green_600,
                    )

                    Surface(
                        onClick = onClick,
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, tay_green_600),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = tay_green_600
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ExtraProductCard(product: ProductModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen en la parte superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                UiTayUrlImage(
                    url = product.urlImg, drawable = R.drawable.peperoni
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Nombre del producto
                Text(
                    text = product.nameProduct,
                    style = textB14,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.currencySymbol}${product.price}",
                        style = textB16,
                        color = tay_green_600,
                    )

                    Surface(
                        onClick = onClick,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, tay_green_600),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = tay_green_600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

