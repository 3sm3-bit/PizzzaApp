package com.pizzza.pizzzaapp.feature.auth.register

import android.location.Geocoder
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.navigation.ScreenInitNav
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.feature.auth.AuthViewModel
import com.valu.uitaycompose.button.UiTayButton
import org.koin.compose.viewmodel.koinViewModel
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.*
import com.valu.uitaycompose.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateTo: (ScreenInitNav) -> Unit
) {
    val  viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()
    val isPhoneValid = uiState.phone.length == 10 && uiState.phone.all { it.isDigit() }

    val isButtonEnabled = uiState.nameUser.isNotBlank() &&
            uiState.names.isNotBlank() &&
            uiState.lastName.isNotBlank() &&
            isEmailValid &&
            uiState.pass.isNotBlank() &&
            isPhoneValid &&
            uiState.address.isNotBlank() &&
            uiState.address != "Selecciona dirección en el mapa"

    Scaffold(
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Image(
                        painter = painterResource(R.drawable.ic_logo_pizzzeria),
                        contentDescription = "logo_ic",
                        modifier = Modifier.width(350.dp).height(100.dp)
                    )
                }

                item {
                    UiTayEditLayout(
                        value = uiState.nameUser,
                        onValueChange = { viewModel.onRegisterFieldChange(nameUser = it) },
                        hint = "Nombre de usuario",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.names,
                        onValueChange = { viewModel.onRegisterFieldChange(names = it) },
                        hint = "Nombres",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.lastName,
                        onValueChange = { viewModel.onRegisterFieldChange(lastName = it) },
                        hint = "Apellidos",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.email,
                        onValueChange = { viewModel.onRegisterFieldChange(email = it) },
                        hint = "Correo electrónico",
                        keyboardType = KeyboardType.Email,
                        isError = uiState.email.isNotBlank() && !isEmailValid,
                        errorMessage = "Formato de correo inválido",
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.pass,
                        onValueChange = { viewModel.onRegisterFieldChange(pass = it) },
                        hint = "Contraseña",
                        isPassword = true,
                        imeAction = ImeAction.Next,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiIconColor = tay_grey_800,
                            uiIconActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    UiTayEditLayout(
                        value = uiState.phone,
                        onValueChange = { viewModel.onRegisterFieldChange(phone = it) },
                        hint = "Celular",
                        keyboardType = KeyboardType.Number,
                        maxLength = 10,
                        isError = uiState.phone.isNotBlank() && !isPhoneValid,
                        errorMessage = "Debe ser de 10 dígitos",
                        imeAction = ImeAction.Done,
                        model = UiEditLayoutModel(
                            uiStrokeActiveColor = tay_red_600,
                            uiTextColor = tay_red_600,
                            uiTextActiveColor = tay_red_600,
                            uiTitleActiveColor = tay_red_600,
                            uiTextFont = textM14,
                            uiTitleFont = textM14
                        )
                    )
                }
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dirección de domicilio",
                                style = textM12,
                                color = tay_red_600
                            )
                            Row(
                                modifier = Modifier
                                    .clickable { onNavigateTo(ScreenInitNav.AddressSelection(fromRegister = true)) }
                                    .padding(vertical = 2.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Seleccionar en mapa",
                                    tint = tay_green_600,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Elegir en mapa",
                                    style = textM12,
                                    color = tay_green_600
                                )
                            }
                        }

                        UiTayEditLayout(
                            value = uiState.address,
                            onValueChange = { newAddress ->
                                viewModel.onRegisterFieldChange(
                                    address = newAddress,
                                    latitude = "0",
                                    longitude = "0"
                                )
                            },
                            hint = "Ejemplo: Calle : numero,Ciudad",
                            imeAction = ImeAction.Done,
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor = tay_red_600,
                                uiHintColor = tay_grey_300,
                                uiTextFont = textM14,
                                uiTitleFont = textM12
                            )
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                UiTayButton(
                    uiTayText = "Registrarse",
                    uiTayEnable = isButtonEnabled,
                    uiTayClick = {
                        scope.launch(Dispatchers.IO) {
                            val currentUiState = viewModel.authUiState.value
                            if (currentUiState.latitude.isBlank() || currentUiState.longitude.isBlank() || currentUiState.latitude == "0" || currentUiState.longitude == "0") {
                                try {
                                    val geocoder = Geocoder(context, Locale.getDefault())
                                    val addressLower = currentUiState.address.lowercase()
                                    val addressQuery = if (!addressLower.contains("mexico") && !addressLower.contains("méxico") && !addressLower.contains("argentina") && !addressLower.contains("peru")) {
                                        "${currentUiState.address}, ${getCurrentCountryName()}"
                                    } else {
                                        currentUiState.address
                                    }
                                    val addresses = geocoder.getFromLocationName(addressQuery, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val lat = addresses[0].latitude.toString()
                                        val lng = addresses[0].longitude.toString()
                                        viewModel.onRegisterFieldChange(latitude = lat, longitude = lng)
                                    }
                                } catch (e: Exception) {
                                    Log.e("RegisterScreen", "Error geocodificando dirección: ${e.message}")
                                }
                            }
                            withContext(Dispatchers.Main) {
                                viewModel.register { _ ->
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                                    onNavigateTo(ScreenInitNav.Login)
                                }
                            }
                        }
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

private fun getCurrentCountryName(): String {
    return try {
        val countryCode = Locale.getDefault().country
        when (countryCode.uppercase()) {
            "MX" -> "Mexico"
            "AR" -> "Argentina"
            "PE" -> "Peru"
            "CO" -> "Colombia"
            "CL" -> "Chile"
            else -> "Mexico"
        }
    } catch (e: Exception) {
        "Mexico"
    }
}
