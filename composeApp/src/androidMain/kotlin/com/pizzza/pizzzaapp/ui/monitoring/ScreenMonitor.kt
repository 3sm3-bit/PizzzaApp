package com.pizzza.pizzzaapp.ui.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM14
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMonitor(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val uiState = viewModel.orderUiState
    val order = uiState.selectedOrder

    if (order == null) {
        onBack()
        return
    }

    // Polling cada 1 minuto
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.getOrderDetail(order.uid)
            delay(1.minutes)
        }
    }

    val deliveryLatLng = remember(order.latitude, order.longitude) {
        LatLng(
            order.latitude.toDoubleOrNull() ?: 0.0,
            order.longitude.toDoubleOrNull() ?: 0.0
        )
    }
    val driverLatLng = remember(order.currentLatitude, order.currentLongitude) {
        LatLng(
            order.currentLatitude.toDoubleOrNull() ?: 0.0,
            order.currentLongitude.toDoubleOrNull() ?: 0.0
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLatLng, 15f)
    }

    // Ajustar cámara para mostrar ambos marcadores
    LaunchedEffect(deliveryLatLng, driverLatLng) {
        val bounds = LatLngBounds.builder()
            .include(deliveryLatLng)
            .include(driverLatLng)
            .build()
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo de Pedido", style = textB20, color = tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = tay_red_600
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                // Marcador Entrega (Casa)
                Marker(
                    state = rememberMarkerState(position = deliveryLatLng),
                    title = "Entrega",
                    snippet = order.address
                )

                // Marcador Repartidor (Moto)
                Marker(
                    state = rememberMarkerState(position = driverLatLng),
                    title = "Repartidor",
                    snippet = "En camino"
                )
            }

            // Info Card at the bottom
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Estado: EN CAMINO", style = textB16, color = tay_red_600)
                    Spacer(Modifier.height(4.dp))
                    Text(text = "Tu repartidor está acercándose a tu ubicación.", style = textM14, color = Color.Gray)
                }
            }
        }
    }
}
