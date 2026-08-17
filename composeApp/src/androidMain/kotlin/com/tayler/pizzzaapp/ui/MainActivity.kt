package com.tayler.pizzzaapp.ui

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterHeader(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelected = { viewModel.updateFilter(it) },
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = { viewModel.refresh() },
                    modifier = Modifier
                        .size(48.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            if (viewModel.uiStateBase.loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF007BFF),
                    trackColor = Color(0xFFE4E6EB)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(uiState.filteredOrders) { order ->
                    OrderCard(order = order, backgroundColor = cardBackground, textColor = darkText)
                }
            }
        }
    }
}

@Composable
fun FilterHeader(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val filters = listOf("TODOS", "PENDIENTE", "CONFIRMADO", "PREPARANDO")

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFDDDFE2), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Estado: $selectedFilter",
                style = textB16,
                color = Color(0xFF1C1E21)
            )
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = Color(0xFF65676B)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            filters.forEach { filter ->
                DropdownMenuItem(
                    text = { Text(filter, color = Color(0xFF1C1E21)) },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OrderCard(order: ParentOrderModel, backgroundColor: Color, textColor: Color) {
    val statusColor = when (order.state.uppercase()) {
        "PENDIENTE" -> Color(0xFFF59E0B) // Amber
        "CONFIRMADO" -> Color(0xFF3B82F6) // Blue
        "PREPARANDO" -> Color(0xFF10B981) // Green
        else -> Color(0xFF6B7280) // Grey
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(statusColor)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pedido #${order.uid.takeLast(4)}",
                        style = textB14,
                        color = Color(0xFF65676B)
                    )
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = order.state.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = textB12,
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OrderInfoRow(icon = Icons.Default.Person, label = "Cliente", value = order.nameClient, textColor = textColor)
                OrderInfoRow(icon = Icons.Default.Edit, label = "Nota", value = order.description, textColor = textColor)
                OrderInfoRow(icon = Icons.Default.AccountBalanceWallet, label = "Total", value = "$${order.price}", textColor = textColor)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDFE2))
                    ) {
                        Text("Detalles", style = textS14, color = Color(0xFF1C1E21))
                    }
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007BFF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Avanzar", style = textS14, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderInfoRow(icon: ImageVector, label: String, value: String, textColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF8A8D91)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label:",
            style = textS14,
            color = Color(0xFF65676B),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = value,
            style = textM14,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
    }
}
