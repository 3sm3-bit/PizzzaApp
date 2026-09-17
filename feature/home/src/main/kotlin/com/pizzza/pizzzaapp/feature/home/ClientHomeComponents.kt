package com.pizzza.pizzzaapp.feature.home

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.graphicsLayer
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
            .height(30.dp)
            .widthIn(min = 100.dp),
        shadowElevation = if (isSelected) 4.dp else 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = text,
                color = if (isSelected) Color.White else tay_red_400,
                style = textB10
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
            .height(160.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                Text(
                    text = product.nameProduct,
                    style = textB12,
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
                        style = textB14,
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

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun PromotionsBanner(promotions: List<ProductModel>, onProductClick: (ProductModel) -> Unit) {
    if (promotions.isEmpty()) return

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    
    val isMultiple = promotions.size > 1
    val cardWidth = if (isMultiple) screenWidth - 64.dp else screenWidth - 32.dp
    val horizontalPadding = 16.dp

    val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy((-18).dp),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(lazyListState = lazyListState)
        ) {
            items(promotions.size) { index ->
                val product = promotions[index]
                val scale by remember {
                    derivedStateOf {
                        val layoutInfo = lazyListState.layoutInfo
                        val visibleItems = layoutInfo.visibleItemsInfo
                        val itemInfo = visibleItems.find { it.index == index }
                        
                        if (itemInfo != null) {
                            val center = layoutInfo.viewportEndOffset / 2f
                            val itemCenter = itemInfo.offset + (itemInfo.size / 2f)
                            val distanceFromCenter = kotlin.math.abs(center - itemCenter)
                            val scale = 1f - (distanceFromCenter / layoutInfo.viewportEndOffset).coerceIn(0f, 0.15f)
                            scale
                        } else {
                            0.85f
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .width(cardWidth)
                        .height(140.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = scale.coerceIn(0.7f, 1f)
                        },
                    onClick = { onProductClick(product) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        UiTayUrlImage(
                            url = product.urlImg
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PizzaGridCard(product: ProductModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                UiTayUrlImage(
                    url = product.urlImg, drawable = R.drawable.peperoni
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = product.nameProduct,
                        style = textB12,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = product.description,
                        style = textSe8,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${product.currencySymbol}${product.price}",
                        style = textB12,
                        color = tay_green_600,
                    )

                    Surface(
                        onClick = onClick,
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, tay_green_600),
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = tay_green_600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


