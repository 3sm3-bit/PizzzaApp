package com.pizzza.pizzzaapp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pizzza.pizzzaapp.core.navigation.ScreenInitNav
import com.pizzza.pizzzaapp.core.ui.R
import com.pizzza.pizzzaapp.feature.auth.AuthViewModel
import com.pizzza.pizzzaapp.ui.AppViewModel
import com.valu.uitaycompose.utils.tay_red_600
import com.valu.uitaycompose.utils.textGabbiB35
import com.valu.uitaycompose.utils.textSe16
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onNavigateTo: (ScreenInitNav) -> Unit
) {

    val  viewModel: AppViewModel = koinViewModel()
    val  authViewModel: AuthViewModel = koinViewModel()
    var error by remember { mutableStateOf<String?>(null) }
    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        
        delay(500.milliseconds)
        viewModel.syncProducts(
            onComplete = { success ->
                if (success) {
                    authViewModel.checkExistingUser { role ->
                        val flag = role?.equals("CLIENTE", ignoreCase = true) == true || role?.equals("ADMIN", ignoreCase = true) == true
                        onNavigateTo(if(flag) ScreenInitNav.ClientHome else  ScreenInitNav.Login)
                    }
                } else {
                    error = "Error al conectar con el servidor. Revisa tu conexión."
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(painter = painterResource(R.drawable.ic_logo_m_pizzzeria)
            , contentDescription = "null",modifier = Modifier.width(250.dp).height(200.dp))
            
            Spacer(Modifier.height(16.dp))

            Text(
                text = "PIZZZERIA",
                color =tay_red_600,
                style = textGabbiB35,
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(Modifier.height(40.dp))
            CircularProgressIndicator(
                    color = tay_red_600,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(36.dp)
                )
            Text(
                    text = "Cargando menú delicioso...",
                    style = textSe16,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 16.dp)
            )

        }
    }
}
