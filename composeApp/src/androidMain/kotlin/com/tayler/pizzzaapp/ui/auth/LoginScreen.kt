package com.tayler.pizzzaapp.ui.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.valu.uitaycompose.button.UiTayButton
import com.valu.uitaycompose.label.UiTayEditLayout
import com.valu.uitaycompose.model.UiEditLayoutModel
import com.valu.uitaycompose.model.UiTayButtonModel
import com.valu.uitaycompose.utils.tay_green_600
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM12

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToClientHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState = viewModel.authUiState
    val isButtonEnabled = uiState.user.length > 2 && uiState.pass.length > 2

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF0F2F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido de nuevo",
                style = textB20,
                color = tay_red_600,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            UiTayEditLayout(
                value = uiState.user,
                onValueChange = { viewModel.onUserChange(it) },
                hint = "Usuario o email",
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor= tay_red_600,
                    uiTitleFont = textM12
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            UiTayEditLayout(
                value = uiState.pass,
                onValueChange = { viewModel.onPassChange(it) },
                hint = "Contraseña",
                isPassword = true,
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor= tay_red_600,
                    uiIconColor = tay_red_600,
                    uiTitleFont = textM12
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            UiTayButton(
                uiTayText = "Iniciar Sesión",
                uiTayEnable = isButtonEnabled,
                uiTayClick = {
                    viewModel.login {
                        onNavigateToClientHome()
                    }
                },
                uiTayBtnModifier = UiTayButtonModel(
                    uTBgColor = tay_red_600,
                    uTStrokeColor = tay_red_600,
                    uTBgSelectedColor = tay_red_600,
                    uTStrokeSelectedColor = tay_red_600,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "registrate ahora",
                style = textB14.copy(textDecoration = TextDecoration.Underline),
                color = tay_green_600,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
