package com.pizzza.pizzzaapp.feature.monitoring

import android.content.Context
import android.graphics.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.core.ui.singleton.LocalAppDataOrder
import com.pizzza.pizzzaapp.feature.orders.OrdersViewModel
import com.valu.uitaycompose.utils.tay_green_800
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB16
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM14
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenMonitor(
    viewModel: OrdersViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val uiState by LocalAppDataOrder.current.state.collectAsStateWithLifecycle()
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
        bitmapDescriptorFromVector(context, R.drawable.ic_map_boy, sizeDp = 32)
    }

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

    val curvedPath = remember(deliveryLatLng, driverLatLng) {
        generateCurvedPath(driverLatLng, deliveryLatLng)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(driverLatLng, 15f)
    }

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
                title = {
                    Text(
                        text = "Monitoreo de Pedido",
                        style = textB20,
                        color = tay_red_600
                    )
                },
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

                Marker(
                    state = rememberMarkerState(position = driverLatLng),
                    title = "Repartidor",
                    snippet = "En camino",
                    icon = driverIcon
                )
            }

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

private fun generateCurvedPath(start: LatLng, end: LatLng): List<LatLng> {
    val points = mutableListOf<LatLng>()
    val count = 50
    
    val midLat = (start.latitude + end.latitude) / 2
    val midLng = (start.longitude + end.longitude) / 2

    val distLat = end.latitude - start.latitude
    val distLng = end.longitude - start.longitude

    val offset = 0.2
    val controlPoint = LatLng(
        midLat + (distLng * offset),
        midLng - (distLat * offset)
    )

    for (i in 0..count) {
        val t = i.toDouble() / count
        val lat = ((1 - t) * (1 - t) * start.latitude) + (2 * (1 - t) * t * controlPoint.latitude) + (t * t * end.latitude)
        val lng = ((1 - t) * (1 - t) * start.longitude) + (2 * (1 - t) * t * controlPoint.longitude) + (t * t * end.longitude)
        points.add(LatLng(lat, lng))
    }
    
    return points
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    sizeDp: Int? = null
): BitmapDescriptor? {
    MapsInitializer.initialize(context)
    
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    
    val density = context.resources.displayMetrics.density
    val width = sizeDp?.let { (it * density).toInt() } ?: drawable.intrinsicWidth
    val height = sizeDp?.let { (it * density).toInt() } ?: drawable.intrinsicHeight

    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, width, height)
    drawable.draw(canvas)
    
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
