package com.tayler.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tayler.pizzzaapp.R
import com.tayler.pizzzaapp.model.ProductModel
import com.tayler.pizzzaapp.ui.AppViewModel
import com.tayler.pizzzaapp.ui.CartViewModel
import com.tayler.pizzzaapp.ui.auth.AuthViewModel
import com.valu.uitaycompose.swipe.UiTayUrlImage
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_400
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM12
import com.valu.uitaycompose.utils.textM14
import com.valu.uitaycompose.utils.textS12

data class NavItemData(
    val index: Int,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenClientHome(
    viewModel: AppViewModel,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.orderUiState
    val cartState = cartViewModel.cartUiState
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Pizza, 1: Extra, 2: Cart, 3: Order
    var selectedSize by remember { mutableStateOf("CHICO") }
    var selectedExtraCategory by remember { mutableStateOf("TODOS") } // "EXTRAS", "BEBIDAS", "TODOS"

    val navItems = listOf(
        NavItemData(0, "Pizza", Icons.Default.LocalPizza),
        NavItemData(1, "Extra", Icons.Default.Fastfood),
        NavItemData(2, "Cart", Icons.Default.ShoppingCart),
        NavItemData(3, "Order", Icons.AutoMirrored.Filled.Assignment)
    )

    LaunchedEffect(Unit) {
        viewModel.getProductsList()
    }

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                border = BorderStroke(1.dp, tay_red_600),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                ) {
                    navItems.forEach { item ->
                        val isSelected = selectedTab == item.index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (item.index == 2) {
                                    onNavigateToCart()
                                } else {
                                    selectedTab = item.index
                                    if (item.index == 0) selectedSize = "CHICO"
                                    if (item.index == 1) selectedExtraCategory = "TODOS"
                                }
                            },
                            icon = {
                                if (item.index == 2) {
                                    BadgedBox(
                                        badge = {
                                            if (cartState.cart.isNotEmpty()) {
                                                Badge(containerColor = tay_green_600) {
                                                    Text(cartState.cart.sumOf { it.quantity }
                                                        .toString(),
                                                        color = Color.White)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(item.icon, contentDescription = null)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = null)
                                }
                            },
                            label = {
                                Text(
                                    item.label, style = if (isSelected) {
                                        textB12
                                    } else {
                                        textM12
                                    }, color =
                                        if (isSelected) {
                                            tay_red_600
                                        } else {
                                            Color.Gray
                                        }
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = tay_red_600,
                                selectedTextColor = tay_red_600,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Bienvenido a la pizzeria",
                        style = textM14,
                        color = tay_red_600
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = "Has tu pedido ya!",
                        style = textB20,
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartState.cart.isNotEmpty()) {
                                    Badge(containerColor = tay_green_600) {
                                        Text(cartState.cart.sumOf { it.quantity }.toString(),
                                            color= Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                modifier = Modifier
                                    .size(28.dp)
                                    .graphicsLayer {
                                        colorFilter = ColorFilter.tint(tay_red_400)
                                    }
                            )
                        }
                    }

                    IconButton(onClick = {
                        authViewModel.logout {
                            onLogout()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = tay_red_400,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Size Filter Chips for Pizza
                val sizes = listOf("CHICO", "MEDIANO", "GRANDE")
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sizes) { size ->
                        val isSelected = selectedSize == size
                        FilterChipSurface(
                            text = size,
                            isSelected = isSelected,
                            onClick = { selectedSize = size }
                        )
                    }
                }
            } else if (selectedTab == 1) {
                // Category Filter Chips for Extras
                val extraCategories = listOf("TODOS", "EXTRAS", "BEBIDAS")
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(extraCategories) { cat ->
                        val isSelected = selectedExtraCategory == cat
                        FilterChipSurface(
                            text = cat,
                            isSelected = isSelected,
                            onClick = { selectedExtraCategory = cat }
                        )
                    }
                }
            }

            // Content List
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val filteredPizzas = uiState.pizzaProducts.filter { product ->
                        when (selectedSize) {
                            "CHICO" -> product.tamanio.equals("CHICO", ignoreCase = true) ||
                                    product.tamanio.equals("CHICA", ignoreCase = true)

                            "MEDIANO" -> product.tamanio.equals("MEDIANO", ignoreCase = true) ||
                                    product.tamanio.equals("MEDIANA", ignoreCase = true)

                            "GRANDE" -> product.tamanio.equals("GRANDE", ignoreCase = true)
                            else -> false
                        }
                    }

                    items(filteredPizzas) { product ->
                        ProductCard(product) {
                            viewModel.selectProduct(product)
                            onNavigateToDetail()
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val filteredExtras = uiState.extraProducts.filter { product ->
                        when (selectedExtraCategory) {
                            "EXTRAS" -> product.type == "2"
                            "BEBIDAS" -> product.type == "3"
                            "TODOS" -> true
                            else -> false
                        }
                    }

                    items(filteredExtras) { product ->
                        ExtraProductCard(product) {
                            cartViewModel.addToCart(product)
                        }
                    }
                }
            }
        }
    }
}

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
        shadowElevation = if (isSelected) 4.dp else 0.dp
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
fun ProductCard(product: ProductModel, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        modifier = Modifier.fillMaxWidth().height(130.dp)
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

            Column(modifier = Modifier
                .weight(1f)
                .padding(16.dp)) {
                Text(
                    text = product.nameProduct,
                    style = textB18,
                    color = Color.Black
                )


                Text(modifier = Modifier
                    .padding(end = 24.dp, top = 4.dp),
                    text = product.description,
                    style = textM10,
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
                        onClick = onAdd,
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
fun ExtraProductCard(product: ProductModel, onAdd: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        modifier = Modifier.fillMaxWidth().height(220.dp)
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

                Spacer(Modifier.height(8.dp))

                // Precio y Botón de más a la derecha
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
                        onClick = onAdd,
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
