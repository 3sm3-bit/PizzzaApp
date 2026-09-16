package com.pizzza.pizzzaapp.feature.auth.register

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateTo: (ScreenInitNav) -> Unit
) {
    val  viewModel: AuthViewModel = koinViewModel()
    val uiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
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
                    Surface(
                        onClick = {onNavigateTo(ScreenInitNav.AddressSelection)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
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
                                text = uiState.address.ifBlank { "Selecciona dirección en el mapa" },
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
                        viewModel.register { _ ->
                            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                            onNavigateTo(ScreenInitNav.Login)
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
