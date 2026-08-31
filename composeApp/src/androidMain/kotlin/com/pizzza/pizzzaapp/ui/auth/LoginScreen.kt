package com.pizzza.pizzzaapp.ui.auth

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.R
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
import com.valu.uitaycompose.utils.textB14
import com.valu.uitaycompose.utils.textB20
import com.valu.uitaycompose.utils.textM10
import com.valu.uitaycompose.utils.textM12
import com.valu.uitaycompose.utils.textM14

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToClientHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val uiState = viewModel.authUiState
    val isButtonEnabled = uiState.user.length > 2 && uiState.pass.length > 2

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Scaffold(
        topBar = {
            Surface(color = tay_red_50) {
                Box(modifier = Modifier.statusBarsPadding()) {
                    UiTayCToolBar(
                        uiTayText = "Pizzzeria 3 Z",
                        uiTayModifier = UiToolBarModel()
                            .backgroundColor(tay_red_50)
                            .textColor(tay_red_600)
                            .iconColor(Color.Transparent) // Sin icono atrás en Login
                    ) { }
                }
            }
        },
        containerColor = Color(0xFFF0F2F5)
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(painter = painterResource(R.drawable.ic_pizzza),
                contentDescription = "logo_ic",
                modifier = Modifier.size(150.dp).padding(bottom = 32.dp))

            UiTayEditLayout(
                value = uiState.user,
                onValueChange = { viewModel.onUserChange(it) },
                hint = "Usuario o email",
                model = UiEditLayoutModel(
                    uiStrokeActiveColor = tay_red_600,
                    uiTextColor = tay_red_600,
                    uiTextActiveColor = tay_red_600,
                    uiTitleActiveColor= tay_red_600,
                    uiTextFont = textM14,
                    uiTitleFont = textM14
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
                    uiIconColor = tay_grey_800,
                    uiIconActiveColor=  tay_red_600,
                    uiTextFont = textM14,
                    uiTitleFont = textM14
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

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
