package com.tayler.pizzzaapp.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import android.widget.Toast
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB12
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.authUiState

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()
    val isPhoneValid = uiState.phone.length == 10 && uiState.phone.all { it.isDigit() }

    val isButtonEnabled = uiState.nameUser.isNotBlank() &&
            uiState.names.isNotBlank() &&
            uiState.lastName.isNotBlank() &&
            uiState.document.isNotBlank() &&
            isEmailValid &&
            uiState.pass.isNotBlank() &&
            isPhoneValid &&
            uiState.address.isNotBlank() &&
            uiState.longitude.isNotBlank() &&
            uiState.latitude.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario", style = textB20, color = tay_red_600) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = tay_red_600)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                UiTayEditLayout(
                    value = uiState.nameUser,
                    onValueChange = { viewModel.onRegisterFieldChange(nameUser = it) },
                    hint = "Nombre de usuario",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.names,
                    onValueChange = { viewModel.onRegisterFieldChange(names = it) },
                    hint = "Nombres",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.lastName,
                    onValueChange = { viewModel.onRegisterFieldChange(lastName = it) },
                    hint = "Apellidos",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.document,
                    onValueChange = { viewModel.onRegisterFieldChange(document = it) },
                    hint = "Documento",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
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
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.pass,
                    onValueChange = { viewModel.onRegisterFieldChange(pass = it) },
                    hint = "Contraseña",
                    isPassword = true,
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.phone,
                    onValueChange = { viewModel.onRegisterFieldChange(phone = it) },
                    hint = "Teléfono",
                    keyboardType = KeyboardType.Phone,
                    isError = uiState.phone.isNotBlank() && !isPhoneValid,
                    errorMessage = "Debe ser de 10 dígitos",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                UiTayEditLayout(
                    value = uiState.address,
                    onValueChange = { viewModel.onRegisterFieldChange(address = it) },
                    hint = "Dirección",
                    model = UiEditLayoutModel(
                        uiStrokeActiveColor = tay_red_600,
                        uiTextColor = tay_red_600,
                        uiTextActiveColor = tay_red_600,
                        uiTitleActiveColor= tay_red_600,
                        uiTextFont = textM12
                    )
                )
            }
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Selecciona Área", style = textB14, color = Color.Black)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val areas = listOf("1" to "zona 1", "2" to "zona 2", "3" to "zona 3")
                        areas.forEach { (value, label) ->
                            val isSelected = uiState.area == value
                            Surface(
                                onClick = { viewModel.onRegisterFieldChange(area = value) },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) tay_red_600 else Color.White,
                                border = if (!isSelected) BorderStroke(1.dp, tay_red_600) else null
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = label, style = textB12, color = if (isSelected) Color.White else tay_red_600)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        UiTayEditLayout(
                            value = uiState.latitude,
                            onValueChange = { viewModel.onRegisterFieldChange(latitude = it) },
                            hint = "Latitud",
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor= tay_red_600,
                                uiTextFont = textM12
                            )
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        UiTayEditLayout(
                            value = uiState.longitude,
                            onValueChange = { viewModel.onRegisterFieldChange(longitude = it) },
                            hint = "Longitud",
                            model = UiEditLayoutModel(
                                uiStrokeActiveColor = tay_red_600,
                                uiTextColor = tay_red_600,
                                uiTextActiveColor = tay_red_600,
                                uiTitleActiveColor= tay_red_600,
                                uiTextFont = textM12
                            )
                        )
                    }
                }
            }
            item {
                val context = LocalContext.current
                Spacer(modifier = Modifier.height(16.dp))
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
        }
    }
}
