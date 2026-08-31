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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.pizzza.pizzzaapp.R
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_green_800
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM14
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
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

    val context = LocalContext.current
    val houseIcon = remember(context) {
        bitmapDescriptorFromVector(context, R.drawable.ic_map_house)
    }
    val driverIcon = remember(context) {
        bitmapDescriptorFromVector(context, R.drawable.ic_map_boy)
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

    // Generar los puntos de la línea curva (Arco)
    val curvedPath = remember(deliveryLatLng, driverLatLng) {
        generateCurvedPath(driverLatLng, deliveryLatLng)
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
        cameraPositionState.animate(CameraUpdateFactory.newLatLngBounds(bounds, 150))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo de Pedido", style =
                    textB20,
                    color = tay_red_600) },
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

                Polyline(
                    points = curvedPath,
                    color = tay_green_800,
                    width = 4f,
                    geodesic = true
                )

                Marker(
                    state = rememberMarkerState(position = deliveryLatLng),
                    title = "Entrega",
                    snippet = order.address,
                    icon = houseIcon
                )

                // Marcador Repartidor (Moto)
                Marker(
                    state = rememberMarkerState(position = driverLatLng),
                    title = "Repartidor",
                    snippet = "En camino",
                    icon = driverIcon
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

/**
 * Genera una lista de puntos que forman un arco entre dos coordenadas LatLng
 */
private fun generateCurvedPath(start: LatLng, end: LatLng): List<LatLng> {
    val points = mutableListOf<LatLng>()
    val count = 50 // Precisión de la curva
    
    // Calculamos el punto medio
    val midLat = (start.latitude + end.latitude) / 2
    val midLng = (start.longitude + end.longitude) / 2
    
    // Calculamos una desviación para crear el "arco" (efecto ovalado)
    // Usamos una diferencia pequeña para que no sea exagerado
    val distLat = end.latitude - start.latitude
    val distLng = end.longitude - start.longitude
    
    // Punto de control para la curva Bezier cuadrática
    // Desviamos el punto medio perpendicularmente a la línea recta
    val offset = 0.2 // Factor de curvatura
    val controlPoint = LatLng(
        midLat + (distLng * offset),
        midLng - (distLat * offset)
    )

    for (i in 0..count) {
        val t = i.toDouble() / count
        // Fórmula de Bezier Cuadrática: (1-t)^2*P0 + 2(1-t)*t*P1 + t^2*P2
        val lat = (1 - t) * (1 - t) * start.latitude + 2 * (1 - t) * t * controlPoint.latitude + t * t * end.latitude
        val lng = (1 - t) * (1 - t) * start.longitude + 2 * (1 - t) * t * controlPoint.longitude + t * t * end.longitude
        points.add(LatLng(lat, lng))
    }
    
    return points
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {
    // Inicializar el SDK de mapas para evitar el error de IBitmapDescriptorFactory
    MapsInitializer.initialize(context)
    
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
