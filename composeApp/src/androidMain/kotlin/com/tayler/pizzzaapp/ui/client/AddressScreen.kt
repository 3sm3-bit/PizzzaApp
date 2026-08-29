package com.tayler.pizzzaapp.ui.client

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.tayler.pizzzaapp.ui.auth.AuthViewModel
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB20
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Default position (Mexico City)
    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    var currentAddress by remember { mutableStateOf("Mueve el mapa para seleccionar") }
    var currentLatLng by remember { mutableStateOf(defaultLocation) }

    // Update address when camera stops moving
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            currentLatLng = center
            scope.launch {
                try {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    // Using old Geocoder API for compatibility with older devices/OS
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(center.latitude, center.longitude, 1)
                    if (addresses?.isNotEmpty() == true) {
                        currentAddress = addresses[0].getAddressLine(0)
                    }
                } catch (e: Exception) {
                    currentAddress = "Ubicación seleccionada"
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona tu Ubicación", style = textB20, color = tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = tay_red_600)
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
            )

            // Fixed Pin in the center
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = tay_red_600,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .offset(y = (-24).dp)
            )

            // Info and confirm button at the bottom
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
                    Text(
                        text = currentAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black,
                        maxLines = 2
                    )
                    Spacer(Modifier.height(16.dp))
                    UiTayButton(
                        uiTayText = "Confirmar Ubicación",
                        uiTayClick = {
                            viewModel.onRegisterFieldChange(
                                address = currentAddress,
                                latitude = currentLatLng.latitude.toString(),
                                longitude = currentLatLng.longitude.toString()
                            )
                            onBack()
                        },
                        uiTayBtnModifier = UiTayButtonModel(
                            uTBgColor = tay_red_600,
                            uTStrokeColor = tay_red_600,
                            uTBgSelectedColor = tay_red_600,
                            uTStrokeSelectedColor = tay_red_600,
                        )
                    )
                }
            }
        }
    }
}
