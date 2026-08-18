package com.tayler.pizzzaapp.ui

import android.os.Build
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.messaging.FirebaseMessaging
import com.tayler.pizzzaapp.entity.ParentOrderModel
import com.tayler.pizzzaapp.ui.base.BaseActivity
import com.tayler.pizzzaapp.ui.base.BaseViewModel
import com.valu.uitaycompose.utils.*
import com.valu.uitaycompose.utils.permission.rememberUiTayPermissionManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private val viewModel : AppViewModel by viewModel()

    @Composable
    override fun SetScreenConfig() {
        App(viewModel)
    }

    override fun setDataGlobal() {
          viewModel.getGeneralOrderList()
          printFirebaseToken()
    }

    private fun printFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Current token: $token")
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel
}

@Composable
fun App(viewModel: AppViewModel) {
    val permissionManager = rememberUiTayPermissionManager()
    val uiState = viewModel.orderUiState

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS) {
                Log.d("FCM", "Notification permission granted")
            }
        }
    }

    MaterialTheme {
        val lightBackground = Color(0xFFF0F2F5)
        val cardBackground = Color.White
        val darkText = Color(0xFF1C1E21)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(lightBackground)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gestión de Pedidos",
                    style = textB20,
                    color = darkText
                )
                
                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFDDDFE2), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refrescar",
                        tint = Color(0xFF007BFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indicadores de estado (Resumen)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusIndicator(
                    text = "CONFIRMADO",
                    count = uiState.countConfirmado,
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                StatusIndicator(
                    text = "LISTO",
                    count = uiState.countListo,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.filteredOrders.isEmpty() && !viewModel.uiStateBase.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No se encontraron pedidos",
                            style = textM16,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Intentar de nuevo")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(uiState.orders) { order ->
                        OrderCard(
                            order = order,
                            backgroundColor = cardBackground,
                            textColor = darkText,
                            onStateChange = { action ->
                                if (action == "AVANZAR") {
                                    viewModel.avanzarEstado(order)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: ParentOrderModel,
    backgroundColor: Color,
    textColor: Color,
    onStateChange: (String) -> Unit
) {
    val statusColor = if (order.state.trim().uppercase() == "CONFIRMADO") Color(0xFF3B82F6) else Color(0xFF10B981)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                // Fila 1: Nombre del Cliente y Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.nameClient,
                        style = textB16,
                        color = textColor,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = order.state.uppercase(),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = textB10,
                            color = statusColor
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F2F5))

                // Desglose de Productos (Orders)
                order.orders.forEach { item ->
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity} ${item.nameProduct} ${item.typeDough}",
                                style = textM14,
                                color = textColor,
                                modifier = Modifier.weight(1f)
                            )
                            val subtotal = (item.quantity.toDoubleOrNull() ?: 0.0) * (item.price.toDoubleOrNull() ?: 0.0)
                            Text(
                                text = "$${subtotal.toInt()}",
                                style = textB14,
                                color = textColor
                            )
                        }

                        if (item.cheeseFilledCrust.trim().uppercase() == "SI") {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "con orilla de queso",
                                    style = textS12,
                                    color = Color(0xFF65676B)
                                )
                                Text(
                                    text = "$${item.priceChosse}",
                                    style = textS12,
                                    color = Color(0xFF65676B)
                                )
                            }
                        }

                        if (item.note.isNotBlank()) {
                            Text(
                                text = "Nota: ${item.note}",
                                fontSize = 10.sp,
                                color = Color(0xFF8A8D91),
                                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F2F5))

                // Logística y Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val isDelivery = order.reception.trim().uppercase() == "DELIVERY"
                        Text(
                            text = if (isDelivery) "🏠 DELIVERY" else "🛍️ RECOJO EN LOCAL",
                            style = textB12,
                            fontSize = 11.sp,
                            color = if (isDelivery) Color(0xFFE91E63) else Color(0xFF007BFF)
                        )
                        if (isDelivery && order.address.isNotBlank()) {
                            Text(
                                text = order.address,
                                style = textS12,
                                fontSize = 11.sp,
                                color = Color(0xFF65676B),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "TOTAL", style = textB10, color = Color(0xFF8A8D91))
                        Text(
                            text = "$${order.price}",
                            style = textB20,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fila 3: Botones de Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Mostrar Detalle */ },
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF0F2F5)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "DETALLE",
                            fontSize = 12.sp,
                            style = textB12,
                            color = Color(0xFF1C1E21)
                        )
                    }
                    Button(
                        onClick = { onStateChange("AVANZAR") },
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = statusColor
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "AVANZAR",
                            fontSize = 12.sp,
                            style = textB12,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(
    text: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = textB12,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = count.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = textB14,
                    color = Color.White
                )
            }
        }
    }
}
