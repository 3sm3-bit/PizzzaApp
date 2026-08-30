package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.pizzza.pizzzaapp.ui.CartViewModel
import com.pizzza.pizzzaapp.ui.StoreViewModel
import com.pizzza.pizzzaapp.ui.auth.AuthViewModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_400
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM12
import com.valu.uitaycompose.utils.textM14

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
    storeViewModel: StoreViewModel,
    authViewModel: AuthViewModel,
    onNavigateToSummary: () -> Unit,
    onNavigateToAddressSelection: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onLogout: () -> Unit
) {
    val cartState = cartViewModel.cartUiState
    var selectedTab by rememberSaveable { mutableIntStateOf(cartState.initialTab) } // Dinámico basado en el estado
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cartState.initialTab) {
        if (cartState.initialTab != 0) {
            selectedTab = cartState.initialTab
            cartViewModel.setInitialTab(0) // Resetear después de aplicar
        }
    }

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
                modifier = Modifier.navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                border = BorderStroke(1.dp, tay_red_600.copy(alpha = 0.5f)),
                color = Color.White,
                shadowElevation = 24.dp
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(65.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                ) {
                    navItems.forEach { item ->
                        val isSelected = selectedTab == item.index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                selectedTab = item.index
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
                    .padding(start=16.dp, top = 16.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Textos de Cabecera Dinámicos
                Column {
                    Text(
                        text = if (selectedTab == 3) "Monitorea tus pedidos aquí" else "Bienvenido a la pizzeria",
                        style = textM14,
                        color = tay_red_600
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = if (selectedTab == 3) "Tus pedidos de hoy" else "Has tu pedido ya!",
                        style = textB20,
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // El carrito solo aparece en Pizza (0) o Extra (1)
                    if (selectedTab == 0 || selectedTab == 1) {
                        IconButton(onClick = { selectedTab = 2 }) {
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
                    }

                    if (selectedTab == 3) {
                        // Botón de Refrescar Pedidos
                        IconButton(onClick = { viewModel.getGeneralOrderList(forceLoading = true) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refrescar",
                                tint = tay_red_400,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        // Botón de Cerrar Sesión (En las otras pestañas)
                        IconButton(onClick = { showLogoutDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar Sesión",
                                tint = tay_red_400,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text(text = "Cerrar Sesión", style = textB20, color = tay_red_600) },
                    text = { Text(text = "¿Estás seguro de que deseas cerrar sesión?",
                        style = textM14,color = Color.Gray) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                viewModel.resetOrderState()
                                authViewModel.logout {
                                    onLogout()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = tay_red_600)
                        ) {
                            Text("Sí, salir", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
                            Text("No", color = Color.Gray)
                        }
                    },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            when (selectedTab) {
                0 -> ScreenPizza(viewModel, onNavigateToDetail)
                1 -> ScreenExtra(viewModel, onNavigateToDetail)
                2 -> ScreenCart(
                    cartViewModel, 
                    storeViewModel, 
                    onNavigateToAddressSelection = onNavigateToAddressSelection,
                    onNavigateToSummary = onNavigateToSummary
                )
                3 -> ScreenOrder(viewModel)
            }
        }
    }
}