package com.pizzza.pizzzaapp.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.extra.UiTayCToolBar
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.model.UiToolBarModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_grey_800
import com.valu.uitaycompose.utils.tay_red_50
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM12
import com.valu.uitaycompose.utils.textM14

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToAddressSelection: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.authUiState

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()
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
        topBar = {
            Surface(
                color = tay_red_50,
                shadowElevation = 4.dp // Darle un poco de sombra para que no sea traslúcido
            ) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Pizzzeria 3 Z",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(tay_red_600)
                    ) { _ ->
                        onBack.invoke()
                    }
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F2F5)) // Asegurar fondo sólido
                    .navigationBarsPadding()
                    .imePadding() // Importante: el botón debe subir con el teclado si es necesario
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                val context = LocalContext.current
                UiTayButton(
                    uiTayText = "Registrarse",
                    uiTayEnable = isButtonEnabled,
                    uiTayClick = {
                        viewModel.register { _ ->
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                            onRegisterSuccess()
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
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Scaffold padding maneja topBar y bottomBar
                .imePadding(), // Solo el contenido de la lista se ajusta al teclado
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Registrate ahora",
                    style = textB20,
                    color = tay_red_600)
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
                        uiTitleActiveColor= tay_red_600,
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
                        uiTitleActiveColor= tay_red_600,
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
                        uiTitleActiveColor= tay_red_600,
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
                        uiTitleActiveColor= tay_red_600,
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
                        uiTitleActiveColor= tay_red_600,
                        uiIconColor = tay_grey_800,
                        uiIconActiveColor=  tay_red_600,
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
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM14,
                        uiTitleFont = textM14
                    )
                )
            }
            item {
                Surface(
                    onClick = onNavigateToAddressSelection,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, tay_green_600),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (uiState.address.isBlank()) "Selecciona dirección en el mapa" else uiState.address,
                            style = textM12,
                            color = if (uiState.address.isBlank()) Color.Gray else tay_red_600,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = tay_green_600
                        )
                    }
                }
            }
        }
    }
}
