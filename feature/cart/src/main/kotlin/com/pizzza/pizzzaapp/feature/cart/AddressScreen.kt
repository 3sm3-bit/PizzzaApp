package com.pizzza.pizzzaapp.feature.cart

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.*
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textM12
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Suppress("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressScreen(
    initialLat: String? = null,
    initialLng: String? = null,
    initialAddress: String? = null,
    onConfirm: (String, String, String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val initialLatLng = remember {
        val lat = initialLat?.toDoubleOrNull()
        val lng = initialLng?.toDoubleOrNull()
        if (lat != null && lng != null) LatLng(lat, lng) else null
    }

    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLatLng ?: defaultLocation, 15f)
    }

    var currentAddress by remember { mutableStateOf(initialAddress ?: "Obteniendo dirección...") }
    var currentLatLng by remember { mutableStateOf(initialLatLng ?: defaultLocation) }

    var searchQuery by remember { mutableStateOf("") }
    var placesResults by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var geocoderResults by remember { mutableStateOf<List<android.location.Address>>(emptyList()) }
    var isSelectingSearch by remember { mutableStateOf(false) }
    var sessionToken by remember { mutableStateOf<AutocompleteSessionToken?>(null) }

    val placesClient = remember {
        if (Places.isInitialized()) {
            Places.createClient(context)
        } else {
            null
        }
    }

    fun performGeocoderSearch(query: String) {
        android.util.Log.i("PIZZZA_PLACES", "🔄 Ejecutando búsqueda de respaldo con Geocoder para: '$query'")
        scope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 5)
                withContext(Dispatchers.Main) {
                    geocoderResults = addresses ?: emptyList()
                    android.util.Log.i("PIZZZA_PLACES", "📍 Geocoder devolvió ${geocoderResults.size} resultados")
                }
            } catch (e: Exception) {
                android.util.Log.e("PIZZZA_PLACES", "❌ Error en Geocoder: ${e.message}")
                withContext(Dispatchers.Main) {
                    geocoderResults = emptyList()
                }
            }
        }
    }

    fun searchAddress(query: String) {
        if (query.isBlank()) {
            placesResults = emptyList()
            geocoderResults = emptyList()
            sessionToken = null
            return
        }

        val token = sessionToken ?: AutocompleteSessionToken.newInstance().also { sessionToken = it }

        val client = placesClient
        if (client != null && Places.isInitialized()) {
            android.util.Log.i("PIZZZA_PLACES", "🔍 Buscando predicciones en Places API (con sesión) para: '$query'...")
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            client.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    val predictions = response.autocompletePredictions
                    android.util.Log.i("PIZZZA_PLACES", "✅ Places API devolvió ${predictions.size} predicciones")
                    if (predictions.isNotEmpty()) {
                        placesResults = predictions
                        geocoderResults = emptyList()
                    } else {
                        placesResults = emptyList()
                        performGeocoderSearch(query)
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.e("PIZZZA_PLACES", "❌ Error en Places API: ${exception.message}", exception)
                    placesResults = emptyList()
                    performGeocoderSearch(query)
                }
        } else {
            android.util.Log.w("PIZZZA_PLACES", "⚠️ Places Client es NULL o no inicializado. Usando Geocoder directamente.")
            performGeocoderSearch(query)
        }
    }

    fun selectPlacePrediction(prediction: AutocompletePrediction) {
        keyboardController?.hide()
        isSelectingSearch = true
        val placeId = prediction.placeId
        val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.ADDRESS)
        val currentToken = sessionToken
        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .setSessionToken(currentToken)
            .build()

        if (placesClient != null) {
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response ->
                    sessionToken = null
                    val place = response.place
                    val placeLatLng = place.latLng
                    val fullAddress = place.address ?: prediction.getFullText(null).toString()
                    if (placeLatLng != null) {
                        val latLng = LatLng(placeLatLng.latitude, placeLatLng.longitude)
                        currentAddress = fullAddress
                        currentLatLng = latLng
                        placesResults = emptyList()
                        searchQuery = ""
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                            )
                            isSelectingSearch = false
                        }
                    } else {
                        isSelectingSearch = false
                    }
                }
                .addOnFailureListener {
                    sessionToken = null
                    isSelectingSearch = false
                }
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun updateAddress(latLng: LatLng) {
        currentLatLng = latLng
        scope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    currentAddress = addresses[0].getAddressLine(0)
                }
            } catch (_: Exception) {
                currentAddress = "Ubicación seleccionada"
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateAddress(userLatLng)
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (initialLatLng != null) return@LaunchedEffect
        
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(it.latitude, it.longitude)
                    updateAddress(userLatLng)
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLatLng, 15f)
                        )
                    }
                }
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            if (!isSelectingSearch && cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE) {
                updateAddress(cameraPositionState.position.target)
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(color = Color.White) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Selecciona tu Ubicación",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(Color.White)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            )

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = tay_red_600,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .offset(y = (-24).dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        searchAddress(it)
                    },
                    placeholder = { Text("Buscar dirección o lugar...", style = textM12, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = tay_red_600) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                placesResults = emptyList()
                                geocoderResults = emptyList()
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = tay_red_600,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (placesResults.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)
                        ) {
                            items(placesResults) { prediction ->
                                val primaryText = prediction.getPrimaryText(null).toString()
                                val secondaryText = prediction.getSecondaryText(null).toString()
                                ListItem(
                                    headlineContent = {
                                        Text(text = primaryText, style = textM12.copy(fontWeight = FontWeight.Bold), color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                    supportingContent = {
                                        if (secondaryText.isNotBlank()) {
                                            Text(text = secondaryText, style = textM12, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectPlacePrediction(prediction)
                                        }
                                )
                                HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)
                            }
                        }
                    }
                } else if (geocoderResults.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp)
                        ) {
                            items(geocoderResults) { address ->
                                val addressText = address.getAddressLine(0) ?: "Dirección"
                                ListItem(
                                    headlineContent = {
                                        Text(text = addressText, style = textM12, color = Color.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            keyboardController?.hide()
                                            isSelectingSearch = true
                                            currentAddress = addressText
                                            val latLng = LatLng(address.latitude, address.longitude)
                                            currentLatLng = latLng
                                            geocoderResults = emptyList()
                                            searchQuery = ""
                                            scope.launch {
                                                cameraPositionState.animate(
                                                    update = CameraUpdateFactory.newLatLngZoom(latLng, 16f)
                                                )
                                                isSelectingSearch = false
                                            }
                                        }
                                )
                                HorizontalDivider(color = Color(0xFFF0F2F5), thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
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
                            onConfirm(
                                currentAddress,
                                currentLatLng.latitude.toString(),
                                currentLatLng.longitude.toString()
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
