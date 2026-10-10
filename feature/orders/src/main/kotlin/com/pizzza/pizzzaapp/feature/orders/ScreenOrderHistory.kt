package com.pizzza.pizzzaapp.feature.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB18
import com.valu.uitaycompose.utils.textM12
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenOrderHistory(
    historyViewModel: OrderHistoryViewModel = koinViewModel(),
    onNavigateToMonitor: () -> Unit = {},
    onBack: () -> Unit
) {
    val orders by historyViewModel.orders.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        historyViewModel.getOrderHistory()
    }
    Scaffold(
        topBar = {
            Surface(color = Color.White) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Historial de Pedidos",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(Color.White)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text("No tienes pedidos en tu historial", style = textB16, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(orders) { order ->
                        OrderHistoryItemCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderHistoryItemCard(
    order: ParentOrderModel
) {
    val displayState = when (order.state.uppercase()) {
        "CONFIRMADO" -> "PREPARANDO"
        "LISTO", "ENVIADO" -> "LISTO"
        "INICIADO" -> "EN CAMINO"
        "ENTREGADO" -> "ENTREGADO"
        else -> order.state.uppercase()
    }

    val descriptionText = if (order.description.isNotBlank()) {
        order.description
    } else {
        order.orders.joinToString(", ") { "${it.quantity}x ${it.nameProduct}" }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Estado y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (order.state.uppercase()) {
                        "CONFIRMADO" -> Color(0xFFFFF3E0)
                        "LISTO", "ENVIADO" -> Color(0xFFE8F5E9)
                        "INICIADO" -> Color(0xFFE3F2FD)
                        "ENTREGADO" -> Color(0xFFE8F5E9)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = displayState,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = textB12,
                        color = when (order.state.uppercase()) {
                            "CONFIRMADO" -> Color(0xFFE65100)
                            "LISTO", "ENVIADO" -> Color(0xFF2E7D32)
                            "INICIADO" -> Color(0xFF1565C0)
                            "ENTREGADO" -> Color(0xFF2E7D32)
                            else -> Color.Gray
                        }
                    )
                }

                Text(
                    text = order.date,
                    style = textM12,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(12.dp))

            if (descriptionText.isNotBlank()) {
                Text(
                    text = descriptionText,
                    style = textM12,
                    color = Color.Black
                )
                Spacer(Modifier.height(12.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = textM12,
                    color = Color.Gray
                )
                Text(
                    text = "${order.symbol}${order.price}",
                    style = textB18,
                    color = tay_red_600
                )
            }
        }
    }
}
