package com.pizzza.pizzzaapp.ui.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.model.ParentOrderModel
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textM12

@Composable
fun ScreenOrder(viewModel: AppViewModel) {
    val uiState = viewModel.orderUiState

    LaunchedEffect(Unit) {
        viewModel.getGeneralOrderList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (uiState.orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes pedidos", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.orders) { order ->
                    OrderItemCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: ParentOrderModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pedido #${order.uid.takeLast(6)}", style = textB16, color = Color.Black)
                Surface(
                    color = when (order.state.uppercase()) {
                        "CONFIRMADO" -> Color(0xFFFFF3E0)
                        "LISTO" -> Color(0xFFE8F5E9)
                        "ENTREGADO" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF5F5F5)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = order.state.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = textB12,
                        color = when (order.state.uppercase()) {
                            "CONFIRMADO" -> Color(0xFFE65100)
                            "LISTO" -> Color(0xFF2E7D32)
                            "ENTREGADO" -> Color(0xFF1565C0)
                            else -> Color.Gray
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            
            Text(text = order.description, style = textM12, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = order.date, style = textM12, color = Color.LightGray)
                Text(text = "${order.symbol}${order.price}", style = textB14, color = tay_red_600)
            }
        }
    }
}
